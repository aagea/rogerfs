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

import org.rogerfs.common.store.{File, Path}
import org.rogerfs.test.store.TestStore
import org.scalatest.WordSpec

class FileSystemSpec extends WordSpec{
  val store = new TestStore
  val fs = FileSystem.mount(store)

  "A file" when {
    "is created with a path(string) " should {
      val file = fs.createFile("/abc/def")
      "must exist" in {
        assert(store.files.contains("/abc/def"))
        assert(file.path.getPath.equals("/abc/def"))
      }
    }
    "is created with a path(PathBase) " should {
      val file = fs.createFile(Path.getPath("/abc1/def1"))
      "must exist" in {
        assert(store.files.contains("/abc1/def1"))
        assert(file.path.getPath.equals("/abc1/def1"))
      }
    }
    "is created with a file " should {
      val fileOri = new File(Path.getPath("/abc2/def2"))
      val file = fs.createFile(fileOri)
      "must exist" in {
        assert(store.files.contains(fileOri.path.getPath))
        assert(file.path.getPath.equals(fileOri.path.getPath))
      }
    }
  }
}
