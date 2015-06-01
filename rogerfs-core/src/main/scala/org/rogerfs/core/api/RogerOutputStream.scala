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
import java.util.UUID

import org.rogerfs.common.store._

import scala.collection.mutable.ArrayBuffer


class RogerOutputStream(val store: IStore, val file: IPath) extends OutputStream {


  val buffer: ArrayBuffer[Byte] = new ArrayBuffer[Byte](store.getMaxSizeData)

  var currentBlock: UUID = store.openBlock(file)
  var currentSubBlock: Int = 0


  override def flush() {
    if (buffer.nonEmpty) {
      store.addData(file, currentBlock, buffer.toArray, currentSubBlock)
      currentSubBlock += 1
      if (currentSubBlock >= store.getMaxSubBlocks) {
        val newBlock = store.openBlock(file)
        store.closeBlock(file, currentBlock, newBlock)
        currentBlock = newBlock
        currentSubBlock = 0
      }
    }
  }

  override def write(b: Int): Unit = {
    buffer += b.asInstanceOf[Byte]
    if (buffer.size >= store.getMaxSizeData) {
      this.flush()
      buffer.clear()
    }
  }

  override def close() = {
    this.flush()
    store.closeBlock(file, currentBlock, null)
    super.close()
  }
}
