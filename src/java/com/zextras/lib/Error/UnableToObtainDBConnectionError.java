package com.zextras.lib.Error;

import com.zextras.lib.log.SeverityLevel;

public class UnableToObtainDBConnectionError extends ZxError
{
  public UnableToObtainDBConnectionError()
  {
    super(SeverityLevel.ERROR, ErrorCodesGeneric.UNABLE_TO_OBTAIN_DB_CONNECTION);
  }
}
