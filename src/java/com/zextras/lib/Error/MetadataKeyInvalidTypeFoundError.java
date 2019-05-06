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

public class MetadataKeyInvalidTypeFoundError extends RuntimeException
{
  private static final String KEY_INVALID_TYPE_FOUND = " found but it is not a ";

  public enum ValueType
  {
    STRING("String"),
    BOOLEAN("Boolean"),
    CONTAINER("Container"),
    LONG("Long"),
    CONTAINER_LIST("ContainerList"),
    INT("Integer");

    private final String mValueType;

    ValueType(String valueType)
    {
      mValueType = valueType;
    }

    public String getValueType()
    {
      return mValueType;
    }
  }

  public MetadataKeyInvalidTypeFoundError(String key, ValueType type, Object value)
  {
    super(key + KEY_INVALID_TYPE_FOUND + type.getValueType() + " value[" + value.getClass().getSimpleName() + "] : " + value.toString());
  }
}
