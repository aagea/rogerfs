package org.rogerfs.shell

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.OptionGroup
import org.apache.commons.cli.Options
import org.apache.commons.cli.{Option => CliOption}
import org.rogerfs.common.store.Path
import org.rogerfs.core.api.FileSystem
import org.rogerfs.core.api.RogerRDD
import org.rogerfs.store.cassandra.CassandraStore

object RogerFsShellApp extends App {
  val ls = CliOption.builder("ls").hasArg.argName("directory")
    .desc("List selected directory.").build()
  val cat = CliOption.builder("cat").hasArg.argName("file").desc("Read selected file.").build()
  val countLines = CliOption.builder("countLines").hasArg.argName("file")
    .desc("Count number of lines.").build()
  val copyFromLocal= CliOption.builder("copyFromLocal").hasArgs.numberOfArgs(2)
    .argName("source> <dest").desc("Copy from local filesystem to RogerFS.").build()
  val help=CliOption.builder("help").desc("Print this message.").build()

  val group= new OptionGroup().addOption(ls).addOption(cat).addOption(countLines)
    .addOption(copyFromLocal).addOption(help)

  val option = new Options().addOptionGroup(group)

  val parser = new DefaultParser()

  def ls(directory:String):Unit={

  }

  lazy val cassandraStore= CassandraStore()
  lazy val fs=FileSystem.mount(cassandraStore)

  def cat(file:String):Unit={
    val  path=Path.getPath(file)
    val stream= fs.readFile(path)
    val textReader= new BufferedReader(new InputStreamReader(stream))
    var line=textReader.readLine()
    while (line != null) {
      println(line)
      line=textReader.readLine()
    }
  }

  def copyFromLocal(source:String, dest:String):Unit={
    val sourceFile=new File(source)
    val sourceReader=new FileInputStream(sourceFile)
    val destPath= Path.getPath(dest)
    val destWriter=fs.writeFile(destPath)

    var rByte=sourceReader.read()
    while (rByte != -1){
      destWriter.write(rByte)
      rByte=sourceReader.read()
    }
    sourceReader.close()
    destWriter.close()

  }

  def countLines(directory:String):Unit={
    val directoryPath = Path.getPath(directory)
    val rdd= new RogerRDD(cassandraStore)
    val count = rdd.getLines(directoryPath).count()
    println("Number of lines: " + count)
  }

  val commandLine=parser.parse(option,args,true)
  val time= java.time.Instant.now()
  println("Start: " + time )
  try {

    commandLine.getOptions.foreach(op => op.getOpt match {
      case "ls" => ls(op.getValue(0))
      case "cat" => cat(op.getValue(0))
      case "copyFromLocal" => copyFromLocal(op.getValue(0), op.getValue(1))
      case "countLines" => countLines(op.getValue(0))
      case "help" =>
        val formatter = new HelpFormatter()
        formatter.printHelp("RogerFSShellApp", option)
      case _ => throw new Exception("Not found command.")
    })
  }finally {
    val millis = java.time.Instant.now().toEpochMilli - time.toEpochMilli
    println(millis + "ms")
    sys.exit()
  }



}
