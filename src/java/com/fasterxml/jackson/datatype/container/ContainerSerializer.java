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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.zextras.lib.Container;
import com.zextras.lib.ContainerList;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class ContainerSerializer extends StdSerializer<Container>
{
  public final static ContainerSerializer instance = new ContainerSerializer();

  public ContainerSerializer()
  {
    super(Container.class);
  }

  @Override
  public void serialize(Container value, JsonGenerator jgen, SerializerProvider provider)
    throws IOException, JsonGenerationException
  {
    jgen.writeStartObject();
    serializeContents(value, jgen, provider);
    jgen.writeEndObject();
  }

  @Override
  public void serializeWithType(
    Container value, JsonGenerator jgen, SerializerProvider provider,
    TypeSerializer typeSer
  )
    throws IOException, JsonGenerationException
  {
    typeSer.writeTypePrefixForObject(value, jgen);
    serializeContents(value, jgen, provider);
    typeSer.writeTypeSuffixForObject(value, jgen);
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
        throws JsonMappingException
    {
        return createSchemaNode("object", true);
    }

    protected void serializeContents(Container value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        Map<String,Object> privateMap = value.getPrivateMap();

        Iterator<?> it = privateMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object ob = privateMap.get(key);

            if (ob == null) {
                if (provider.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES)) {
                    jgen.writeNullField(key);
                }
                continue;
            }
            jgen.writeFieldName(key);
            Class<?> cls = ob.getClass();
            if (cls == Container.class) {
                serialize((Container) ob, jgen, provider);
            } else if (cls == ContainerList.class) {
                ContainerListSerializer.instance.serialize((ContainerList) ob, jgen, provider);
            } else  if (cls == String.class) {
                jgen.writeString((String) ob);
            } else  if (cls == Integer.class) {
                jgen.writeNumber(((Integer) ob).intValue());
            } else  if (cls == Long.class) {
                jgen.writeNumber(((Long) ob).longValue());
            } else  if (cls == Boolean.class) {
                jgen.writeBoolean((Boolean) ob);
            } else  if (cls == Double.class) {
                jgen.writeNumber((Double) ob);
            } else if (cls == ContainerList.class) {
                ContainerListSerializer.instance.serialize((ContainerList) ob, jgen, provider);
            } else if (Container.class.isAssignableFrom(cls)) { // sub-class
                serialize((Container) ob, jgen, provider);
            } else if (ContainerList.class.isAssignableFrom(cls)) { // sub-class
                ContainerListSerializer.instance.serialize((ContainerList) ob, jgen, provider);
            } else {
                provider.defaultSerializeValue(ob, jgen);
            }
        }
    }
}
