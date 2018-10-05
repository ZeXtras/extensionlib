/*
 * Copyright (C) 2017 ZeXtras S.r.l.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.zextras.lib.activities;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsumerQueue<T>
{
  private final LinkedList<T> mQueue;
  private final Lock          mLock;
  private final Condition     mWaitItem;
  private final int           mQueueMaxSize;
  private final Condition     mWaitEmpty;
  private final Condition     mWaitPop;
  private       int           mRunning;
  private       boolean       mFinish;

  public ConsumerQueue(int queueMaxSize, int numActivities)
  {
    mQueueMaxSize = queueMaxSize;
    mQueue = new LinkedList<T>();
    mLock = new ReentrantLock();
    mWaitItem = mLock.newCondition();
    mWaitEmpty = mLock.newCondition();
    mWaitPop = mLock.newCondition();
    mFinish = false;
    mRunning = numActivities;
  }

  public void add(T item) throws InterruptedException
  {
    mLock.lock();
    try
    {
      if (mFinish)
      {
        throw new RuntimeException();
      }

      while (mQueue.size() >= mQueueMaxSize)
      {
        mWaitPop.await();
      }
      mQueue.add(item);
      mWaitItem.signal();
    }
    finally
    {
      mLock.unlock();
    }
  }
  public boolean addIfAvailable(T item) throws InterruptedException
  {
    mLock.lock();
    try
    {
      if (mFinish)
      {
        throw new RuntimeException();
      }

      if (mQueue.size() >= mQueueMaxSize)
      {
        return false;
      }
      mQueue.add(item);
      mWaitItem.signal();
      return true;
    }
    finally
    {
      mLock.unlock();
    }
  }

  public T pop() throws InterruptedException
  {
    mLock.lock();
    try
    {
      while (mQueue.isEmpty() && !mFinish)
      {
        mWaitItem.await();
      }
      mWaitPop.signal();
      if (mQueue.isEmpty() && mFinish)
      {
        mRunning--;
        mWaitEmpty.signal();
        return null;
      }
      return mQueue.pop();
    }
    finally
    {
      mLock.unlock();
    }
  }

  public void finish() throws InterruptedException
  {
    mLock.lock();
    try
    {
      mFinish = true;
      while (mRunning > 0)
      {
        mWaitItem.signalAll();
        mWaitEmpty.await();
      }
    }
    finally
    {
      mLock.unlock();
    }
    if (!checkIntegrity())
    {
      throw new RuntimeException("ConsumerQueue invalid state");
    }
  }

  public boolean isStopped()
  {
    mLock.lock();
    try
    {
      return mFinish;
    }
    finally
    {
      mLock.unlock();
    }
  }

  public boolean checkIntegrity()
  {
    mLock.lock();
    try {
      return mQueue.isEmpty() && mFinish == true;
    } finally {
      mLock.unlock();
    }
  }


  public interface Consumer<T>
  {
    void consume(T item);
  }
}
