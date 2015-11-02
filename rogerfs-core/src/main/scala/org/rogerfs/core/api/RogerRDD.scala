package org.rogerfs.core.api

import java.util.UUID

import org.apache.spark.rdd.RDD
import org.rogerfs.common.store.IPath
import org.rogerfs.common.store.IStore

class RogerRDD(store: IStore) extends Serializable {

  case class Line(index: Int, uuid: UUID, line: String, isFirst: Boolean = false)
    extends Serializable

  private def convertLines(index: Int, dataArr: Array[String], isSplit: Boolean,
                   currentBlock: UUID, nextBlock: UUID): List[Line] = {
    if (dataArr.length == 1) {
      val line = if (isSplit) {
        Line(0, nextBlock, dataArr.head, isFirst = true)
      } else {
        Line(index, currentBlock, dataArr.head)
      }
      List(line)
    } else {
      Line(index, currentBlock, dataArr.head) :: convertLines(index + 1, dataArr.tail, isSplit,
        currentBlock, nextBlock)
    }
  }

  def getLines(directory: IPath): RDD[String] = {
    val blockRDD = store.getRdd(directory)
    val first = blockRDD.flatMap(block => {
      val dataStr = new String(block.getData, "UTF-8")
      val isSplit = block.getNextUIID != null && !dataStr.endsWith("\n")
      val dataArr = dataStr.split("\n")

      convertLines(0, dataArr, isSplit, block.getUuid, block.getNextUIID)
    })
    first.map(line => ((line.uuid, line.index), line)).reduceByKey((acc, curr) => {
      val data = if (curr.isFirst) {
        curr.line + acc.line
      } else {
        acc.line + curr.line
      }
      Line(curr.index, curr.uuid, data)
    }).map(line => line._2.line)
  }
}
