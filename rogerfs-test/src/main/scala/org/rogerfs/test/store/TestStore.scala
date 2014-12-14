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

import org.rogerfs.common.store.{File, IStore}


class TestStore extends IStore{

  private class Block(val id:UUID, val data:Array[Array[Byte]]=new Array[Array[Byte]](getMaxOffset),
                      var nextBlock:UUID=null)

  private val files:util.Map[File,util.Map[UUID,Block]]=new util.HashMap[File,util.Map[UUID,Block]]()

  override def createFile(file: File): Unit = {
    files.put(file,new util.HashMap[UUID,Block])
  }
  override def openBlock(file: File): UUID = {
    val blocks= files.get(file)
    val uuid=UUID.randomUUID()
    blocks.put(uuid,new Block(uuid))
    uuid
  }

  override def addData(file: File, uuid: UUID, data: Array[Byte], offset: Int): Unit = {
    val mapBlocks= files.get(file)
    val block= mapBlocks.get(uuid)
    block.data(offset)=data
  }

  override def closeBlock(file: File, uuid: UUID, nextBlock: UUID): Unit = {
    val mapBlocks= files.get(file)
    val block= mapBlocks.get(uuid)
    block.nextBlock=nextBlock
  }

  override def getData(file: File, uuid: UUID, offset: Int): Array[Byte] = {
    val mapBlocks= files.get(file)
    val block= mapBlocks.get(uuid)
    block.data(offset)
  }

  override def getBlocks(file: File): Array[UUID] = {
    val mapBlocks= files.get(file)
    val blocks:Array[UUID]=new Array[UUID](mapBlocks.size())
    mapBlocks.keySet().toArray(blocks)
    blocks
  }

  override def getFiles(pathDirectory: String): Array[File] = {
    val filesArr:Array[File]=new Array[File](files.size())
    files.keySet().toArray(filesArr)
    filesArr
  }

  override def getMaxSizeData: Int = 16

  override def getMaxOffset: Int = 8

  def existFile(file:File): Boolean ={
    files.containsKey(file)
  }
  def existBlock(file:File,block:UUID):Boolean={
    existFile(file) && files.get(file).containsKey(block)
  }

}
