package com.zextras.lib;

import javax.annotation.Nonnull;
import org.openzal.zal.lib.Clock;

import java.util.PriorityQueue;

public class FixedCacheTTLMap<T extends Comparable, R> extends FixedCacheMap<T, R>
{
  private final PriorityQueue<Data<T>> mTTLQueue;
  private final Clock                  mClock;
  private final long                   mTtl;

  public FixedCacheTTLMap(int size, Getter<T, R> getter, Clock clock, long ttl,boolean cacheMisses)
  {
    super(size, getter, cacheMisses);
    mClock = clock;
    mTtl = ttl;
    mTTLQueue = new PriorityQueue<>();
  }

  @Override
  public void _put(T key,R value)
  {
    mTTLQueue.add(new Data<>(mClock.now(),key));
  }

  @Override
  protected void _cleanUp()
  {
    Data<T> entry = mTTLQueue.peek();
    long now = mClock.now();
    while( entry != null && (mMap.size() >= mSize || (now - entry.getTime()) > mTtl ))
    {
      mTTLQueue.remove();
      mMap.remove(entry.getData());
      entry = mTTLQueue.peek();
    }
  }

  private class Data<K> implements Comparable<Data<K>>
  {
    private final long mTime;
    private final K    mData;

    public Data(long time, K data)
    {
      mTime = time;
      mData = data;
    }

    public int compareTo(@Nonnull Data<K> o)
    {
      return Long.compare(mTime,o.mTime);
    }

    public long getTime()
    {
      return mTime;
    }

    public K getData()
    {
      return mData;
    }
  }
}
