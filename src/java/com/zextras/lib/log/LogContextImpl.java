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
import javax.annotation.Nullable;
import org.openzal.zal.log.ZimbraLog;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LogContextImpl implements LogContext
{
  private final HashMap<String, String> mContext;
  private final LogContext    mParent;
  private       boolean       mFrozen;
  private       SeverityLevel mSeverityLevel;
  private       Boolean       mHasDedicated;
  private       String        mLoggerName;

  public LogContextImpl(@Nonnull LogContext parent)
  {
    mParent = parent;
    mContext = new LinkedHashMap<String, String>();
    mLoggerName = "";
    mFrozen = false;
    mHasDedicated = null;
    mSeverityLevel = null;
  }

  private String emptyWhenNull( String s )
  {
    return (s == null) ? "" : s;
  }

  private boolean has(String key)
  {
    return mContext.containsKey(key) || (mContext.get(key) != null && !mContext.get(key).isEmpty());
  }

  public String get(String key)
  {
    if (!has(key))
    {
      return mParent.get(key);
    }

    return mContext.get(key);
  }

  @Nonnull
  @Override
  public String getAccountName()
  {
    return get("account");
  }

  @Nonnull
  @Override
  public String getAccountId()
  {
    return get("accountId");
  }

  @Nonnull
  @Override
  public String getDeviceId()
  {
    return get("id");
  }

  @Nonnull
  @Override
  public String getDeviceModel()
  {
    return get("model");
  }

  @Nonnull
  @Override
  public String getOperationId()
  {
    return get("operationId");
  }

  @Nonnull
  @Override
  public String getOperationName()
  {
    return get("operation");
  }

  @Nonnull
  @Override
  public String getOperationModuleName()
  {
    return get("module");
  }

  @Nonnull
  @Override
  public LogContext setAccountName(String account)
  {
    canSetCheck();
    if (account != null)
    {
      return set("account", account);
    }
    return this;
  }

  @Nonnull
  @Override
  public LogContext setAccount(Account account)
  {
    canSetCheck();
    if (account != null)
    {
      setAccountName(account.getName());
      set("accountId", account.getId());
    }
    return this;
  }

  @Nonnull
  @Override
  public LogContext setDeviceId(String deviceId)
  {
    return set("id", emptyWhenNull(deviceId));
  }

  @Nonnull
  @Override
  public LogContext setDeviceModel(String deviceModel)
  {
    return set("model", emptyWhenNull(deviceModel));
  }

  @Nonnull
  @Override
  public LogContext setOperationId(
    @Nullable String operationId
  )
  {
    canSetCheck();
    if (operationId != null)
    {
      return set("operationId", operationId);
    }
    return this;
  }

  @Nonnull
  @Override
  public LogContext setOperationName(
    @Nullable String operationName
  )
  {
    canSetCheck();
    if (operationName != null)
    {
      return set("operation", operationName);
    }
    return this;
  }

  @Nonnull
  @Override
  public LogContext setOperationModuleName(
    @Nullable String moduleName
  )
  {
    canSetCheck();
    if (moduleName != null)
    {
      set("module", moduleName);
    }
    return this;
  }

  @Nonnull
  @Override
  public LogContext setDedicatedLog(boolean hasDedicated)
  {
    canSetCheck();
    mHasDedicated = hasDedicated;
    return this;
  }

  @Nonnull
  @Override
  public LogContext setLoggerName(String name)
  {
    canSetCheck();
    mLoggerName = emptyWhenNull(name);
    return this;
  }

  @Override
  public boolean hasDedicatedLog()
  {
    if( mHasDedicated == null)
    {
      return mParent.hasDedicatedLog();
    }
    return mHasDedicated;
  }

  @Nonnull
  @Override
  public String getLoggerName()
  {
    if (mLoggerName.isEmpty())
    {
      return mParent.getLoggerName();
    }
    return mLoggerName;
  }

  @Nonnull
  @Override
  public SeverityLevel getOperationSeverityLevel()
  {
    if (mSeverityLevel == null)
    {
      return mParent.getOperationSeverityLevel();
    }
    return mSeverityLevel;
  }

  @Nonnull
  @Override
  public LogContext setOperationSeverityLevel(SeverityLevel severityLevel)
  {
    if (severityLevel != null)
    {
      mSeverityLevel = severityLevel;
    }

    return this;
  }

  @Override
  public void freeze()
  {
    if (mFrozen)
    {
      throw new RuntimeException("You cannot freeze an already frozen LogContext.");
    }
    populateZimbraLogContext();
    mFrozen = true;
  }

  @Override
  public void populateZimbraLogContext()
  {
    mParent.populateZimbraLogContext();
    ZimbraLog.addToContext("tid", String.valueOf(Thread.currentThread().getId()));
    for (Map.Entry<String, String> entry : mContext.entrySet())
    {
      ZimbraLog.addToContext(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void cleanZimbraLogContext()
  {
    for (Map.Entry<String, String> entry : mContext.entrySet())
    {
      ZimbraLog.addToContext(entry.getKey(), null);
    }
    mParent.populateZimbraLogContext();
  }

  @Nonnull
  @Override
  public LogContext createChild()
  {
    if (!mFrozen)
    {
      throw new RuntimeException("You cannot create a child from a non-frozen LogContext.");
    }
    return new LogContextImpl(this);
  }

  @Nonnull
  @Override
  public LogContext getParent()
  {
    return mParent;
  }

  @Override
  public boolean isFrozen()
  {
    return mFrozen;
  }

  @Override
  public boolean isRootContext()
  {
    return false;
  }

  @Nonnull
  @Override
  public String getOriginalIp()
  {
    return get("oip");
  }

  @Nonnull
  @Override
  public String getEASVersion()
  {
    return get("eas");
  }

  @Nonnull
  @Override
  public String getUserAddress()
  {
    return get("user");
  }

  @Nonnull
  @Override
  public LogContext setOriginalIp(String originalIp)
  {
    return set("oip", originalIp);
  }

  @Nonnull
  @Override
  public LogContext setEASVersion(String easVersion)
  {
    return set("eas", easVersion);
  }

  @Nonnull
  @Override
  public LogContext setRequestId(int id)
  {
    return set("rid", String.valueOf(id));
  }

  @Nonnull
  @Override
  public LogContext setUserAddress(String userAddress)
  {
    return set("user", userAddress);
  }

  @Nonnull
  @Override
  public LogContext setProxyIp(String sourceIpAddress)
  {
    return set("proxy", sourceIpAddress);
  }

  @Override
  public String getRequestId()
  {
    return get("rid");
  }

  @Nonnull
  @Override
  public String getProxyIp()
  {
    return get("proxy");
  }

  @Override
  public LogContext set(@Nonnull String key, String value)
  {
    canSetCheck();
    mContext.put(key, value == null ? "" : value);
    return this;
  }

  private void canSetCheck()
  {
    if (mFrozen)
    {
      throw createCantSetException();
    }
  }

  private RuntimeException createCantSetException()
  {
    return new RuntimeException("You cannot perform sets on frozen LogContext.");
  }
}
