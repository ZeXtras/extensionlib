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

public class UnnecessaryServiceSwitch implements ServiceSwitch
{
  private final ServiceSwitch mServiceSwitch;

  public UnnecessaryServiceSwitch( ServiceSwitch serviceSwitch )
  {
    mServiceSwitch = serviceSwitch;
  }

  @Override
  public void turnOn(SwitchConditionNotification conditionNotification) throws Service.UnnecessaryServiceStartException
  {
    try
    {
      mServiceSwitch.turnOn(conditionNotification);
    }
    catch (Service.UnnecessaryServiceStartException ex)
    {
      ex.populate(this);
      throw ex;
    }
    catch (Service.ServiceStartException ex)
    {
      ex.populate(this);
      Service.UnnecessaryServiceStartException unnecessaryServiceStartException = new Service.UnnecessaryServiceStartException(mServiceSwitch,ex.getServiceStartExceptionMessage());
      for( Service.ServiceStartException subFailure : ex.getSubFailures() ) {
        unnecessaryServiceStartException.addSubServiceFailure(subFailure);
      }
      unnecessaryServiceStartException.initCause(ex);
      throw unnecessaryServiceStartException;
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
