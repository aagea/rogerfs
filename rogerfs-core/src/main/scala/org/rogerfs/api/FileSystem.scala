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

package org.rogerfs.api

import org.rogerfs.common.store._
import org.rogerfs.common.utils.UUIDGen


object FileSystem{
  def mount(store:IStore):IFileSystem={
    new FileSystem(store)
  }

  private class FileSystem(store:IStore) extends IFileSystem{

    def createFile(path: String): File = {
      createFile(Path.getPath(path))
    }

    def createFile(path:PathBase): File = {
      createFile(File(path))
    }

    def createFile(file:File): File = {
      store.createFile(file)
      file
    }

    def WriteFile(file:File):RogerOutputStream = {
      val partition:Partition=new Partition(file,UUIDGen.getTimeUUID)
      store.createPartition(partition)
      new RogerOutputStream(store,partition)
    }

  }

}


