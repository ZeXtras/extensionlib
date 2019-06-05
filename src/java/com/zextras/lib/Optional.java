/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2017 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
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
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package com.zextras.lib;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nullable;

public class Optional<T>
{
  public final static Optional sEmptyInstance = new Optional();

  private final T mValue;

  public Optional()
  {
    mValue = null;
  }

  @JsonCreator
  public Optional(
    @JsonProperty("value") T value
  )
  {
    mValue = value;
  }

  @JsonIgnore
  public T getValue()
  {
    if(! hasValue())
    {
      throw new RuntimeException();
    }
    return mValue;
  }

  @JsonIgnore
  public T get()
  {
    if(! hasValue())
    {
      throw new RuntimeException();
    }
    return mValue;
  }

  public static <X> Optional<X> of(X value)
  {
    if( value == null )
    {
      return empty();
    }
    else
    {
      return new Optional<>(value);
    }
  }

  public static <X> Optional<X> of(Optional<X> value)
  {
    if( value == null )
    {
      return empty();
    }
    else
    {
      return value;
    }
  }

  public static <X> Optional<X> empty()
  {
    return sEmptyInstance;
  }

  public boolean hasValue()
  {
    return mValue != null;
  }

  @Nullable
  @JsonGetter(value="value")
  public T rawGetValue()
  {
    return mValue;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    Optional that = (Optional) o;

    if (mValue != null ? !mValue.equals(that.mValue) : that.mValue != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return mValue != null ? mValue.hashCode() : 0;
  }

  public T optValue(T def)
  {
    return mValue != null ? mValue : def;
  }

  @Override
  public String toString()
  {
    if( hasValue() )
    {
      return mValue.toString();
    }
    else
    {
      return "null";
    }
  }
}
