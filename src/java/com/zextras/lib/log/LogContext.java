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

public interface LogContext
{
  @NotNull String get(String key);
  @NotNull String getAccountName();
  @NotNull String getAccountId();
  @NotNull String getDeviceId();
  @NotNull String getDeviceModel();
  @NotNull String getOperationId();
  @NotNull String getOperationName();
  @NotNull String getOperationModuleName();
  @NotNull String getRequestId();
  @NotNull String getProxyIp();


  LogContext set(String key, String value);
  @NotNull LogContext setAccountName(String mail);
  @NotNull LogContext setAccount(Account account);
  @NotNull LogContext setDeviceId(String deviceId);
  @NotNull LogContext setDeviceModel(String deviceModel);
  @NotNull LogContext setOperationId(String operationId);
  @NotNull LogContext setOperationName(String operationName);
  @NotNull LogContext setOperationModuleName(String moduleName);
  @NotNull LogContext setOperationSeverityLevel(SeverityLevel severityLevel);

  @NotNull LogContext setDedicatedLog(boolean hasDedicated);
  @NotNull LogContext setLoggerName(String name);

  boolean hasDedicatedLog();
  @NotNull String getLoggerName();
  @NotNull SeverityLevel getOperationSeverityLevel();
  void freeze();
  @NotNull LogContext createChild();

  @NotNull LogContext getParent();

  boolean isFrozen();
  boolean isRootContext();

  @NotNull String getOriginalIp();
  @NotNull String getEASVersion();

  @NotNull
  String getUserAddress();

  @NotNull LogContext setOriginalIp(String originalIp);
  @NotNull LogContext setEASVersion(String easVersion);

  @NotNull LogContext setRequestId(int id);

  @NotNull LogContext setUserAddress(String userAddress);

  @NotNull LogContext setProxyIp(String sourceIpAddress);

  void populateZimbraLogContext();

  void cleanZimbraLogContext();
}
