package com.zextras.lib;

import org.openzal.zal.lib.Clock;

public class FixedCacheStringTTLMap<R> extends FixedCacheTTLMap<String, R>
{
  public FixedCacheStringTTLMap(int size, Getter<String, R> getter, Clock clock, long ttl,boolean cacheMisses)
  {
    super(size, getter, clock, ttl, cacheMisses);
  }

  @Override
  public void put(String key,R value)
  {
    super.put(key.intern(),value);
  }
}
