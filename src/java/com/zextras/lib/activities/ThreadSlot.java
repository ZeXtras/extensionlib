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

import com.zextras.lib.log.ZELogger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSlot implements ZELogger
{
  private       boolean        mFree;
  private       ActivityThread mThread;
  private       Runnable       mActivity;
  private final ReentrantLock  mLock =  new ReentrantLock();;
  private       ActivityManager mActivityManager;

  public ThreadSlot(ActivityManager manager)
  {
    mActivityManager = manager;
    mActivity = null;
    mThread = null;
    mFree = true;
  }

  public String getLoggerName()
  {
    return "Activity Thread Slot";
  }

  public boolean isFree()
  {
    mLock.lock();
    try{
      return mFree;
    }
    finally{
      mLock.unlock();
    }
  }


  public boolean isFree(long timeout) throws InterruptedException
  {
    boolean locked = mLock.tryLock(timeout, TimeUnit.MILLISECONDS);
    if( !locked ) {
      throw new InterruptedException();
    }
    try{
      return mFree;
    }
    finally{
      mLock.unlock();
    }
  }


  public void free()
  {

    mLock.lock();
    try{
      if( mFree ){
        throw new RuntimeException("activity already free");
      }
      mFree = true;
    }
    finally{
      mLock.unlock();
    }

    boolean needToStop = mActivityManager.clearFreeThread(this);
    if( needToStop ) {
      cleanStop();
    }
  }

  public void startActivity( ActivitySlot activitySlot )
  {
    mLock.lock();
    try{
      if( !mFree ){
        throw new RuntimeException("activity not free");
      }

      //ZELog.core.debug(this,"startActivity start");
      mActivity = activitySlot.getActivity();
      if( mThread == null ){
        mThread = new ActivityThread(mActivityManager.getPrefix());
      }
      mFree = false;
    }
    finally{
      mLock.unlock();
    }

    //ZELog.core.debug(this,"startActivity startActivity");
    mThread.startActivity(this, activitySlot.getActivity(), activitySlot.getLogContext(), activitySlot.getName() );
    //ZELog.core.debug(this,"startActivity end");
  }

  public Runnable getCurrentActivity()
  {
    return mActivity;
  }

  public void cleanStop()
  {
    ActivityThread thread = null;

    mLock.lock();
    try{
      thread = mThread;
    }
    finally{
      mLock.unlock();
    }

    if( thread != null ) {
      thread.cleanStop();
    }
  }
}