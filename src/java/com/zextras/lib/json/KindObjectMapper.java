package com.zextras.lib.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KindObjectMapper extends ObjectMapper
{
  public KindObjectMapper()
  {
    super();
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
    configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
  }
}
