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


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zextras.lib.log.CurrentLogContext;
import com.zextras.lib.log.LogContext;
import com.zextras.lib.log.RootLogContext;
import com.zextras.lib.log.ZELogger;
import com.zextras.lib.switches.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class ActivityManager implements ZELogger, Service
{
  public final static int MAX_THREADS_SLOT   = 35;
  public final static int MAX_UNPURGED_TASKS = 10;
  public final static String PREFIX_NAME = "";

  private final Timer                    mTimer;
  private final LinkedList<ActivitySlot> mActivityList;
  private final LinkedList<ThreadSlot>   mThreadSlots;
  private final AtomicInteger            mScheduledTaskCounter;
  private final int                      mMaxThreadSlots;
  private final String mPrefix;
  private       boolean                  mStopped;

  public String getPrefix()
  {
    return mPrefix;
  }

  public ActivityManager(int maxThreadSlots, String prefix)
  {
    mMaxThreadSlots = maxThreadSlots;
    mPrefix = prefix;
    mActivityList = new LinkedList<ActivitySlot>();
    mThreadSlots = new LinkedList<ThreadSlot>();
    mStopped = false;
    mTimer = new Timer(mPrefix + " Activity Timer", true);
    mScheduledTaskCounter = new AtomicInteger(0);
  }

  public ActivityManager(int maxThreadSlots)
  {
    this(maxThreadSlots, PREFIX_NAME);
  }

  @Inject
  public ActivityManager()
  {
    this(MAX_THREADS_SLOT, PREFIX_NAME);
  }

  public String getLoggerName()
  {
    return mPrefix + " Activity Manager";
  }

  private final ReentrantLock mLock = new ReentrantLock();

  //@VisibleForTesting
  public ThreadSlot getSlot(int numberOfSlot)
  {
    mLock.lock();
    try {
      if( numberOfSlot >= 0 && numberOfSlot < mThreadSlots.size() )
      {
        return mThreadSlots.get(numberOfSlot);
      }
    }
    finally{
      mLock.unlock();
    }
    throw new RuntimeException("slot "+numberOfSlot+" not found");
  }

  public void addActivityWithCurrentLogContext(Runnable activity, Object context)
  {
    LogContext logContext = CurrentLogContext.current().createChild();
    logContext.freeze();

    addActivity(activity, logContext,"");
  }

  public void addActivity(Runnable activity)
  {
    addActivity(activity, new RootLogContext(),"");
  }

  public void addActivity(Runnable activity,String name)
  {
    addActivity(activity, new RootLogContext(),name);
  }

  public void addActivity(Runnable activity, LogContext logContext,String name)
  {
    mLock.lock();
    try
    {
      mActivityList.add( new ActivitySlot(activity, logContext, name) );
    }
    finally{
      mLock.unlock();
    }

    //ZELog.core.debug(this,"addActivityLegacy added");
    checkloop();
  }

  public void cleanStop()
  {
    LinkedList<ThreadSlot> slots;
    mLock.lock();
    try{
      mStopped = true;
      slots = new LinkedList<ThreadSlot>(mThreadSlots);
      mTimer.purge();
      mTimer.cancel();
    }
    finally{
      mLock.unlock();
    }

    for( ThreadSlot threadSlot : slots)
    {
      threadSlot.cleanStop();
    }

    mLock.lock();
    try{
      mThreadSlots.clear();
    }
    finally{
      mLock.unlock();
    }

    mLock.lock();
    try{
      mActivityList.clear();
    }
    finally{
      mLock.unlock();
    }
  }

  public void checkloop()
  {
    mLock.lock();
    try
    {
      while( !mStopped && !mActivityList.isEmpty() )
      {
        ActivitySlot activitySlot = mActivityList.removeFirst();

        ThreadSlot chosenThreadSlot = null;
        for( ThreadSlot threadSlot : mThreadSlots)
        {
          if( threadSlot.isFree() )
          {
            chosenThreadSlot = threadSlot;
            break;
          }
        }

        if( chosenThreadSlot == null )
        {
          chosenThreadSlot = new ThreadSlot(this);
          mThreadSlots.add(chosenThreadSlot);
        }

        chosenThreadSlot.startActivity(
          activitySlot
        );
      }
    }
    finally{
      mLock.unlock();
    }
  }

  boolean clearFreeThread( ThreadSlot threadSlot )
  {
    mLock.lock();
    try
    {
      boolean isFree;

      while( true )
      {
        try{
          isFree = threadSlot.isFree( 1000L );
          break;
        }
        catch (InterruptedException e)
        {
          mLock.unlock();
          try
          {
            Thread.sleep(1000L);
          }
          catch (InterruptedException e1){
          }
          finally{
            mLock.lock();
          }
        }
      }

      if( mThreadSlots.size() > mMaxThreadSlots && isFree )
      {
        mThreadSlots.remove(threadSlot);
        return true;
      }
      else
      {
        return false;
      }
    }
    finally {
      mLock.unlock();
    }
  }

  //@VisibleForTesting
  public int getNumberOfFreeSlots()
  {
    List<ThreadSlot> slots;
    mLock.lock();
    try {
      slots = new ArrayList<ThreadSlot>(mThreadSlots);
    } finally {
      mLock.unlock();
    }

    int conter = 0;
    for(ThreadSlot threadSlot : slots)
    {
      if( threadSlot.isFree() ){
        conter++;
      }
    }

    return conter;
  }

  //@VisibleForTesting
  public int getNumberOfThreadSlot()
  {
    mLock.lock();
    try {
      return mThreadSlots.size();
    }
    finally{
      mLock.unlock();
    }
  }

  private void incrementAndCheckPurge()
  {
    int howManyUnpurged = mScheduledTaskCounter.incrementAndGet();
    if( howManyUnpurged >= MAX_UNPURGED_TASKS )
    {
      mScheduledTaskCounter.set(0);
      mTimer.purge();
    }
  }

  public ActivityTimer scheduleActivity(final Runnable activity, Date date)
  {
    TimerTask task = new TimerTask() {
      @Override
      public void run()
      {
        addActivity(activity);
      }
    };

    mTimer.schedule(task, date);
    incrementAndCheckPurge();

    return new ActivityTimerImpl(task);
  }

  public ActivityTimer scheduleActivity(final Runnable activity, long delayInMs)
  {
    TimerTask task = new TimerTask() {
      @Override
      public void run()
      {
        addActivity(activity);
      }
    };

    mTimer.schedule(task, delayInMs);
    incrementAndCheckPurge();

    return new ActivityTimerImpl(task);
  }

  public ActivityTimer scheduleActivityAtFixedRate(
    final Runnable activity,
    long delay,
    long period
  )
  {
    TimerTask task = new TimerTask() {
      @Override
      public void run()
      {
        addActivity(activity);
      }
    };

    mTimer.scheduleAtFixedRate(task, delay, period);
    incrementAndCheckPurge();

    return new ActivityTimerImpl(task);
  }

  @Override
  public void start() throws ServiceStartException
  {

  }

  @Override
  public void stop()
  {
    cleanStop();
  }

  public <T> ConsumerQueue<T> createNewConsumerQueue(
    final ConsumerQueue.Consumer<T> consumer,
    int numActivity,
    int maxQueueSize
  )
  {
    final ConsumerQueue<T> queue = new ConsumerQueue<T>(maxQueueSize, numActivity);
    Runnable runnable = new Runnable()
    {
      @Override
      public void run()
      {
        while (true)
        {
          T item;
          try
          {
            item = queue.pop();
          }
          catch (InterruptedException e)
          {
            return;
          }
          if (item == null)
          {
            return;
          }
          consumer.consume(item);
        }
      }
    };

    for (int i = 0; i < numActivity; i++)
    {
      addActivity(runnable);
    }

    return queue;
  }

  public int getNumberOfMaxThreadSlots()
  {
    return mMaxThreadSlots;
  }

  private class ActivityTimerImpl implements ActivityTimer
  {
    private final TimerTask mTimerTask;

    public ActivityTimerImpl(TimerTask timerTask)
    {
      mTimerTask = timerTask;
    }

    public boolean cancel()
    {
      return mTimerTask.cancel();
    }
  }
}
