package org.rogerfs.common.store;

public interface IStore {
  public void createFile(File file) throws StoreException;
  public void createPartition(Partition partition) throws StoreException;
  public void createBlock(Block block) throws StoreException;
  public void createSubBlock(SubBlock subBlock) throws StoreException;

  public int getSizeBlock();
  public int getSizeSubBlock();
}
