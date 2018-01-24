package com.zextras.lib.Error;

import com.zextras.lib.json.JSONArray;
import com.zextras.lib.json.JSONException;
import com.zextras.lib.json.JSONObject;
import com.zextras.lib.log.SeverityLevel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ErrorCodesGeneric
{
  public static ErrorCode UNKNOWN_ERROR;
  public static ErrorCode UNABLE_TO_OBTAIN_DB_CONNECTION;
  public static ErrorCode OPERATION_BLOCKING_ERROR;

  private static final ArrayList<ErrorCode> mErrorCodes;

  static
  {
    mErrorCodes = new ArrayList<ErrorCode>();

    UNKNOWN_ERROR = new ErrorCode()
    {
      public String getCodeString()
      {
        return "UNKNOWN_ERROR";
      }

      public String getMessage()
      {
        return "Unknown Error";
      }
    };
    mErrorCodes.add(UNKNOWN_ERROR);

    UNABLE_TO_OBTAIN_DB_CONNECTION = new ErrorCode()
    {
      public String getCodeString()
      {
        return "UNABLE_TO_OBTAIN_DB_CONNECTION";
      }

      public String getMessage()
      {
        return "Unable to obtain database connection";
      }
    };
    mErrorCodes.add(UNABLE_TO_OBTAIN_DB_CONNECTION);

    OPERATION_BLOCKING_ERROR = new ErrorCode()
    {
      public String getCodeString()
      {
        return "OPERATION_BLOCKING_ERROR";
      }

      public String getMessage()
      {
        return "Blocking operation error";
      }
    };
    mErrorCodes.add(OPERATION_BLOCKING_ERROR);
  }


  private static final String JSTemplate = "/*\n" +
    " * ***** BEGIN LICENSE BLOCK *****\n" +
    " * Copyright (C) 2011-" + Calendar.getInstance().get(Calendar.YEAR) + " ZeXtras\n" +
    " *\n" +
    " * The contents of this file are subject to the ZeXtras EULA;\n" +
    " * you may not use this file except in compliance with the EULA.\n" +
    " * You may obtain a copy of the EULA at\n" +
    " * http://www.zextras.com/zextras-eula.html\n" +
    " * ***** END LICENSE BLOCK *****\n" +
    " */\n" +
    "\n" +
    "// File auto-generated, DO NOT EDIT\n" +
    "function ZxErrorCode() {}\n" +
    "ZxErrorCode._msgs = {};\n" +
    "ZxErrorCode.getMessage = function(code) { return ZxErrorCode._msgs[code]; };\n" +
    "\n" +
    "@{content}@" +
    "\n" +
    "if (typeof module !== 'undefined' && module.exports) {\n" +
    //    "  var require = patchRequire(require);\n" +
    "  module.exports = ZxErrorCode;\n" +
    "}";

  private static final String CoffeeTemplate =
    "#\n" +
    "# ***** BEGIN LICENSE BLOCK *****\n" +
    "# Copyright (C) 2011-" + Calendar.getInstance().get(Calendar.YEAR) + " ZeXtras\n" +
    "#\n" +
    "# The contents of this file are subject to the ZeXtras EULA;\n" +
    "# you may not use this file except in compliance with the EULA.\n" +
    "# You may obtain a copy of the EULA at\n" +
    "# http://www.zextras.com/zextras-eula.html\n" +
    "# ***** END LICENSE BLOCK *****\n" +
    "#\n" +
    "\n" +
    "# File auto-generated, DO NOT EDIT\n" +
    "class ZxErrorCode\n" +
    "\n" +
    "  @_msgs: {}\n\n" +
    "@{content}@" +
    "\n" +
    "  @getMessage: (code) -> if ZxErrorCode._msgs[code]? then ZxErrorCode._msgs[code] else code\n" +
    "\n" +
    "if module? then module.exports = ZxErrorCode\n" +
    "if window? then window.ZxErrorCode = ZxErrorCode\n";

  private static final String TSTemplate = "/*\n" +
      " * ***** BEGIN LICENSE BLOCK *****\n" +
      " * Copyright (C) 2011-" + Calendar.getInstance().get(Calendar.YEAR) + " ZeXtras\n" +
      " *\n" +
      " * The contents of this file are subject to the ZeXtras EULA;\n" +
      " * you may not use this file except in compliance with the EULA.\n" +
      " * You may obtain a copy of the EULA at\n" +
      " * http://www.zextras.com/zextras-eula.html\n" +
      " * ***** END LICENSE BLOCK *****\n" +
      " */\n" +
      "\n" +
      "// File auto-generated, DO NOT EDIT\n" +
      "export class ZxErrorCode {\n" +
      "\n" +
      "@{content}@" +
      "\n" +
      "  private static sMsgs: {[key: string]: string} = {\n" +
      "@{messages}@" +
      "  };\n" +
      "  public static getMessage(code: string): string { return ZxErrorCode.sMsgs[code]; }\n" +
      "  if (typeof window !== 'undefined') {\n" +
          "window['ZxErrorCode'] = ZxErrorCode;\n" +
      "  }\n" +
      "}";

  private static final Map<String, ErrorCode> mStringToErrorCode = new HashMap<String, ErrorCode>();

  static
  {
    for (ErrorCode errorCode : mErrorCodes)
    {
      mStringToErrorCode.put(errorCode.getCodeString(), errorCode);
    }
  }

  public static ErrorCode fromString(String errorName)
  {
    ErrorCode errorCode = mStringToErrorCode.get(errorName);
    return errorCode != null ? errorCode : UNKNOWN_ERROR;
  }

  /**
   * Build a JS Class of errors usable by ZeXtras Zimlet.
   * @return String
   */
  public static String BuildJSClass()
  {
    final StringBuilder sb = new StringBuilder();
    for (ErrorCode code : mErrorCodes)
    {
      String tmpMessage = code.getMessage().replace("\'", "\\'").replace("\n", "\\n");
      sb.append("ZxErrorCode.").append(code.getCodeString()).append(" = '").append(code.getCodeString()).append(
        "';\n"
      ).append("ZxErrorCode._msgs.").append(code.getCodeString()).append(" = '").append(tmpMessage).append(
        "';\n"
      );
    }
    return JSTemplate.replace("@{content}@", sb.toString());
  }

  public static String BuildCoffeeScriptClass()
  {
    final StringBuilder sb = new StringBuilder();
    for (ErrorCode code : mErrorCodes)
    {
      String tmpMessage = code.getMessage().replace("\"", "\\\"").replace("\n", "\\n");
      sb.append("  @").append(code.getCodeString()).append(": \"").append(code.getCodeString()).append("\"\n")
        .append("  @_msgs.").append(code.getCodeString()).append(" = \"").append(tmpMessage).append("\"\n");
    }
    return CoffeeTemplate.replace("@{content}@", sb.toString());
  }

  public static String BuildTSClass()
  {
    final StringBuilder sb = new StringBuilder();
    final StringBuilder sbMsgs = new StringBuilder();
    for (ErrorCode code : mErrorCodes)
    {
      String tmpMessage = code.getMessage().replace("\'", "\\'").replace("\n", "\\n");
      sb.append("  public static ").append(code.getCodeString()).append(": string = '").append(code.getCodeString()).append("';\n");
      sbMsgs.append("    ").append(code.getCodeString()).append(": '").append(tmpMessage).append("',\n");
    }
    return TSTemplate.replace("@{content}@", sb.toString()).replace("@{messages}@", sbMsgs.toString());
  }

  public static void main(String[] args)
  {
    if (args.length <= 0) {
      System.out.println(ErrorCodesGeneric.BuildCoffeeScriptClass());
      return;
    }

    if (args[0].toUpperCase().equals("TS")) {
      System.out.println(ErrorCodesGeneric.BuildTSClass());
      return;
    }
    if (args[0].toUpperCase().equals("JS")) {
      System.out.println(ErrorCodesGeneric.BuildJSClass());
      return;
    }
    if (args[0].toUpperCase().equals("COFFEE")) {
      System.out.println(ErrorCodesGeneric.BuildCoffeeScriptClass());
      return;
    }

    throw new RuntimeException("Please specify an output format [TS, JS or COFFEE]");
  }

  public static ZxError buildError(JSONObject errorObject) throws JSONException
  {
    String code = errorObject.getString(ZxError.KEY_CODE);
    JSONObject details = errorObject.getJSONObject(ZxError.KEY_DETAILS);
    //String message = errorObject.getString(KEY_MESSAGE);

    SeverityLevel severityLevel = SeverityLevel.WARNING;
    ErrorCode errorCode = ErrorCodesGeneric.fromString(code);

    ZxError error = new ZxError(severityLevel, errorCode);

    for (String detail : details.keySet())
    {
      error.setDetail(detail, details.getString(detail));
    }

    JSONArray jsonTrace = errorObject.getJSONArray(ZxError.KEY_STACKTRACE);
    StackTraceElement[] stackTrace = new StackTraceElement[jsonTrace.length()];

    if(jsonTrace.length() > 0)
    {
      for (int i = 0; i < jsonTrace.length(); i++)
      {
        /* Here an example showed on Browser as JSONObject
        className: "com.zextras.lib.PapiHandlerProviderMap"
        fileName: "PapiHandlerProviderMap.java"
        lineNumber: 15
        methodName: "getHandler"
        nativeMethod: false (a native method has lineNumber == -2)
        */
        JSONObject traceRow = (JSONObject) jsonTrace.get(i);
        String declaringClass = traceRow.optString("className","null");
        String methodName     = traceRow.optString("methodName","null");
        String fileName       = traceRow.optString("fileName","null");
        int lineNumber        = traceRow.optInt("lineNumber", 0);

        StackTraceElement newEl = new StackTraceElement(
          declaringClass,
          methodName,
          fileName,
          lineNumber
        );
        stackTrace[i] = newEl;
      }
    }
    else
    {
      StackTraceElement newEl = new StackTraceElement(
        "null",
        "null",
        "null",
        0
      );
      stackTrace = new StackTraceElement[1];
      stackTrace[0] = newEl;
    }
    error.setStackTrace(stackTrace);

    return error;
  }
}
