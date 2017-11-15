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

package com.zextras.lib.log;

import org.openzal.zal.Account;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.log.ZimbraLog;

public class RootLogContext implements LogContext
{
  @NotNull
  @Override
  public String get(String key)
  {
    return "";
  }

  @NotNull
  @Override
  public String getAccountName()
  {
    return "";
  }

  @NotNull
  @Override
  public String getAccountId()
  {
    return "";
  }

  @NotNull
  @Override
  public String getDeviceId()
  {
    return "";
  }

  @NotNull
  @Override
  public String getDeviceModel()
  {
    return "";
  }

  @NotNull
  @Override
  public String getOperationId()
  {
    return "";
  }

  @NotNull
  @Override
  public String getOperationName()
  {
    return "";
  }

  @NotNull
  @Override
  public String getOperationModuleName()
  {
    return "ZxCore";
  }

  @Override
  public String getRequestId()
  {
    return "";
  }

  @NotNull
  @Override
  public String getProxyIp()
  {
    return "";
  }

  @Override
  public LogContext set(@NotNull String key, @NotNull String value)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setAccountName(String account)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setAccount(Account account)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setDeviceId(String deviceId)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setDeviceModel(String deviceModel)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setOperationId(String operationId)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setOperationName(String operationName)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setOperationModuleName(String moduleName)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setOperationSeverityLevel(SeverityLevel severityLevel)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setDedicatedLog(boolean hasDedicated)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setLoggerName(String name)
  {
    throw createCantSetException();
  }

  @Override
  public boolean hasDedicatedLog()
  {
    return false;
  }

  @NotNull
  @Override
  public String getLoggerName()
  {
    return "";
  }

  @NotNull
  @Override
  public SeverityLevel getOperationSeverityLevel()
  {
    return SeverityLevel.INFORMATION;
  }

  @Override
  public void freeze()
  {
    throw new RuntimeException("You cannot freeze an already frozen LogContext.");
  }

  @NotNull
  @Override
  public LogContext createChild()
  {
    return new LogContextImpl(this);
  }

  @NotNull
  @Override
  public LogContext getParent()
  {
    throw new RuntimeException("You cannot request the parent of Root LogContext.");
  }

  @Override
  public boolean isFrozen()
  {
    return true;
  }

  @Override
  public boolean isRootContext()
  {
    return true;
  }

  @NotNull
  @Override
  public String getOriginalIp()
  {
    return "";
  }

  @NotNull
  @Override
  public String getEASVersion()
  {
    return "";
  }

  @NotNull
  @Override
  public String getUserAddress()
  {
    return "";
  }

  @NotNull
  @Override
  public LogContext setOriginalIp(String originalIp)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setEASVersion(String easVersion)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setRequestId(int id)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setUserAddress(String userAddress)
  {
    throw createCantSetException();
  }

  @NotNull
  @Override
  public LogContext setProxyIp(String sourceIpAddress)
  {
    throw createCantSetException();
  }

  @Override
  public void populateZimbraLogContext()
  {
    ZimbraLog.clearContext();
  }

  @Override
  public void cleanZimbraLogContext()
  {
    ZimbraLog.clearContext();
  }

  private RuntimeException createCantSetException()
  {
    return new RuntimeException("You cannot perform sets on Root LogContext.");
  }
}
