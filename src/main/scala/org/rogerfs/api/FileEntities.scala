package org.rogerfs.api

import java.util.UUID

case class File(path:IPath)

case class Partition(file:File, partition: UUID)

case class Block(partition:Partition, block: UUID)

case class SubBlock(block:Block, subBlock: UUID, data: Array[Byte])

object Path {
  private val PATH_REG_EXP = """^(?:\/[A-Za-z0-9_\-\.]+)+\/?$"""

  def getPath(filePath: String): IPath = {
    if (isValid(filePath)) {
      val path = normalize(filePath)
      val nodes = path.split("/")
      val name= nodes.last
      val parent= nodes.dropWhile(str=>str.isEmpty).init.mkString("/","/","")
      new InternalPath(path,name,parent)
    } else {
      throw new InvalidPathException("PATH is not valid!!")
    }
  }

  def normalize(filePath: String):String = {
    val lowercaseFilePath = filePath.toLowerCase

    val removeSlashFilePath = if (lowercaseFilePath.last == '/') {
      lowercaseFilePath.substring(0, lowercaseFilePath.length - 1)
    } else { lowercaseFilePath }

    removeSlashFilePath
  }

  def isValid(filePath: String): Boolean = {
    PATH_REG_EXP.r.findFirstIn(filePath) != None
  }

  class InternalPath(val path: String, val name: String, val parent: String) extends IPath {
    override def getName: String = name

    override def getParent: String = parent

    override def getPath: String = path
  }

}

class InvalidPathException(msg: String) extends RuntimeException(msg)


