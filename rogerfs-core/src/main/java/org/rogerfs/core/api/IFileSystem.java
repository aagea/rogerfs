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

package org.rogerfs.core.api;

import org.rogerfs.common.store.File;
import org.rogerfs.common.store.PathBase;
import org.rogerfs.common.store.StoreException;

public interface IFileSystem {

  public File createFile(String path) throws StoreException;
  public File createFile(PathBase path) throws StoreException;
  public File createFile(File file) throws StoreException;

  public RogerOutputStream writeFile(File file) throws StoreException;
}
