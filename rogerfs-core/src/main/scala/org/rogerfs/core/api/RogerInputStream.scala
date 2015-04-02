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

import java.io.InputStream
import java.util
import java.util.UUID

import org.rogerfs.common.store._

object RogerInputStream {
  val BOF:Int = -1
}

class RogerInputStream(val store: IStore, val file: File) extends InputStream {




  val blocks:util.SortedMap[UUID,UUID]= store.getBlocks(file)


  var currentBlock: UUID = nextBlock(null)

  var currentSubBlock:Int = 0

  var currentPos: Int = RogerInputStream.BOF

  var currentData:Array[Byte]= getData()




  override def read(): Int = {
    if(currentPos >= store.getMaxSizeData-1){
      currentPos = RogerInputStream.BOF
      currentSubBlock +=1
      if(currentSubBlock>store.getMaxSubBlocks){
        currentSubBlock = 0
        currentBlock = this.nextBlock(currentBlock)
      }
      currentData=getData();
    }
    currentPos+=1
    currentData(currentPos)
  }

  def getData():Array[Byte] ={
    store.getData(file,currentBlock,currentSubBlock)
  }

  def nextBlock(current:UUID):UUID = {
      if(current==null){
        blocks.firstKey()
      }else {
        val next = blocks.get(current)
        blocks.remove(current)
        if (next == null) {
          blocks.firstKey()
        } else {
          next
        }
      }

  }
}
