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

import com.zextras.lib.log.CurrentLogContext;
import com.zextras.lib.log.LogContext;
import com.zextras.lib.log.ZELogger;
import org.openzal.zal.Utils;
import org.openzal.zal.log.ZimbraLog;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ActivityThread extends Thread implements ZELogger
{
  private final String mPrefix;
  private ThreadSlot mThreadSlot = null;
  private Runnable   mActivity   = null;
  private LogContext mLogContext = null;

  private boolean    mStopped    = false;
  private final Lock      mLock      = new ReentrantLock();
  private final Condition mCondition = mLock.newCondition();


  public ActivityThread(String prefix)
  {
    mPrefix = prefix;
    setName(getLoggerName() + " Thread");
    setDaemon(true);
    start();
  }

  public String getLoggerName()
  {
    return mPrefix + " Activity";
  }

  public void startActivity(ThreadSlot threadSlot, Runnable activity, LogContext logContext)
  {
    //ZimbraLog.mailbox.debug("startActivity");
    //ZimbraLog.mailbox.debug(Utils.currentStackTrace());
    mLock.lock();
    try
    {
      mThreadSlot = threadSlot;
      mActivity = activity;
      mLogContext = logContext;
      mCondition.signalAll();
    }
    finally{
      mLock.unlock();
    }
    //ZimbraLog.mailbox.debug("startActivity end");
  }

  public void cleanStop()
  {
    mLock.lock();
    try
    {
      mStopped = true;
      mCondition.signalAll();
    }
    finally{
      mLock.unlock();
    }
  }

  public void run()
  {
    //ZimbraLog.mailbox.debug("Starting new thread" );

    while( true )
    {
      try
      {
        setName( getLoggerName()+" Thread");

        Runnable currentActivity = null;
        ThreadSlot currentThreadSlot = null;
        Object currentContext = null;

        mLock.lock();
        try
        {
          if( mStopped ){
            break;
          }

          if( mActivity != null )
          {
            currentActivity = mActivity;
            currentThreadSlot = mThreadSlot;
            CurrentLogContext.setCurrent(mLogContext);

            mActivity = null;
            mThreadSlot = null;
            mLogContext = null;
          }
          else
          {
            try{
              mCondition.await();
            }catch (InterruptedException ex){
            }
          }

          if (mStopped)
          {
            break;
          }
        }
        finally{
          mLock.unlock();
        }

        if( currentActivity != null )
        {
          try
          {
            currentActivity.run();
          }
          finally{
            currentThreadSlot.free();
          }
        }
      }
      catch( Throwable ex )
      {
        ZimbraLog.mailbox.error("Exception: "+ Utils.exceptionToString(ex) );
        try{
          Thread.sleep(60000);
        }catch(Throwable ex2){}
      }
    }
    //ZimbraLog.mailbox.debug("Shutting down thread" );
  }
}
