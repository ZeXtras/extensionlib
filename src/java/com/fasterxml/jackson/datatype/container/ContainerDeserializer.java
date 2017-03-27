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
import com.zextras.lib.Container;
import com.zextras.lib.ContainerImpl;

import java.io.IOException;

public class ContainerDeserializer extends StdDeserializer<Container>
{
  public final static ContainerDeserializer instance = new ContainerDeserializer();

  public ContainerDeserializer()
  {
    super(Container.class);
  }

  @Override
  public Container deserialize(JsonParser jp, DeserializationContext ctxt)
    throws IOException, JsonProcessingException
  {
    Container ob = new ContainerImpl();
    JsonToken t = jp.getCurrentToken();
    if (t == JsonToken.START_OBJECT)
    {
      t = jp.nextToken();
    }
    for (; t == JsonToken.FIELD_NAME; t = jp.nextToken())
    {
      String fieldName = jp.getCurrentName();
            t = jp.nextToken();
            switch (t) {
            case START_ARRAY:
                ob.putList(fieldName, ContainerListDeserializer.instance.deserialize(jp, ctxt));
                continue;
            case START_OBJECT:
                ob.putContainer(fieldName, deserialize(jp, ctxt));
                continue;
            case VALUE_STRING:
                ob.putString(fieldName, jp.getText());
                continue;
            case VALUE_NULL:
                continue;
            case VALUE_TRUE:
                ob.putBoolean(fieldName, Boolean.TRUE);
                continue;
            case VALUE_FALSE:
                ob.putBoolean(fieldName, Boolean.FALSE);
                continue;
            case VALUE_NUMBER_INT:
                ob.putLong(fieldName, jp.getLongValue());
                continue;
            case VALUE_NUMBER_FLOAT:
                ob.putString(fieldName, jp.getValueAsString());
                continue;
            case VALUE_EMBEDDED_OBJECT:
                throw ctxt.mappingException("Object embedded in a Container - not supported");
            }
            throw ctxt.mappingException("Urecognized or unsupported JsonToken type: "+t);
        }
        return ob;
    }
}
