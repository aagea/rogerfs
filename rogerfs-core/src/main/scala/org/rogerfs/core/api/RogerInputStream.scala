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

class RogerInputStream(val store: IStore, val file: IPath) extends InputStream {


  val blocks: util.SortedMap[UUID, UUID] = store.getBlocks(file)


  var currentBlock: UUID = null

  var currentSubBlock: Int = 0

  var currentPos: Int = 0

  var currentData: Array[Byte] = null

  var bof = true
  var eof = false


  override def read(): Int = {
    if (eof) {
      -1
    } else {
      if (bof || currentPos >= currentData.length - 1) {
        currentPos = 0
        nextSubBlock
        bof = false
      } else {
        currentPos += 1
      }
      if (eof) {
        -1
      } else {
        currentData(currentPos)
      }
    }
  }

  private def getData: Array[Byte] = {
    store.getData(file, currentBlock, currentSubBlock)
  }

  private def nextSubBlock = {

    do {
      if (!bof) {
        currentSubBlock += 1
      }
      if (currentData == null || currentSubBlock >= store.getMaxSubBlocks) {
        this.nextBlock
        currentSubBlock = 0
      }
      if (eof) {
        currentData = null
      } else {
        currentData = getData
      }
    } while (currentData == null && !eof)

  }

  private def nextBlock: Unit = {
    if (blocks.size() == 0) {
      eof = true
    } else if (this.currentBlock == null) {
      currentBlock = blocks.firstKey()
    } else {
      val next = blocks.get(this.currentBlock)
      blocks.remove(this.currentBlock)
      if (next == null) {
        currentBlock = null
        nextBlock
      } else {
        this.currentBlock = next
      }
    }

  }
}
