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

package com.zextras.lib.Error;

import com.zextras.lib.Container;
import com.zextras.lib.ContainerImpl;
import com.zextras.lib.ContainerListContainer;
import org.openzal.zal.lib.ActualClock;
import com.zextras.lib.log.SeverityLevel;
import com.zextras.lib.json.JSONArray;
import com.zextras.lib.json.JSONException;
import com.zextras.lib.json.JSONObject;
import org.openzal.zal.lib.Clock;

import java.util.HashMap;
import java.util.List;

public class ZxError extends Exception
{
  private final ErrorCode mCode;
  private final HashMap<String, String> mDetailsMap = new HashMap<String, String>();
  private SeverityLevel mSeverityLevel;
  private Clock         mCurrentTime;

  public static final String KEY_CODE       = "code";
  public static final String KEY_DETAILS    = "details";
  private static final String KEY_ERROR_TIME = "time";
  private static final String KEY_MESSAGE    = "message";
  public static final String KEY_STACKTRACE = "stackTrace";
  private static final String KEY_CAUSE      = "cause";

  protected final static String NOT_AVAILABLE_INFO = "'Information not available'";

  public ZxError(SeverityLevel severityLevel, ErrorCode code)
  {
    mCode = code;
    mSeverityLevel = severityLevel;
    mCurrentTime = ActualClock.sInstance;
  }

  public ZxError(ErrorCode code)
  {
    this(SeverityLevel.WARNING, code);
  }

  public void setSeverity(SeverityLevel severity)
  {
    mSeverityLevel = severity;
  }

  public SeverityLevel getSeverity()
  {
    return mSeverityLevel;
  }

  public String getMessage()
  {
    String message = mCode.getMessage();
    if (message == null) { return "UNDEFINED_ERROR_CODE";}

    HashMap<String, String> details = getDetails();

    for (String key : details.keySet()) {
      String det =  details.get(key);
      message = message.replace("{" + key + "}", (det == null) ? "" : det );
    }
    return message;
  }

  public String getStrippedMessage()
  {
    return getMessage();
  }

  public ErrorCode getCode() {
    return mCode;
  }

  public HashMap<String, String> getDetails()
  {
    return mDetailsMap;
  }

  protected ZxError setDetail(String key, String value)
  {
    mDetailsMap.put(key, value);
    return this;
  }

  protected ZxError setDetail(String key, int value)
  {
    mDetailsMap.put(key, Integer.toString(value));
    return this;
  }

  protected ZxError setDetail(String key, long value)
  {
    mDetailsMap.put(key, Long.toString(value));
    return this;
  }

  protected String getDetail(String key)
  {
    return mDetailsMap.get(key);
  }

  public JSONObject toJSON()
  {
    JSONObject obj = new JSONObject();
    obj.put(KEY_CODE, mCode.getCodeString());
    obj.put(KEY_MESSAGE, mCode.getMessage());
    obj.put(KEY_ERROR_TIME, mCurrentTime.now());
    obj.put(KEY_DETAILS, getDetails());
    JSONArray trace = new JSONArray();
    for (StackTraceElement stackEl : getStackTrace()) {
      JSONObject element = new JSONObject();
      element.put("className", stackEl.getClassName());
      element.put("fileName", stackEl.getFileName());
      element.put("lineNumber", stackEl.getLineNumber());
      element.put("methodName", stackEl.getMethodName());
      element.put("nativeMethod", stackEl.isNativeMethod());
      trace.put(element);
    }
    obj.put(KEY_STACKTRACE, trace);
    obj.put(KEY_CAUSE, encodeSubCause(getCause()));
    return obj;
  }

  public Container toContainer()
  {
    Container obj = new ContainerImpl();
    obj.putString(KEY_CODE, mCode.getCodeString());
    obj.putString(KEY_MESSAGE, mCode.getMessage());
    obj.putLong(KEY_ERROR_TIME, mCurrentTime.now());
    Container details = new ContainerImpl();
    for (String detailKey : getDetails().keySet())
    {
      details.putString(detailKey, getDetail(detailKey));
    }
    obj.putContainer(KEY_DETAILS, details);
    obj.putListContainer(KEY_STACKTRACE, buildTrace(getStackTrace()));
    Container cause =  buildContainerFromException(getCause());
    if (cause != null)
    {
      obj.putContainer(KEY_CAUSE, cause);
    }
    return obj;
  }

  private Iterable<Container> buildTrace(StackTraceElement[] stackTrace)
  {
    ContainerListContainer trace = new ContainerListContainer();
    for (StackTraceElement stackEl : getStackTrace()) {
      Container element = new ContainerImpl();
      element.putString("className", stackEl.getClassName());
      element.putString("fileName", stackEl.getFileName());
      element.putLong("lineNumber", stackEl.getLineNumber());
      element.putString("methodName", stackEl.getMethodName());
      element.putBoolean("nativeMethod", stackEl.isNativeMethod());
      trace.add(element);
    }
    return trace;
  }

  public ZxError setDetail(String key, List<String> val)
  {
    String tmp = "";
    boolean first = true;
    for (String tmpVal : val) {
      if (!first) {
        tmp += ", ";
      } else {
        first = false;
      }
      tmp += tmpVal;
    }
    return this.setDetail(key, tmp);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ZxError zxError = (ZxError) o;
    HashMap<String, String> myDetails = getDetails();
    HashMap<String, String> otherDetails = zxError.getDetails();

    if (mCode != zxError.mCode) return false;
    if (!myDetails.equals(otherDetails)) return false;
    if (mSeverityLevel != zxError.mSeverityLevel) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = mCode.hashCode();
    result = 31 * result + mDetailsMap.hashCode();
    result = 31 * result + mSeverityLevel.hashCode();
    return result;
  }

  private Container buildContainerFromException(Throwable t)
  {
    Container errorObj = new ContainerImpl();
    if (t != null)
    {
      errorObj.putString(KEY_MESSAGE, t.getMessage());
      errorObj.putListContainer(KEY_STACKTRACE, buildTrace(t.getStackTrace()));
      Container cause = buildContainerFromException(t.getCause());
      if (cause != null)
      {
        errorObj.putContainer(KEY_CAUSE, cause);
      }
      return errorObj;
    }
    else
    {
      return null;
    }
  }

  private JSONObject encodeSubCause(Throwable e)
  {
    JSONObject errorObj = new JSONObject();

    if(e != null)
    {
      errorObj.put(KEY_MESSAGE, e.getMessage());
      JSONArray trace = new JSONArray();
      for (StackTraceElement stackEl : e.getStackTrace()) {
        JSONObject element = new JSONObject();
        element.put("className", stackEl.getClassName());
        element.put("fileName", stackEl.getFileName());
        element.put("lineNumber", stackEl.getLineNumber());
        element.put("methodName", stackEl.getMethodName());
        element.put("nativeMethod", stackEl.isNativeMethod());
        trace.put(element);
      }
      errorObj.put(KEY_STACKTRACE, trace);
      errorObj.put(
        KEY_CAUSE,
        encodeSubCause(e.getCause())
      );
      return errorObj;
    }
    else
    {
      return null;
    }
  }
}
