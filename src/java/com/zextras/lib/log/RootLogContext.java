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
import javax.annotation.Nonnull;
import org.openzal.zal.log.ZimbraLog;

public class RootLogContext implements LogContext
{
  @Nonnull
  @Override
  public String get(String key)
  {
    return "";
  }

  @Nonnull
  @Override
  public String getAccountName()
  {
    return "";
  }

  @Nonnull
  @Override
  public String getAccountId()
  {
    return "";
  }

  @Nonnull
  @Override
  public String getDeviceId()
  {
    return "";
  }

  @Nonnull
  @Override
  public String getDeviceModel()
  {
    return "";
  }

  @Nonnull
  @Override
  public String getOperationId()
  {
    return "";
  }

  @Nonnull
  @Override
  public String getOperationName()
  {
    return "";
  }

  @Nonnull
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

  @Nonnull
  @Override
  public String getProxyIp()
  {
    return "";
  }

  @Override
  public LogContext set(@Nonnull String key, @Nonnull String value)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setAccountName(String account)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setAccount(Account account)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setDeviceId(String deviceId)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setDeviceModel(String deviceModel)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setOperationId(String operationId)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setOperationName(String operationName)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setOperationModuleName(String moduleName)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setOperationSeverityLevel(SeverityLevel severityLevel)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setDedicatedLog(boolean hasDedicated)
  {
    throw createCantSetException();
  }

  @Nonnull
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

  @Nonnull
  @Override
  public String getLoggerName()
  {
    return "";
  }

  @Nonnull
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

  @Nonnull
  @Override
  public LogContext createChild()
  {
    return new LogContextImpl(this);
  }

  @Nonnull
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

  @Nonnull
  @Override
  public String getOriginalIp()
  {
    return "";
  }

  @Nonnull
  @Override
  public String getEASVersion()
  {
    return "";
  }

  @Nonnull
  @Override
  public String getUserAddress()
  {
    return "";
  }

  @Nonnull
  @Override
  public LogContext setOriginalIp(String originalIp)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setEASVersion(String easVersion)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setRequestId(int id)
  {
    throw createCantSetException();
  }

  @Nonnull
  @Override
  public LogContext setUserAddress(String userAddress)
  {
    throw createCantSetException();
  }

  @Nonnull
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
