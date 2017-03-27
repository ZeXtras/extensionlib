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
import org.jetbrains.annotations.Nullable;

public class LogContextImpl implements LogContext
{
  private       String        mAccountName;
  private       String        mIP;
  private       String        mDeviceId;
  private       String        mDeviceModel;
  private       String        mOperationId;
  private       String        mOperationName;
  private final LogContext    mParent;
  private       boolean       mFrozen;
  private       SeverityLevel mSeverityLevel;
  private       Boolean       mHasDedicated;
  private       String        mLoggerName;
  private       String        mOperationModuleName;
  private       String        mAccountId;
  private       String        mOriginalIp;
  private       String        mEasVersion;
  private       int           mRequestId;
  private       String        mUserAddress;
  private       String        mProxyIp;

  public LogContextImpl(@NotNull LogContext parent)
  {
    mParent = parent;
    mAccountName = "";
    mAccountId = "";
    mIP = "";
    mOriginalIp = "";
    mEasVersion = "";
    mDeviceId = "";
    mDeviceModel = "";
    mOperationId = "";
    mOperationName = "";
    mLoggerName = "";
    mUserAddress = "";
    mProxyIp = "";

    mFrozen = false;
    mHasDedicated = null;
    mSeverityLevel = null;
    mOperationModuleName = null;
  }

  private String emptyWhenNull( String s )
  {
    return (s == null) ? "" : s;
  }

  @NotNull
  @Override
  public String getAccountName()
  {
    if (mAccountName.isEmpty())
    {
      return mParent.getAccountName();
    }
    return mAccountName;
  }

  @NotNull
  @Override
  public String getAccountId()
  {
    if (mAccountId.isEmpty())
    {
      return mParent.getAccountName();
    }
    return mAccountId;
  }

  @NotNull
  @Override
  public String getDeviceId()
  {
    if (mDeviceId.isEmpty())
    {
      return mParent.getDeviceId();
    }
    return mDeviceId;
  }

  @NotNull
  @Override
  public String getDeviceModel()
  {
    if (mDeviceModel.isEmpty())
    {
      return mParent.getDeviceModel();
    }
    return mDeviceModel;
  }

  @NotNull
  @Override
  public String getOperationId()
  {
    if (mOperationId.isEmpty())
    {
      return mParent.getOperationId();
    }
    return mOperationId;
  }

  @NotNull
  @Override
  public String getOperationName()
  {
    if (mOperationName.isEmpty())
    {
      return mParent.getOperationName();
    }
    return mOperationName;
  }

  @NotNull
  @Override
  public String getOperationModuleName()
  {
    if (mOperationModuleName == null)
    {
      return mParent.getOperationModuleName();
    }
    return mOperationModuleName;
  }

  @NotNull
  @Override
  public LogContext setAccountName(String account)
  {
    canSetCheck();
    if (account != null)
    {
      mAccountName = account;
    }
    return this;
  }

  @NotNull
  @Override
  public LogContext setAccount(Account account)
  {
    canSetCheck();
    if (account != null)
    {
      setAccountName(account.getName());
      mAccountId = account.getId();
    }
    return this;
  }

  @NotNull
  @Override
  public LogContext setDeviceId(String deviceId)
  {
    canSetCheck();
    mDeviceId = emptyWhenNull(deviceId);
    return this;
  }

  @NotNull
  @Override
  public LogContext setDeviceModel(String deviceModel)
  {
    canSetCheck();
    mDeviceModel = emptyWhenNull(deviceModel);
    return this;
  }

  @NotNull
  @Override
  public LogContext setOperationId(
    @Nullable String operationId
  )
  {
    canSetCheck();
    if (operationId != null)
    {
      mOperationId = operationId;
    }
    return this;
  }

  @NotNull
  @Override
  public LogContext setOperationName(
    @Nullable String operationName
  )
  {
    canSetCheck();
    if (operationName != null)
    {
      mOperationName = operationName;
    }
    return this;
  }

  @NotNull
  @Override
  public LogContext setOperationModuleName(
    @Nullable String moduleName
  )
  {
    canSetCheck();
    if (moduleName != null)
    {
      mOperationModuleName = moduleName;
    }
    return this;
  }

  @NotNull
  @Override
  public LogContext setDedicatedLog(boolean hasDedicated)
  {
    canSetCheck();
    mHasDedicated = hasDedicated;
    return this;
  }

  @NotNull
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

  @NotNull
  @Override
  public String getLoggerName()
  {
    if (mLoggerName.isEmpty())
    {
      return mParent.getLoggerName();
    }
    return mLoggerName;
  }

  @NotNull
  @Override
  public SeverityLevel getOperationSeverityLevel()
  {
    if (mSeverityLevel == null)
    {
      return mParent.getOperationSeverityLevel();
    }
    return mSeverityLevel;
  }

  @NotNull
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
    mFrozen = true;
  }

  @NotNull
  @Override
  public LogContext createChild()
  {
    if (!mFrozen)
    {
      throw new RuntimeException("You cannot create a child from a non-frozen LogContext.");
    }
    return new LogContextImpl(this);
  }

  @NotNull
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

  @NotNull
  @Override
  public String getOriginalIp()
  {
    if (mOriginalIp.isEmpty())
    {
      return mParent.getOriginalIp();
    }
    return mOriginalIp;
  }

  @NotNull
  @Override
  public String getEASVersion()
  {
    if (mEasVersion.isEmpty())
    {
      return mParent.getEASVersion();
    }
    return mEasVersion;
  }

  @NotNull
  @Override
  public String getUserAddress()
  {
    return mUserAddress;
  }

  @NotNull
  @Override
  public LogContext setOriginalIp(String originalIp)
  {
    canSetCheck();
    mOriginalIp = originalIp;
    return this;
  }

  @NotNull
  @Override
  public LogContext setEASVersion(String easVersion)
  {
    canSetCheck();
    mEasVersion = easVersion;
    return this;
  }

  @NotNull
  @Override
  public LogContext setRequestId(int id)
  {
    canSetCheck();
    mRequestId = id;
    return this;
  }

  @NotNull
  @Override
  public LogContext setUserAddress(String userAddress)
  {
    canSetCheck();
    mUserAddress = userAddress;
    /* if (user != null)
    {
      mUserAddress = user.getAddress().toString();
    } */
    return this;
  }

  @NotNull
  @Override
  public LogContext setProxyIp(String sourceIpAddress)
  {
    canSetCheck();
    mProxyIp = sourceIpAddress;
    return this;
  }

  @Override
  public int getRequestId()
  {
    return mRequestId;
  }

  @NotNull
  @Override
  public String getProxyIp()
  {
    return mProxyIp;
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
