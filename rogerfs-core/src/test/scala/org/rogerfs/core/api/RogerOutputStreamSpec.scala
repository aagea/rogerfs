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

import org.rogerfs.test.store.TestStore
import org.scalatest.WordSpec
import scala.collection.JavaConversions._

class RogerOutputStreamSpec extends WordSpec{
  val store = new TestStore
  val fs = FileSystem.mount(store)

  "A RogerOutputStream" when {
    "write a byte not flush" should{
      val file=fs.createFile("/abc/def1.file")
      val os=fs.writeFile(file)
      os.write(42)
      "must be empty" in {
        val subBlocks = store.getSubBlocks(os.currentBlock.uuid)
        assert(subBlocks.isEmpty)
        assert(os.currentNumberOfSubBlockWrite==0)
      }
    }

    "write a byte and flush" should{
      val file=fs.createFile("/abc/def1.file")
      val os=fs.writeFile(file)
      os.write(42)
      os.flush()
      "must not be empty" in {
        val subBlocks = store.getSubBlocks(os.currentBlock.uuid)
        assert(subBlocks.nonEmpty)
        assert(os.currentNumberOfSubBlockWrite==1)
      }
    }

    "write a byte and close" should{
      val file=fs.createFile("/abc/def1.file")
      val os=fs.writeFile(file)
      os.write(42)
      os.close()
      "must not be empty" in {
        val subBlocks = store.getSubBlocks(os.currentBlock.uuid)
        assert(subBlocks.nonEmpty)
        assert(os.currentNumberOfSubBlockWrite==1)
      }
    }



  }

}
