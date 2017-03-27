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

public class MandatoryServiceSwitch implements ServiceSwitch
{
  private final ServiceSwitch mServiceSwitch;

  public MandatoryServiceSwitch( ServiceSwitch serviceSwitch )
  {
    mServiceSwitch = serviceSwitch;
  }

  @Override
  public void turnOn(SwitchConditionNotification conditionNotification) throws Service.ServiceStartException
  {
    try
    {
      mServiceSwitch.turnOn(conditionNotification);
      if( !mServiceSwitch.isOn() ) {
        throw new Service.ServiceStartException(mServiceSwitch, conditionNotification.getNotificationMessage());
      }
    }
    catch (Service.UnnecessaryServiceStartException ex)
    {
      ex.populate(this);
      Service.ServiceStartException mandatoryException = new Service.ServiceStartException(this,ex.getServiceStartExceptionMessage());
      for( Service.ServiceStartException subFailure : ex.getSubFailures() ) {
        mandatoryException.addSubServiceFailure(subFailure);
      }
      mandatoryException.initCause(ex);
      throw mandatoryException;
    }
    catch (Service.ServiceStartException ex)
    {
      throw ex.populate(this);
    }
  }

  @Override
  public void turnOff()
  {
    mServiceSwitch.turnOff();
  }

  @Override
  public boolean isOn()
  {
    return mServiceSwitch.isOn();
  }

  @Override
  public boolean couldTurnOn(SwitchConditionNotification conditionNotification)
  {
    return mServiceSwitch.couldTurnOn(conditionNotification);
  }

  @Override
  public boolean couldTurnOff()
  {
    return mServiceSwitch.couldTurnOff();
  }

  @Override
  public String name()
  {
    return mServiceSwitch.name();
  }
}
