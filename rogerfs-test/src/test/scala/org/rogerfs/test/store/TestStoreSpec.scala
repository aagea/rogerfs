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

import org.rogerfs.common.store._
import org.rogerfs.common.utils.UUIDGen
import org.scalatest.WordSpec

class TestStoreSpec extends WordSpec{
  val store = new TestStore
  val path = Path.getPath("/abc/def")
  val file = new File(path)
  val partition = new Partition(file, UUIDGen.getTimeUUID)
  val block = new Block(partition, UUIDGen.getTimeUUID)
  val subBlock = new SubBlock(block, UUIDGen.getTimeUUID, Array[Byte](1, 3, 4))

  "A file" when {
    "is created " should {
      store.createFile(file)
      "must exist" in{
        assert(store.files.contains(file.path.toString))
      }
    }
  }
  "A partition" when {
    "is created " should {
      store.createPartition(partition)
      "must exist" in{
        assert(store.partitions.contains(partition.uuid))
      }
    }
  }
  "A block" when {
    "is created " should {
      store.createBlock(block)
      "must exist" in{
        assert(store.blocks.contains(block.uuid))
      }
    }
  }
  "A sub-block" when {
    "is created " should {
      store.createSubBlock(subBlock)
      "must exist" in{
        assert(store.subBlocks.contains(subBlock.uuid))
      }
    }
  }

}
