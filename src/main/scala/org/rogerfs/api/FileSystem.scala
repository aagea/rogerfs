package org.rogerfs.api


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



  }

}


