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

    def createFile(path:IPath): File = {
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


