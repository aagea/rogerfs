package org.rogerfs.common.store;

import java.io.Serializable;
import java.util.UUID;

public class RawData implements Serializable{
  private final IPath path;
  private final UUID uuid;
  private final byte[] data;
  private final UUID nextUIID;

  public RawData(IPath path, UUID uuid, byte[] data, UUID nextUIID) {
    this.path=path;
    this.uuid = uuid;
    this.data = data;
    this.nextUIID = nextUIID;
  }

  public UUID getUuid() {
    return uuid;
  }

  public byte[] getData() {
    return data;
  }

  public UUID getNextUIID() {
    return nextUIID;
  }

  public IPath getPath() {
    return path;
  }
}
