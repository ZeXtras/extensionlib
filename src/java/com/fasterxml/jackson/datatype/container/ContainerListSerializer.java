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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.zextras.lib.Container;
import com.zextras.lib.ContainerList;

import java.io.IOException;
import java.lang.reflect.Type;

public class ContainerListSerializer extends StdSerializer<ContainerList>
{
  public final static ContainerListSerializer instance = new ContainerListSerializer();

  public ContainerListSerializer()
  {
    super(ContainerList.class);
  }

  @Override
  public void serialize(ContainerList value, JsonGenerator jgen, SerializerProvider provider)
    throws IOException, JsonGenerationException
  {
    jgen.writeStartArray();
    serializeContents(value, jgen, provider);
    jgen.writeEndArray();
  }

  @Override
  public void serializeWithType(
    ContainerList value, JsonGenerator jgen, SerializerProvider provider,
    TypeSerializer typeSer
  )
    throws IOException, JsonGenerationException
  {
    typeSer.writeTypePrefixForArray(value, jgen);
    serializeContents(value, jgen, provider);
        typeSer.writeTypeSuffixForArray(value, jgen);
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
        throws JsonMappingException
    {
        return createSchemaNode("array", true);
    }

    protected void serializeContents(ContainerList value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        for (int i = 0, len = value.size(); i < len; ++i) {
            Object ob = value.get(i);
            if (ob == null) {
                jgen.writeNull();
                continue;
            }
            Class<?> cls = ob.getClass();
            if (cls == Container.class) {
                ContainerSerializer.instance.serialize((Container) ob, jgen, provider);
            } else if (cls == ContainerList.class) {
                serialize((ContainerList) ob, jgen, provider);
            } else  if (cls == String.class) {
                jgen.writeString((String) ob);
            } else  if (cls == Integer.class) {
                jgen.writeNumber(((Integer) ob).intValue());
            } else  if (cls == Long.class) {
                jgen.writeNumber(((Long) ob).longValue());
            } else  if (cls == Boolean.class) {
                jgen.writeBoolean(((Boolean) ob).booleanValue());
            } else  if (cls == Double.class) {
                jgen.writeNumber(((Double) ob).doubleValue());
            } else if (Container.class.isAssignableFrom(cls)) { // sub-class
                ContainerSerializer.instance.serialize((Container) ob, jgen, provider);
            } else if (ContainerList.class.isAssignableFrom(cls)) { // sub-class
                serialize((ContainerList) ob, jgen, provider);
            } else if (ContainerList.class.isAssignableFrom(cls)) { // sub-class
                ContainerListSerializer.instance.serialize((ContainerList) ob, jgen, provider);
            } else {
                provider.defaultSerializeValue(ob, jgen);
            }
        }
    }
}
