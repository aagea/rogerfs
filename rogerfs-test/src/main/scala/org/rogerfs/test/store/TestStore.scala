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
import scala.collection.JavaConversions._

import org.rogerfs.common.store._

import scala.collection.mutable

class TestStore extends IStore{
  val files= mutable.HashMap.empty[String,File]
  val partitions = mutable.HashMap.empty[UUID, Partition]
  val blocks = mutable.HashMap.empty[UUID, Block]
  val subBlocks= mutable.HashMap.empty[UUID, SubBlock]

  override def createFile(file: File): Unit = {
    files += (file.path.toString -> file)
  }

  override def createSubBlock(subBlock: SubBlock): Unit = {
    subBlocks += (subBlock.uuid -> subBlock)
  }

  override def getSizeBlock: Int = 8

  override def createBlock(block: Block): Unit = {
    blocks += (block.uuid -> block)
  }

  override def createPartition(partition: Partition): Unit = {
    partitions += (partition.uuid -> partition)
  }

  override def getSizeSubBlock: Int = 100

  override def getFiles(pathParent: String): util.List[File] = {
    files.values.filter(file => file.path.getParent.equals(pathParent)).toList
  }

  override def getSubBlocks(uuidBlock: UUID): util.List[SubBlock] = {
    subBlocks.values.filter(subBlock => subBlock.block.uuid.equals(uuidBlock)).toList
  }

  override def getBlocks(uuidPartition: UUID): util.List[Block] = {
    blocks.values.filter(block => block.partition.uuid.equals(uuidPartition)).toList
  }

  override def getPartitions(pathFile: String): util.List[Partition] = {
    partitions.values.filter(partition => partition.file.path.toString.equals(pathFile)).toList
  }
}
