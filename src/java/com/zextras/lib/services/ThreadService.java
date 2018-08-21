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

package com.zextras.lib.services;

import com.zextras.lib.Error.ZxError;
import com.zextras.lib.log.ZELogger;
import com.zextras.lib.switches.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ThreadService implements Service, Runnable, ZELogger
{
  private Thread mThread;
  private Lock mLock = new ReentrantLock();

  public boolean isStopped()
  {
    mLock.lock();
    try
    {
      return mStopped;
    }
    finally
    {
      mLock.unlock();
    }
  }

  private boolean mStopped;

  protected ThreadService()
  {
    mStopped = true;
  }

  private void renewThread()
  {
    mThread = new Thread(this);
    mThread.setName(getLoggerName());
  }

  @Override
  public void start() throws ServiceStartException
  {
    mStopped = false;
    renewThread();
    mThread.start();
  }

  @Override
  public void stop()
  {
    cleanStop();
  }

  public void cleanStop()
  {
    mLock.lock();
    try
    {
      mStopped = true;
      mThread.interrupt();
      mThread = null;
    }
    finally
    {
      mLock.unlock();
    }
  }

  //@VisibleForTesting
  public Thread getThread()
  {
    return mThread;
  }
}
