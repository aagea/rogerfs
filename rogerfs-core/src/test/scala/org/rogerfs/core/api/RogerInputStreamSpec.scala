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

import org.rogerfs.common.store.IStore
import org.rogerfs.common.store.Path
import org.rogerfs.test.store.TestStore
import org.scalatest.WordSpec

class RogerInputStreamSpec extends WordSpec {

  val store: IStore = new TestStore
  val file = Path.getPath("/a/b/c")
  store.createFile(file)
  val block1 = store.openBlock(file)
  store.addData(file, block1,
    Array[Byte](0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), 0)
  store.addData(file, block1,
    Array[Byte](16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31), 1)
  val block2 = store.openBlock(file)
  store.closeBlock(file, block1, block2)
  store.addData(file, block2,
    Array[Byte](0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), 0)
  store.addData(file, block2,
    Array[Byte](16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31), 1)
  store.closeBlock(file, block2, null)


  val fileSystem = FileSystem.mount(store)

  val inputStream = fileSystem.readFile(file)
  val data = new Array[Byte](8)

  "A RogerInputStream" when {
    "read Sequential bytes in multiples blocks" must {

      "not fail" in {
        inputStream.read(data)
        assertResult(7)(data(7))

        inputStream.read(data)
        assertResult(15)(data(7))

        inputStream.read(data)
        assertResult(23)(data(7))

        inputStream.read(data)
        assertResult(31)(data(7))

        inputStream.read(data)
        assertResult(7)(data(7))

        inputStream.read(data)
        assertResult(15)(data(7))

        inputStream.read(data)
        assertResult(23)(data(7))

        inputStream.read(data)
        assertResult(31)(data(7))

      }
    }
  }
}
