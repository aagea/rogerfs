/*
 * Copyright (c) 2014 Alvaro Agea.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.rogerfs.test.store

import java.util.UUID

import org.rogerfs.common.store._
import org.scalatest.WordSpec

class TestStoreSpec extends WordSpec{
  val store = new TestStore
  val path = Path.getPath("/abc/def")
  val file = new File(path)
  var currentBlock:UUID= null

  "A file" when {
    "is created " should {
      store.createFile(file)
      "must exist" in{
        assert(store.existFile(file))
      }
    }
  }
  "A block" when {
    "is open " should {
      currentBlock = store.openBlock(file)
      "must exist" in{
        assert(store.existBlock(file,currentBlock))
      }
    }
  }
  "Some data" when {
    "is added " should {
      store.addData(file,currentBlock, Array[Byte](1,2,3,4),0)
      "must exist" in{
        assert(store.getData(file,currentBlock,0)!=null)
      }
    }
  }

}
