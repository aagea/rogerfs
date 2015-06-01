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

import org.rogerfs.common.store.IPath
import org.rogerfs.common.store.IStore

import scala.collection.JavaConversions._


class TestStore extends IStore {

  private class Block(val id: UUID,
                      val data: Array[Array[Byte]] = new Array[Array[Byte]](getMaxSubBlocks),
                      var nextBlock: UUID = null)

  private val files: util.Map[String, util.Map[UUID, Block]] = new util.HashMap[String, util
  .Map[UUID,
    Block]]()

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

}
