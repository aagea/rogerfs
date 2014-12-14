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

import java.util.UUID;

public interface IStore {

  public void createFile(File file) throws StoreException;
  public UUID openBlock(File file) throws StoreException;
  public void addData(File file, UUID block, byte[] data, int offset) throws StoreException;
  public void closeBlock(File file, UUID block, UUID nextBlock) throws StoreException;

  public File[] getFiles(String pathDirectory);
  public UUID[] getBlocks(File file);
  public byte[] getData(File file, UUID block, int offset);

  public int getMaxOffset();
  public int getMaxSizeData();
}
