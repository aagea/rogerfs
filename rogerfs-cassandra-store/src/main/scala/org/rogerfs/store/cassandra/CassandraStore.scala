/*
 * Copyright (c) 2014 Alvaro Agea.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rogerfs.store.cassandra

import java.nio.ByteBuffer
import java.util
import java.util.UUID

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.spark.connector._
import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.rogerfs.common.store.IPath
import org.rogerfs.common.store.IStore
import org.rogerfs.common.store.Path
import org.rogerfs.common.store.RawData
import org.rogerfs.common.utils.UUIDGen

import scala.collection.JavaConversions._

object CassandraStoreConfig{
  val CONF_NODE = "rogerfs.cassandra.node"
  val CONF_PORT = "rogerfs.cassandra.port"
  val CONF_KEYSPACE = "rogerfs.cassandra.keyspace"
  val CONF_MAX_SUBBLOCK = "rogerfs.cassandra.max-subblock"
  val CONF_MAX_SIZE_DATA = "rogerfs.cassandra.max-size-data"
  val CONF_SPARK_MASTER = "rogerfs.cassandra.spark-master"

  def getDefaultConfig():CassandraStoreConfig={
    val conf= ConfigFactory.load()
    val node=conf.getString(CONF_NODE)
    val port=conf.getInt(CONF_PORT)
    val keyspace=conf.getString(CONF_KEYSPACE)
    val maxSubBlock=conf.getInt(CONF_MAX_SUBBLOCK)
    val maxSizeData=conf.getInt(CONF_MAX_SIZE_DATA)
    val sparkMaster=conf.getString(CONF_SPARK_MASTER)
    CassandraStoreConfig(node,port,keyspace,maxSubBlock,maxSizeData,sparkMaster)
  }

}

case class CassandraStoreConfig(node: String = CassandraStore.DEFAULT_NODE,
                                port: Int = CassandraStore.DEFAULT_PORT,
                                keyspace: String = CassandraStore.DEFAULT_KEYSPACE,
                                maxSubBlock: Int = CassandraStore.DEFAULT_MAX_SUBBLOCK,
                                maxSizeData: Int = CassandraStore.DEFAULT_MAX_SIZE_DATA,
                                sparkMaster: String = CassandraStore.DEFAULT_SPARK_MASTER)

object CassandraStore {

  val TABLE_NAME = "filesystem"
  val DEFAULT_NODE = "localhost"
  val DEFAULT_PORT = 9160
  val DEFAULT_KEYSPACE = "rogerfs"
  val DEFAULT_MAX_SUBBLOCK = 100
  val DEFAULT_MAX_SIZE_DATA = 8388608
  val DEFAULT_SPARK_MASTER = "local[8]"


  val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS filesystem (" +
    "path text," +
    "parent text ," +
    "block uuid," +
    "subblock int," +
    "nextBlock uuid static," +
    "data blob" +
    "PRIMARY KEY ((path, parent, block),subblock)" +
    ");"

  val PATH_INDEX = "CREATE INDEX IF NOT EXISTS path_index ON filesystem (path);"
  val PARENT_INDEX = "CREATE INDEX IF NOT EXISTS parent_index ON filesystem (parent);"


}

case class CassandraStore(config: CassandraStoreConfig=CassandraStoreConfig.getDefaultConfig())
  extends IStore {
  val cluster: Cluster = Cluster.builder().addContactPoint(config.node)
    .withPort(config.port).build()
  val session: Session = {
    val ses = cluster.connect(config.keyspace)
    ses.execute(CassandraStore.CREATE_TABLE)
    ses.execute(CassandraStore.PATH_INDEX)
    ses.execute(CassandraStore.PARENT_INDEX)
    ses
  }

  val sparkConf = new SparkConf().setMaster(config.sparkMaster)
    .set("spark.cassandra.connection.host", config.node)
    .set("spark.cassandra.connection.native.port", config.port.toString)

  lazy val sc = new SparkContext(sparkConf)


  override def createFile(file: IPath): Unit = {
    val query = QueryBuilder.insertInto(CassandraStore.TABLE_NAME).value("path", file.getPath)
      .value("parent", file.getParent).value("block", UUIDGen.getTimeUUID)
      .value("subblock", 0).getQueryString
    session.execute(query)
  }

  override def getData(file: IPath, block: UUID, subBlock: Int): Array[Byte] = {
    val query = QueryBuilder.select("data").from(CassandraStore.TABLE_NAME).where()
      .and(QueryBuilder.eq("path", file.getPath))
      .and(QueryBuilder.eq("parent", file.getParent))
      .and(QueryBuilder.eq("block", block))
      .and(QueryBuilder.eq("subblock", subBlock))
      .getQueryString
    val results: ResultSet = session.execute(query)
    if (results.isExhausted) {
      null
    } else {
      val bufferData = results.one().getBytes("data")
      val data: Array[Byte] = new Array[Byte](bufferData.capacity())
      bufferData.get(data)
      data
    }
  }

  override def getMaxSizeData: Int = config.maxSizeData

  override def getMaxSubBlocks: Int = config.maxSubBlock

  override def getBlocks(file: IPath): util.SortedMap[UUID, UUID] = {
    val result: util.SortedMap[UUID, UUID] = new util.TreeMap[UUID, UUID]()
    val query = QueryBuilder.select("block", "nextBlock").from(CassandraStore.TABLE_NAME)
      .where(QueryBuilder.eq("path", file)).orderBy(QueryBuilder.asc("block"))
    val rows = session.execute(query)
    rows.foreach(row => {
      val block = row.getUUID("block")
      val nextBlock = row.getUUID("nextBlock")
      result.put(block, nextBlock)
    })
    result
  }

  override def openBlock(file: IPath): UUID = {
    val uuid = UUIDGen.getTimeUUID
    val query = QueryBuilder.insertInto(CassandraStore.TABLE_NAME).value("path", file.getPath)
      .value("parent", file.getParent).value("block", uuid)
      .value("subblock", 0).getQueryString
    session.execute(query)
    uuid
  }

  override def addData(file: IPath, block: UUID, data: Array[Byte], subBlock: Int): Unit = {
    val buffer: ByteBuffer = ByteBuffer.wrap(data)
    val query = QueryBuilder.insertInto(CassandraStore.TABLE_NAME).value("path", file.getPath)
      .value("parent", file.getParent).value("block", block)
      .value("subblock", subBlock).value("data", buffer).getQueryString
    session.execute(query)
  }

  override def closeBlock(file: IPath, block: UUID, nextBlock: UUID): Unit = {
    val query = QueryBuilder.update(CassandraStore.TABLE_NAME)
      .`with`(QueryBuilder.set("nextBlock", nextBlock))
      .where(QueryBuilder.eq("path", file.getPath))
      .and(QueryBuilder.eq("parent", file.getParent))
      .and(QueryBuilder.eq("block", block)).getQueryString
    session.execute(query)
  }

  override def getFiles(pathDirectory: IPath): Array[IPath] = {
    val result: util.List[IPath] = new util.ArrayList[IPath]()
    val query = QueryBuilder.select("path").from(CassandraStore.TABLE_NAME)
      .where(QueryBuilder.eq("parent", pathDirectory.getPath))
      .getQueryString
    val rows: ResultSet = session.execute(query)

    rows.foreach(row => {
      val filename = row.getString("filename")
      result.add(Path.getPath(filename))
    })
    result.toArray[IPath](new Array[IPath](result.size()))
  }

  override def getRdd(directory: IPath): RDD[RawData] = {
    sc.cassandraTable(config.keyspace, CassandraStore.TABLE_NAME)
      .select("path", "block", "nextBlock", "subblock", "data")
      .where("parent = ?", directory.getPath).spanBy(row => (row.getString("path"),
      row.getString("parent"), row.getUUID("block")))
      .map(row => new RawData(Path.getPath(row._1._1), row._1._3,
      row._2.foldLeft(List[Byte]())
        ((acc, curr) => acc ::: curr.getBytes("data").array().toList).toArray,
      row._2.head.getUUID("nextBlock")))
  }

}



