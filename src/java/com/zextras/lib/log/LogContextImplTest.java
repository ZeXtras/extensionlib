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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class LogContextImplTest
{
  private LogContext mLogOnOp;
  private LogContext mLogOnZeOp;
  private String mOperationId;
  private LogContext mLogOnEmpty;

  @Before
  public void setup() throws Exception
  {
    LogContext root = mock(LogContext.class);
    Mockito.when(root.getAccountName()).thenReturn("");
    Mockito.when(root.getProxyIp()).thenReturn("");
    Mockito.when(root.getDeviceId()).thenReturn("");
    Mockito.when(root.getDeviceModel()).thenReturn("");
    Mockito.when(root.getOperationId()).thenReturn("");
    Mockito.when(root.getOperationName()).thenReturn("");
    Mockito.when(root.hasDedicatedLog()).thenReturn(false);
    Mockito.when(root.getLoggerName()).thenReturn("Root Logger");
    Mockito.when(root.getOperationModuleName()).thenReturn("ZxCore");
    Mockito.when(root.getOperationSeverityLevel()).thenReturn(SeverityLevel.INFORMATION);

    mLogOnEmpty = new LogContextImpl(root);

    mLogOnOp = new LogContextImpl(root);
    mLogOnOp.setOperationId("operationId");
    mLogOnOp.setOperationName("operationName");
    mLogOnOp.setOperationModuleName("ZxCore");
    mLogOnOp.setDeviceId("xxxx-xxx-xxx-xxxx-xxxxxxxx");

    mLogOnZeOp = new LogContextImpl(root);
    mLogOnOp.setOperationId("operationId");
    mLogOnOp.setOperationName("operationName");
    mLogOnOp.setOperationModuleName("ZxCore");
  }

  @Test
  public void testGetAccountName() throws Exception
  {
    assertEquals(mLogOnEmpty.getAccountName(), "");
    assertEquals(mLogOnOp.getAccountName(), "");
    assertEquals(mLogOnZeOp.getAccountName(), "");
  }

  @Test
  public void testGetIP() throws Exception
  {
    assertEquals(mLogOnEmpty.getProxyIp(), "");
    assertEquals(mLogOnOp.getProxyIp(), "");
    assertEquals(mLogOnZeOp.getProxyIp(), "");
  }

  @Test
  public void testGetDeviceId() throws Exception
  {
    assertEquals(mLogOnEmpty.getDeviceId(), "");
    assertEquals(mLogOnOp.getDeviceId(), "xxxx-xxx-xxx-xxxx-xxxxxxxx");
    assertEquals(mLogOnZeOp.getDeviceId(), "");
  }

  @Test
  public void testGetDeviceModel() throws Exception
  {
    assertEquals(mLogOnEmpty.getDeviceModel(), "");
    assertEquals(mLogOnOp.getDeviceModel(), "");
    assertEquals(mLogOnZeOp.getDeviceModel(), "");
  }

  @Test
  public void testGetOperationId() throws Exception
  {
    assertEquals(mLogOnEmpty.getOperationId(), "");
    assertEquals(mLogOnOp.getOperationId(), mOperationId.toString());
    assertEquals(mLogOnZeOp.getOperationId(), "operationId");
  }

  @Test
  public void testGetOperationName() throws Exception
  {
    assertEquals(mLogOnEmpty.getOperationName(), "");
    assertEquals(mLogOnOp.getOperationName(), "Test Operation");
    assertEquals(mLogOnZeOp.getOperationName(), "Test ZEOperation");
  }

  @Test
  public void testHasDedicatedLog() throws Exception
  {
    assertFalse(mLogOnEmpty.hasDedicatedLog());
    assertFalse(mLogOnOp.hasDedicatedLog());
    assertTrue(mLogOnZeOp.hasDedicatedLog());
  }

  @Test
  public void testGetLoggerName() throws Exception
  {
    assertEquals(mLogOnEmpty.getLoggerName(), "Root Logger");
    assertEquals(mLogOnOp.getLoggerName(), "Root Logger");
    assertEquals(mLogOnZeOp.getLoggerName(), "Root Logger");
  }

  @Test
  public void testGetOperationSeverityLevel() throws Exception
  {
    assertEquals(mLogOnEmpty.getOperationSeverityLevel(), SeverityLevel.INFORMATION);
    assertEquals(mLogOnOp.getOperationSeverityLevel(), SeverityLevel.INFORMATION);
    assertEquals(mLogOnZeOp.getOperationSeverityLevel(), SeverityLevel.DEBUG);
  }

  @Test
  public void testFreeze_must_pass() throws Exception
  {
    mLogOnEmpty.freeze();
  }

  @Test(expected = RuntimeException.class)
  public void testFreeze_must_fail_on_second() throws Exception
  {
    mLogOnEmpty.freeze();
    mLogOnEmpty.freeze();
  }

  @Test(expected = RuntimeException.class)
  public void testCreateChild_mustFail() throws Exception
  {
    mLogOnEmpty.createChild();
  }

  @Test
  public void testCreateChild_must_pass() throws Exception
  {
    mLogOnEmpty.freeze();
    mLogOnEmpty.createChild();
  }

  @Test
  public void testSets_on_freezed_must_fail() throws Exception
  {
    mLogOnEmpty.freeze();

    try
    {
      mLogOnEmpty.setAccount(mock(Account.class));
      fail();
    } catch(Exception ignore) {}

    try
    {
      mLogOnEmpty.setProxyIp("fake");
      fail();
    } catch(Exception ignore) {}

    try
    {
      mLogOnEmpty.setDeviceId("fake");
      fail();
    } catch(Exception ignore) {}

    try
    {
      mLogOnEmpty.setDeviceModel("fake");
      fail();
    } catch(Exception ignore) {}

    try
    {
      mLogOnEmpty.setOperationId("operationId");
      fail();
    } catch(Exception ignore) {}

    try
    {
      mLogOnEmpty.setOperationName("operationName");
      fail();
    } catch(Exception ignore) {}

    try
    {
      mLogOnEmpty.setOperationModuleName("module");
      fail();
    } catch(Exception ignore) {}

    try
    {
      mLogOnEmpty.setDedicatedLog(true);
      fail();
    } catch(Exception ignore) {}

    try
    {
      mLogOnEmpty.setLoggerName("fake");
      fail();
    } catch(Exception ignore) {}
  }
}