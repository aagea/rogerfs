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

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import org.rogerfs.common.store.IPath
import org.rogerfs.common.store.IStore
import org.rogerfs.common.store.Path
import org.rogerfs.common.utils.UUIDGen

import scala.collection.JavaConversions._

case class CassandraStoreConfig(node: String = CassandraStore.DEFAULT_NODE,
                                port: Int = CassandraStore.DEFAULT_PORT,
                                keyspace: String = CassandraStore.DEFAULT_KEYSPACE,
                                maxSubBlock: Int = CassandraStore.DEFAULT_MAX_SUBBLOCK,
                                maxSizeData: Int = CassandraStore.DEFAULT_MAX_SIZE_DATA)

object CassandraStore {

  val TABLE_NAME = "filesystem"
  val DEFAULT_NODE = "localhost"
  val DEFAULT_PORT = 9160
  val DEFAULT_KEYSPACE = "localhost"
  val DEFAULT_MAX_SUBBLOCK = 100
  val DEFAULT_MAX_SIZE_DATA = 8388608
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


  def getInstance(config: CassandraStoreConfig): IStore = {
    val cluster: Cluster = Cluster.builder().addContactPoint(config.node)
      .withPort(config.port).build()
    val session: Session = cluster.connect(config.keyspace)
    session.execute(CREATE_TABLE)
    session.execute(PATH_INDEX)
    session.execute(PARENT_INDEX)


    new CassandraStore(session, config.maxSubBlock, config.maxSizeData)
  }

  private class CassandraStore(session: Session, maxSubBlock: Int, maxSizeData: Int) extends IStore {

    override def createFile(file: IPath): Unit = {
      val query = QueryBuilder.insertInto(TABLE_NAME).value("path", file.getPath)
        .value("parent", file.getParent).value("block", UUIDGen.getTimeUUID)
        .value("subblock", 0).getQueryString
      session.execute(query)
    }

    override def getData(file: IPath, block: UUID, subBlock: Int): Array[Byte] = {
      val query = QueryBuilder.select("data").from(TABLE_NAME).where()
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

    override def getMaxSizeData: Int = maxSizeData

    override def getMaxSubBlocks: Int = maxSubBlock

    override def getBlocks(file: IPath): util.SortedMap[UUID, UUID] = {
      val result: util.SortedMap[UUID, UUID] = new util.TreeMap[UUID, UUID]()
      val query = QueryBuilder.select("block", "nextBlock").from(TABLE_NAME)
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
      val query = QueryBuilder.insertInto(TABLE_NAME).value("path", file.getPath)
        .value("parent", file.getParent).value("block", uuid)
        .value("subblock", 0).getQueryString
      session.execute(query)
      uuid
    }

    override def addData(file: IPath, block: UUID, data: Array[Byte], subBlock: Int): Unit = {
      val buffer: ByteBuffer = ByteBuffer.wrap(data)
      val query = QueryBuilder.insertInto(TABLE_NAME).value("path", file.getPath)
        .value("parent", file.getParent).value("block", block)
        .value("subblock", subBlock).value("data", buffer).getQueryString
      session.execute(query)
    }

    override def closeBlock(file: IPath, block: UUID, nextBlock: UUID): Unit = {
      val query = QueryBuilder.update(TABLE_NAME).`with`(QueryBuilder.set("nextBlock", nextBlock))
        .where(QueryBuilder.eq("path", file.getPath))
        .and(QueryBuilder.eq("parent", file.getParent))
        .and(QueryBuilder.eq("block", block)).getQueryString
      session.execute(query)
    }

    override def getFiles(pathDirectory: IPath): Array[IPath] = {
      val result: util.List[IPath] = new util.ArrayList[IPath]()
      val query = QueryBuilder.select("path").from(TABLE_NAME)
        .where(QueryBuilder.eq("parent", pathDirectory.getPath))
        .getQueryString
      val rows: ResultSet = session.execute(query)

      rows.foreach(row => {
        val filename = row.getString("filename")
        result.add(Path.getPath(filename))
      })
      result.toArray[IPath](new Array[IPath](result.size()))
    }
  }

}


