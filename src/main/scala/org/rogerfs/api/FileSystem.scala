package org.rogerfs.api

import org.rogerfs.store.IStore

object FileSystem{
  def mount(store:IStore):IFileSystem={
    new FileSystem(store)
  }

  private class FileSystem(store:IStore) extends IFileSystem{
    def createDirectory(path:String){

    }
    def createFile()
  }

}


