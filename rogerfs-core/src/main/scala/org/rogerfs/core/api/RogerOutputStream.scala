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

package org.rogerfs.core.api

import java.io.OutputStream

import org.rogerfs.common.store._
import org.rogerfs.common.utils.UUIDGen

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
