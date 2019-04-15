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

public interface LogContext
{
  @Nonnull String get(String key);
  @Nonnull String getAccountName();
  @Nonnull String getAccountId();
  @Nonnull String getDeviceId();
  @Nonnull String getDeviceModel();
  @Nonnull String getOperationId();
  @Nonnull String getOperationName();
  @Nonnull String getOperationModuleName();
  @Nonnull String getRequestId();
  @Nonnull String getProxyIp();


  LogContext set(String key, String value);
  @Nonnull LogContext setAccountName(String mail);
  @Nonnull LogContext setAccount(Account account);
  @Nonnull LogContext setDeviceId(String deviceId);
  @Nonnull LogContext setDeviceModel(String deviceModel);
  @Nonnull LogContext setOperationId(String operationId);
  @Nonnull LogContext setOperationName(String operationName);
  @Nonnull LogContext setOperationModuleName(String moduleName);
  @Nonnull LogContext setOperationSeverityLevel(SeverityLevel severityLevel);

  @Nonnull LogContext setDedicatedLog(boolean hasDedicated);
  @Nonnull LogContext setLoggerName(String name);

  boolean hasDedicatedLog();
  @Nonnull String getLoggerName();
  @Nonnull SeverityLevel getOperationSeverityLevel();
  void freeze();
  @Nonnull LogContext createChild();

  @Nonnull LogContext getParent();

  boolean isFrozen();
  boolean isRootContext();

  @Nonnull String getOriginalIp();
  @Nonnull String getEASVersion();

  @Nonnull
  String getUserAddress();

  @Nonnull LogContext setOriginalIp(String originalIp);
  @Nonnull LogContext setEASVersion(String easVersion);

  @Nonnull LogContext setRequestId(int id);

  @Nonnull LogContext setUserAddress(String userAddress);

  @Nonnull LogContext setProxyIp(String sourceIpAddress);

  void populateZimbraLogContext();

  void cleanZimbraLogContext();
}
