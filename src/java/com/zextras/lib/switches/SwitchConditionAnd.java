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

import java.util.Arrays;
import java.util.List;

public class SwitchConditionAnd implements SwitchCondition
{
  List<SwitchCondition> mConditions;

  public SwitchConditionAnd(SwitchCondition... conditions)
  {
    mConditions = Arrays.asList(conditions);
  }

  @Override
  public boolean onToOff()
  {
    for( SwitchCondition condition : mConditions ) {
      if( !condition.onToOff() ) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean offToOn(SwitchConditionNotification conditionNotification)
  {
    for( SwitchCondition condition : mConditions ) {
      if( !condition.offToOn(conditionNotification) ) {
        return false;
      }
    }
    return true;
  }
}
