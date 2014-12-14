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

import org.rogerfs.common.store.{Path, File, IStore}
import org.rogerfs.test.store.TestStore
import org.scalatest.WordSpec

class RogerOutputStreamSpec extends WordSpec{
  val store:IStore = new TestStore
  val fs = FileSystem.mount(store)

  "A RogerOutputStream" when {
    "write a byte not flush" should{
      val file=fs.createFile(new File(Path.getPath("/abc/def1.file")))
      val os=fs.writeFile(file)
      os.write(42)
      "must be empty" in {
        val subBlocks = store.getData(file,os.currentBlock,0)
        assert(subBlocks==null)
        assert(os.currentOffset==0)
      }
    }

    "write a byte and flush" should{
      val file=fs.createFile(new File(Path.getPath("/abc/def1.file")))
      val os=fs.writeFile(file)
      os.write(42)
      os.flush()
      "must not be empty" in {
        val subBlocks = store.getData(file,os.currentBlock,0)
        assert(subBlocks.nonEmpty)
        assert(os.currentOffset==1)
      }
    }

    "write a byte and close" should{
      val file=fs.createFile(new File(Path.getPath("/abc/def2.file")))
      val os=fs.writeFile(file)
      os.write(42)
      os.close()
      "must not be empty" in {
        val subBlocks = store.getData(file,os.currentBlock,0)
        assert(subBlocks.nonEmpty)
        assert(os.currentOffset==1)
      }
    }
    "write an array byte and flush" should{
      val file =fs.createFile(new File(Path.getPath("/abc/def3.file")))
      val os =fs.writeFile(file)
      os.write(Array[Byte](1,2,3))
      os.flush()
      "must not be empty" in {
        val subBlocks = store.getData(file,os.currentBlock,0)
        assert(subBlocks.nonEmpty)
        assert(os.currentOffset==1)
      }
      "must contains three bytes" in{
        val data = store.getData(file,os.currentBlock,0)
        assert(data.length==3)
      }
    }



  }

}
