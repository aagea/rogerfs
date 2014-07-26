package org.rogerfs.api

import java.io.OutputStream

import org.rogerfs.utils.UUIDGen

import scala.collection.mutable.ArrayBuffer


class RogerOutputStream(val store:IStore, val partition:Partition) extends OutputStream{


  val buffer:ArrayBuffer[Byte]=new ArrayBuffer[Byte](store.getSizeSubBlock)

  var currentBlock:Block=new Block(partition,UUIDGen.getTimeUUID)
  var currentNumberOfSubBlockWrite:Int=0
  store.createBlock(currentBlock)



  override def flush(){
    val subBlock:SubBlock=new SubBlock(currentBlock,UUIDGen.getTimeUUID,buffer.toArray)
    store.createSubBlock(subBlock)
    currentNumberOfSubBlockWrite += 1
    if(currentNumberOfSubBlockWrite >= store.getSizeBlock){
      val block:Block=new Block(partition,UUIDGen.getTimeUUID)
      store.createBlock(block)
      currentBlock=block
      currentNumberOfSubBlockWrite=0
    }
  }

  override def write(b: Int): Unit = {
    buffer += b.asInstanceOf[Byte]
    if(buffer.size>=store.getSizeSubBlock){
      this.flush()
      buffer.clear()
    }
  }
}
