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

import com.zextras.lib.Error.ErrorCode;
import com.zextras.lib.Error.ZxError;

import java.util.ArrayList;
import java.util.List;

public interface Service
{
  void start() throws ServiceStartException;
  void stop();

  class ServiceStartException extends ZxError
  {
    private final List<ServiceStartException> mSubServices;
    private final String        mServiceStartExceptionMessage;
    private       ServiceSwitch mServiceSwitch;

    public ServiceStartException(ServiceSwitch serviceSwitch, String message, Throwable cause)
    {
      this(
        new ErrorCode()
        {
          @Override
          public String getCodeString()
          {
            return "UNABLE_TO_START_MANDATORY_SERVICE";
          }

          @Override
          public String getMessage()
          {
            return "{message}";
          }
        },
        serviceSwitch, message, cause
      );
    }

    protected ServiceStartException(ErrorCode code, ServiceSwitch serviceSwitch, String message, Throwable cause)
    {
      super(code);
      if( cause != null ) {
        this.initCause(cause);
      }
      this.setDetail("message", message);

      mServiceSwitch = serviceSwitch;
      mSubServices = new ArrayList<ServiceStartException>(4);
      mServiceStartExceptionMessage = message;
    }

    public ServiceStartException(String message)
    {
      this(null, message);
    }

    public ServiceStartException(String message, Throwable cause)
    {
      this(null, message, cause);
    }

    public String getServiceStartExceptionMessage()
    {
      return mServiceStartExceptionMessage;
    }

    public ServiceSwitch getServiceSwitch()
    {
      return mServiceSwitch;
    }

    public ServiceStartException populate(ServiceSwitch serviceSwitch)
    {
      if( mServiceSwitch == null ) {
        mServiceSwitch = serviceSwitch;
      }
      return this;
    }

    public List<ServiceStartException> getSubFailures()
    {
      return mSubServices;
    }

    public ServiceStartException(ServiceSwitch serviceSwitch, String message)
    {
      this(serviceSwitch, message, null);
    }

    public void addSubServiceFailure(ServiceStartException exception)
    {
      mSubServices.add(exception);
    }

    public void addSubServiceFailures(List<ServiceStartException> exceptions)
    {
      mSubServices.addAll(exceptions);
    }

    public boolean isMandatory()
    {
      return true;
    }

    public void setFailureDetails(String details)
    {
      setDetail("message", details);
    }
  }

  public class UnnecessaryServiceStartException extends ServiceStartException
  {
    public UnnecessaryServiceStartException(ServiceSwitch service, String message, Throwable cause)
    {
      super(
        new ErrorCode()
        {
          @Override
          public String getCodeString()
          {
            return "UNABLE_TO_START_UNNECESSARY_SERVICE";
          }

          @Override
          public String getMessage()
          {
            return "{message}";
          }
        },
        service, message, cause
      );
    }

    public UnnecessaryServiceStartException(ServiceSwitch service, String message)
    {
      this(service, message, null);
    }

    @Override
    public boolean isMandatory()
    {
      return false;
    }
  }
}
