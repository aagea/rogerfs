package org.rogerfs.api

import java.io.OutputStream

class RogerOutputStream(val store:IStore) extends OutputStream{


  override def write(b: Int): Unit = ???
}
