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

package com.fasterxml.jackson.datatype.container;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.zextras.lib.ContainerList;
import com.zextras.lib.ContainerListImpl;

import java.io.IOException;

public class ContainerListDeserializer extends StdDeserializer<ContainerList>
{
  public final static ContainerListDeserializer instance = new ContainerListDeserializer();

  public ContainerListDeserializer()
  {
    super(ContainerList.class);
  }

  @Override
  public ContainerList deserialize(JsonParser jp, DeserializationContext ctxt)
    throws IOException, JsonProcessingException
  {
    ContainerList list = new ContainerListImpl()
    {
      @Override
      public String getType()
      {
        throw new UnsupportedOperationException();
      }
    };

    JsonToken t;
    while ((t = jp.nextToken()) != JsonToken.END_ARRAY)
    {
      switch (t)
      {
        case START_ARRAY:
          list.add(deserialize(jp, ctxt));
          continue;
        case START_OBJECT:
                list.add(ContainerDeserializer.instance.deserialize(jp, ctxt));
                continue;
            case VALUE_STRING:
                list.add(jp.getText());
                continue;
            case VALUE_NULL:
                list.add(null);
                continue;
            case VALUE_TRUE:
                list.add(Boolean.TRUE);
                continue;
            case VALUE_FALSE:
                list.add(Boolean.FALSE);
                continue;
            case VALUE_NUMBER_INT:
                list.add(jp.getNumberValue().longValue());
                continue;
            case VALUE_NUMBER_FLOAT:
                list.add(jp.getNumberValue());
                continue;
            case VALUE_EMBEDDED_OBJECT:
                throw ctxt.mappingException("Object embedded in a ContainerList - not supported");
            }
            throw ctxt.mappingException("Urecognized or unsupported JsonToken type: "+t);
        }
        return list;
    }
}
