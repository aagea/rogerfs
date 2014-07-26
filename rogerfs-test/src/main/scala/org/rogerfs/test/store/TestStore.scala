package org.rogerfs.test.store

import java.util.UUID

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

  override def createSubBlock(subBlock: SubBlock): Unit = {}

  override def getSizeBlock: Int = 8

  override def createBlock(block: Block): Unit = {
    blocks += (block.uuid -> block)
  }

  override def createPartition(partition: Partition): Unit = {
    partitions += (partition.uuid -> partition)
  }

  override def getSizeSubBlock: Int = 100
}
