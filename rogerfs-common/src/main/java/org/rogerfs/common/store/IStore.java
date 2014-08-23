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

import java.util.List;
import java.util.UUID;

public interface IStore {
  public void createFile(File file) throws StoreException;
  public void createPartition(Partition partition) throws StoreException;
  public void createBlock(Block block) throws StoreException;
  public void createSubBlock(SubBlock subBlock) throws StoreException;

  public List<File> getFiles(String pathDirectory);
  public List<Partition> getPartitions(String pathFile);
  public List<Block> getBlocks(UUID uuidPartition);
  public List<SubBlock> getSubBlocks(UUID block);

  public int getSizeBlock();
  public int getSizeSubBlock();
}
