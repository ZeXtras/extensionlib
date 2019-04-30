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

public abstract class CurrentLogContext
{
  public interface StackedLogContext extends LogContext
  {
    LogContext begin();
    LogContext end();
  }

  static final ThreadLocal<StackedLogContext> sCurrentLogContext = new ThreadLocal<StackedLogContext>();

  public static StackedLogContext current()
  {
    if (sCurrentLogContext.get() == null) {
      sCurrentLogContext.set( new StackedLogContextImpl(new RootLogContext()) );
    }

    return sCurrentLogContext.get();
  }

  public static LogContext begin()
  {
    return current().begin();
  }

  public static LogContext end()
  {
    return current().end();
  }

  public static LogContext setCurrent( @Nonnull LogContext logContext)
  {
    if (logContext == null) {
      throw new NullPointerException();
    }

    StackedLogContextImpl stackedLogContext = new StackedLogContextImpl(logContext);
    sCurrentLogContext.set(stackedLogContext);
    return stackedLogContext;
  }

  static class StackedLogContextImpl implements StackedLogContext
  {
    private LogContext mLogContext;

    StackedLogContextImpl( LogContext logContext )
    {
      mLogContext = logContext;
    }

    @Override
    public LogContext begin()
    {
      mLogContext = mLogContext.createChild();
      return mLogContext;
    }

    @Override
    public LogContext end()
    {
      if (!mLogContext.isFrozen())
      {
        throw new RuntimeException("You cannot end a non-frozen LogContext.");
      }
      mLogContext.cleanZimbraLogContext();
      mLogContext = mLogContext.getParent();
/*
  Remove log context when ending the last LogContext for the GC
*/
      if( mLogContext.isRootContext() )
      {
        sCurrentLogContext.remove();
      }

      return mLogContext;
    }

    @Nonnull
    @Override
    public String get(String key)
    {
      return mLogContext.get(key);
    }

    @Nonnull
    @Override
    public String getAccountName()
    {
      return mLogContext.getAccountName();
    }

    @Nonnull
    @Override
    public String getAccountId()
    {
      return mLogContext.getAccountId();
    }

    @Nonnull
    @Override
    public String getDeviceId()
    {
      return mLogContext.getDeviceId();
    }

    @Nonnull
    @Override
    public String getDeviceModel()
    {
      return mLogContext.getDeviceModel();
    }

    @Nonnull
    @Override
    public String getOperationId()
    {
      return mLogContext.getOperationId();
    }

    @Nonnull
    @Override
    public String getOperationName()
    {
      return mLogContext.getOperationName();
    }

    @Nonnull
    @Override
    public String getOperationModuleName()
    {
      return mLogContext.getOperationModuleName();
    }

    @Nonnull
    @Override
    public String getRequestId()
    {
      return mLogContext.getRequestId();
    }

    @Nonnull
    @Override
    public String getProxyIp()
    {
      return mLogContext.getProxyIp();
    }

    @Override
    public LogContext set(@Nonnull String key, @Nonnull String value)
    {
      return mLogContext.set(key, value);
    }

    @Nonnull
    @Override
    public LogContext setAccountName(String account)
    {
      return mLogContext.setAccountName(account);
    }

    @Nonnull
    @Override
    public LogContext setAccount(Account account)
    {
      return mLogContext.setAccount(account);
    }

    @Nonnull
    @Override
    public LogContext setDeviceId(String deviceId)
    {
      return mLogContext.setDeviceId(deviceId);
    }

    @Nonnull
    @Override
    public LogContext setDeviceModel(String deviceModel)
    {
      return mLogContext.setDeviceModel(deviceModel);
    }

    @Nonnull
    @Override
    public LogContext setOperationId(String operationId)
    {
      return mLogContext.setOperationId(operationId);
    }

    @Nonnull
    @Override
    public LogContext setOperationName(String operationName)
    {
      return mLogContext.setOperationName(operationName);
    }

    @Nonnull
    @Override
    public LogContext setOperationModuleName(String moduleName)
    {
      return mLogContext.setOperationModuleName(moduleName);
    }

    @Nonnull
    @Override
    public LogContext setOperationSeverityLevel(SeverityLevel severityLevel)
    {
      return mLogContext.setOperationSeverityLevel(severityLevel);
    }

    @Nonnull
    @Override
    public LogContext setDedicatedLog(boolean hasDedicated)
    {
      return mLogContext.setDedicatedLog(hasDedicated);
    }

    @Nonnull
    @Override
    public LogContext setLoggerName(String name)
    {
      return mLogContext.setLoggerName(name);
    }

    @Override
    public boolean hasDedicatedLog()
    {
      return mLogContext.hasDedicatedLog();
    }

    @Nonnull
    @Override
    public String getLoggerName()
    {
      return mLogContext.getLoggerName();
    }

    @Nonnull
    @Override
    public SeverityLevel getOperationSeverityLevel()
    {
      return mLogContext.getOperationSeverityLevel();
    }

    @Override
    public void freeze()
    {
      mLogContext.freeze();
    }

    @Nonnull
    @Override
    public LogContext createChild()
    {
      return mLogContext.createChild();
    }

    @Nonnull
    @Override
    public LogContext getParent()
    {
      return mLogContext.getParent();
    }

    @Override
    public boolean isFrozen()
    {
      return mLogContext.isFrozen();
    }

    @Override
    public boolean isRootContext()
    {
      return mLogContext.isRootContext();
    }

    @Nonnull
    @Override
    public String getOriginalIp()
    {
      return mLogContext.getOriginalIp();
    }

    @Nonnull
    @Override
    public String getEASVersion()
    {
      return mLogContext.getEASVersion();
    }

    @Nonnull
    @Override
    public String getUserAddress()
    {
      return mLogContext.getUserAddress();
    }

    @Nonnull
    @Override
    public LogContext setOriginalIp(String originalIp)
    {
      return mLogContext.setOriginalIp(originalIp);
    }

    @Nonnull
    @Override
    public LogContext setEASVersion(String easVersion)
    {
      return mLogContext.setEASVersion(easVersion);
    }

    @Nonnull
    @Override
    public LogContext setRequestId(int id)
    {
      return mLogContext.setRequestId(id);
    }

    @Nonnull
    @Override
    public LogContext setUserAddress(String userAddress)
    {
      return null;
    }

    @Nonnull
    @Override
    public LogContext setProxyIp(String sourceIpAddress)
    {
      return mLogContext.setProxyIp(sourceIpAddress);
    }

    @Override
    public void populateZimbraLogContext()
    {
      mLogContext.populateZimbraLogContext();
    }

    @Override
    public void cleanZimbraLogContext()
    {
      mLogContext.cleanZimbraLogContext();
    }
  }
}
