package org.rogerfs.shell

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.OptionGroup
import org.apache.commons.cli.Options
import org.apache.commons.cli.{Option => CliOption}

object RogerFsShellApp extends App {
  val ls = CliOption.builder("ls").hasArg.build()
  val cat = CliOption.builder("cat").hasArg.build()
  val countLines = CliOption.builder("countLines").hasArg.build()
  val copyFromLocal= CliOption.builder("copyFromLocal").hasArgs.numberOfArgs(2).build()

  val group= new OptionGroup().addOption(ls).addOption(cat).addOption(countLines)
    .addOption(copyFromLocal)

  val option = new Options().addOptionGroup(group)

  val parser = new DefaultParser()
  val commandLine=parser.parse(option,args,true)

  commandLine.getOptions.foreach(op=>op.getArgName match {
    case "ls" => ls(op.getValue(0))
    case "cat" => cat(op.getValue(0))
    case "copyFromLocal" => copyFromLocal(op.getValue(0),op.getValue(1))
    case "countLines" =>  countLines(op.getValue(0))
  })

  def ls(directory:String):Unit={
    println("Paso por ls")
  }

  def cat(file:String):Unit={
    println("Paso por cat")
  }

  def copyFromLocal(source:String, dest:String):Unit={
    println("Paso por copyFromLocal")
  }

  def countLines(directory:String):Unit={
    println("Paso por countLines")
  }
  println("hola")
}
