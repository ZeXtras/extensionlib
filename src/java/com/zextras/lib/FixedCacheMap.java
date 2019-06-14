package com.zextras.lib;

import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class allow to easily create a cached values, it doesn't use any softreference so any entry will persist.
 * (!) It also guarantee that only one Getter is run at the same time.
 *
 *    size is the size of the cache
 *    getter is the populator of the cache in cache of cache miss
 */

public class FixedCacheMap<T extends Comparable, R>
{
  public interface Getter<T extends Comparable,R>
  {
    R get(T key);
  }

  protected final TreeMap<T, R> mMap;
  protected final Getter<T, R>  mGetter;
  protected final int           mSize;
  protected final ReentrantLock mLock;
  protected final ReentrantLock mGetterLock;
  protected       long          mHits;
  protected       long          mMiss;
  protected final boolean       mCacheMisses;

  public FixedCacheMap(int size, Getter<T, R> getter,boolean cacheMisses)
  {
    mSize = size;
    mCacheMisses = cacheMisses;
    mMap = new java.util.TreeMap<>();
    mGetter = getter;
    mLock = new ReentrantLock();
    mGetterLock = new ReentrantLock();
    mHits = 0L;
    mMiss = 0L;
  }

  protected void _get(T key) {}

  public R get(T key)
  {
    mLock.lock();
    try
    {
      R cachedResult = mMap.get(key);
      _get(key);
      if( cachedResult != null || (mCacheMisses && mMap.containsKey(key)))
      {
        mHits++;
        return cachedResult;
      }
      mMiss++;
    }
    finally
    {
      mLock.unlock();
    }

    R value;
    mGetterLock.lock();
    try
    {
      value = mGetter.get(key);
    }
    finally
    {
      mGetterLock.unlock();
    }

    if (value != null || mCacheMisses)
    {
      put(key, value);
    }

    return value;
  }

  protected void _put(T key,R value){}

  public void put(T key,R value)
  {
    mLock.lock();
    try
    {
      _cleanUp();
      mMap.put(key, value);
      _put(key, value);
    }
    finally
    {
      mLock.unlock();
    }
  }

  protected void _cleanUp()
  {
    while( mMap.size() >= mSize )
    {
      mMap.pollFirstEntry();
    }
  }

  public double hitRatio()
  {
    long hit = mHits;
    long miss = mMiss;
    if (hit + miss == 0)
    {
      return 0;
    }
    return ((double) hit) / ((double) (hit + miss));
  }

  public int size()
  {
    return mMap.size();
  }

  public void resetHitRatio()
  {
    mMiss = 0L;
    mHits = 0L;
  }

  public void clear()
  {
    mLock.lock();
    try
    {
      mMap.clear();
    }
    finally
    {
      mLock.unlock();
    }
  }
}
