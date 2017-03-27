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

package com.zextras.lib.switches;

import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("PointlessBooleanExpression")
public class SimpleServiceSwitch implements ServiceSwitch
{
  private final Service         mService;
  private final SwitchCondition mSwitchCondition;
  private final ReentrantLock   mLock;
  private final String          mName;
  private       boolean         mIsOn;

  public SimpleServiceSwitch(String name, Service service)
  {
    this(name, new SwitchConditionAlwaysTrue(), service);
  }

  public SimpleServiceSwitch(String name, SwitchCondition switchCondition, Service service)
  {
    mService = service;
    mSwitchCondition = switchCondition;
    mName = name;
    mIsOn = false;
    mLock = new ReentrantLock();
  }

  @Override
  public void turnOn(SwitchConditionNotification conditionNotification)
    throws Service.ServiceStartException
  {
    mLock.lock();
    try
    {
      if( mIsOn == false )
      {
        boolean started = false;

        try
        {
          mIsOn = true;
          boolean conditionOk = mSwitchCondition.offToOn(conditionNotification);
          if( conditionOk )
          {
            try
            {
              mService.start();
            }
            catch (Service.ServiceStartException ex)
            {
              throw ex.populate(this);
            }
            catch (Exception ex)
            {
              throw new Service.ServiceStartException(this, "Exception ("+ex.getClass().getName()+")", ex);
            }

            started = true;
          }
        }
        finally
        {
          if( !started ) {
            mIsOn = false;
          }
        }
      }
    }
    finally
    {
      mLock.unlock();
    }
  }

  @Override
  public void turnOff()
  {
    mLock.lock();
    try
    {
      if( mIsOn == true )
      {
        boolean stopped = false;

        try
        {
          mIsOn = false;

          boolean conditionOk = mSwitchCondition.onToOff();
          if( conditionOk )
          {
            mService.stop();
            stopped = true;
          }
        }
        finally
        {
          if( !stopped ) {
            mIsOn = true;
          }
        }
      }
    }
    finally
    {
      mLock.unlock();
    }
  }

  @Override
  public boolean isOn()
  {
    mLock.lock();
    try
    {
      return mIsOn;
    }
    finally{
      mLock.unlock();
    }
  }

  @Override
  public boolean couldTurnOn(SwitchConditionNotification conditionNotification)
  {
    mLock.lock();
    try
    {
      return (!mIsOn) && mSwitchCondition.offToOn(conditionNotification);
    }
    finally{
      mLock.unlock();
    }
  }

  @Override
  public boolean couldTurnOff()
  {
    mLock.lock();
    try
    {
      return mIsOn && mSwitchCondition.onToOff();
    }
    finally{
      mLock.unlock();
    }
  }

  @Override
  public String name()
  {
    return mName;
  }
}
