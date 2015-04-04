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

package org.rogerfs.common.store

object File {
  def getFile(path:String):File={
    File(Path.getPath(path))
  }
}
case class File(path: IPath)


object Path {
  private val PATH_REG_EXP = """^(?:\/[A-Za-z0-9_\-\.]+)+\/?$"""

  def getPath(filePath: String): IPath = {
    if (isValid(filePath)) {
      val path = normalize(filePath)
      val nodes = path.split("/").dropWhile(str => str.isEmpty)
      val name = nodes.last
      val parent = nodes.init.mkString("/", "/", "")
      new InternalPath(path, name, parent)
    } else {
      throw new InvalidPathException("PATH is not valid!!")
    }
  }

  def normalize(filePath: String): String = {
    val lowercaseFilePath = filePath.toLowerCase

    val removeSlashFilePath = if (lowercaseFilePath.last == '/') {
      lowercaseFilePath.substring(0, lowercaseFilePath.length - 1)
    } else {
      lowercaseFilePath
    }

    removeSlashFilePath
  }

  def isValid(filePath: String): Boolean = {
    PATH_REG_EXP.r.findFirstIn(filePath) != None
  }

  class InternalPath(val path: String, val name: String, val parent: String) extends IPath {
    override def getName: String = name

    override def getParent: String = parent

    override def getPath: String = path

    override def toString: String = path
  }

}

class InvalidPathException(msg: String) extends RuntimeException(msg)


