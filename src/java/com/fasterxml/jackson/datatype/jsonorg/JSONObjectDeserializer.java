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

package com.fasterxml.jackson.datatype.jsonorg;

import java.io.IOException;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.zextras.lib.json.JSONException;
import com.zextras.lib.json.JSONObject;

public class JSONObjectDeserializer extends StdDeserializer<JSONObject>
{
    public final static JSONObjectDeserializer instance = new JSONObjectDeserializer();

    public JSONObjectDeserializer()
    {
        super(JSONObject.class);
    }

    @Override
    public JSONObject deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        JSONObject ob = new JSONObject();
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        for (; t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
            String fieldName = jp.getCurrentName();
            t = jp.nextToken();
            switch (t) {
            case START_ARRAY:
                ob.put(fieldName, JSONArrayDeserializer.instance.deserialize(jp, ctxt));
                continue;
            case START_OBJECT:
                ob.put(fieldName, deserialize(jp, ctxt));
                continue;
            case VALUE_STRING:
                ob.put(fieldName, jp.getText());
                continue;
            case VALUE_NULL:
                ob.put(fieldName, null);
                continue;
            case VALUE_TRUE:
                ob.put(fieldName, Boolean.TRUE);
                continue;
            case VALUE_FALSE:
                ob.put(fieldName, Boolean.FALSE);
                continue;
            case VALUE_NUMBER_INT:
                ob.put(fieldName, jp.getNumberValue());
                continue;
            case VALUE_NUMBER_FLOAT:
                ob.put(fieldName, jp.getNumberValue());
                continue;
            case VALUE_EMBEDDED_OBJECT:
                ob.put(fieldName, jp.getEmbeddedObject());
                continue;
            }
            throw ctxt.mappingException("Urecognized or unsupported JsonToken type: "+t);
        }
        return ob;
    }
}
