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

package org.rogerfs.common.store;

import java.util.SortedMap;
import java.util.UUID;

import org.apache.spark.rdd.RDD;

import scala.Tuple3;

public interface IStore {

  void createFile(IPath file) throws StoreException;
  UUID openBlock(IPath file) throws StoreException;
  void addData(IPath file, UUID block, byte[] data, int subBlock) throws StoreException;
  void closeBlock(IPath file, UUID block, UUID nextBlock) throws StoreException;

  IPath[] getFiles(IPath pathDirectory);
  SortedMap<UUID,UUID> getBlocks(IPath file);
  byte[] getData(IPath file, UUID block, int subBlock);

  RDD<RawData> getRdd(IPath directory);

  int getMaxSubBlocks();
  int getMaxSizeData();
}
