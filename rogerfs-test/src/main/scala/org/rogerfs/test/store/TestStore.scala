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

package org.rogerfs.test.store

import java.util
import java.util.UUID

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.rogerfs.common.store.IPath
import org.rogerfs.common.store.IStore
import org.rogerfs.common.store.RawData

import scala.collection.JavaConversions._


class TestStore extends IStore {

  private class Block(val id: UUID,
                      val data: Array[Array[Byte]] = new Array[Array[Byte]](getMaxSubBlocks),
                      var nextBlock: UUID = null)

  private val files: util.Map[String, util.Map[UUID, Block]] =
    new util.HashMap[String, util.Map[UUID, Block]]()

  val config= new SparkConf().setMaster("local[8]")
    .setAppName("TestStoreRdd")

  lazy val sc = new SparkContext(config)

  override def createFile(file: IPath): Unit = {
    files.put(file.getPath, new util.HashMap[UUID, Block])
  }

  override def openBlock(file: IPath): UUID = {
    val blocks = files.get(file.getPath)
    val uuid = UUID.randomUUID()
    blocks.put(uuid, new Block(uuid))
    uuid
  }

  override def addData(file: IPath, uuid: UUID, data: Array[Byte], offset: Int): Unit = {
    val mapBlocks = files.get(file.getPath)
    val block = mapBlocks.get(uuid)
    block.data(offset) = data
  }

  override def closeBlock(file: IPath, uuid: UUID, nextBlock: UUID): Unit = {
    val mapBlocks = files.get(file.getPath)
    val block = mapBlocks.get(uuid)
    block.nextBlock = nextBlock
  }

  override def getData(file: IPath, uuid: UUID, offset: Int): Array[Byte] = {
    val mapBlocks = files.get(file.getPath)
    val block = mapBlocks.get(uuid)
    block.data(offset)
  }

  override def getBlocks(file: IPath): util.SortedMap[UUID, UUID] = {
    val mapBlocks = files.get(file.getPath)
    val result = new util.TreeMap[UUID, UUID]()
    mapBlocks.entrySet().foreach(x => result.put(x.getKey, x.getValue.nextBlock))
    result
  }

  override def getFiles(pathDirectory: IPath): Array[IPath] = {
    val filesArr: Array[IPath] = new Array[IPath](files.size())
    files.keySet().toArray(filesArr).filter(x=>x.getParent == pathDirectory.getPath)
    filesArr
  }

  override def getMaxSizeData: Int = 16

  override def getMaxSubBlocks: Int = 8

  def existFile(file: IPath): Boolean = {
    files.containsKey(file.getPath)
  }

  def existBlock(file: IPath, block: UUID): Boolean = {
    existFile(file) && files.get(file.getPath).containsKey(block)
  }

  private def getAllData(iPath: IPath,uuid: UUID):Array[Byte]={
    var result=List[Byte]()
    0.until(this.getMaxSubBlocks).foreach(i=>{
      result = result ::: this.getData(iPath,uuid,i).toList
    })
    result.toArray
  }

  override def getRdd(directory: IPath): RDD[RawData] = {
    val files= this.getFiles(directory)
    val data=files.flatMap(x=>this.getBlocks(file = x)
      .map(y=>new RawData(x,y._1,this.getAllData(x,y._2),y._2)))
    sc.parallelize(data)
  }
}
