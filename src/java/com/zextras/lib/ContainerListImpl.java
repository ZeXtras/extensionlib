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

package com.zextras.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.container.ContainerModule;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nonnull;
import com.zextras.lib.json.KindObjectMapper;

public abstract class ContainerListImpl<T>  implements ContainerList<T>, Serializable
{
  protected LinkedList<T> mList;

  private static final ObjectWriter sJsonWriter = (new KindObjectMapper()).writer();

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null)
    {
      return false;
    }

    ContainerListImpl that = (ContainerListImpl) o;

    if (!mList.isEmpty())
    {
      return mList.equals(that.mList);
    }
    else
    {
      return that.mList.isEmpty();
    }
  }

  @Override
  public int hashCode()
  {
    return mList != null ? mList.hashCode() : 0;
  }

  public ContainerListImpl()
  {
    mList = new LinkedList<T>();
  }

  public ContainerListImpl(Iterable<T> value)
  {
    mList = new LinkedList<T>();
    for (T item : value)
    {
      mList.add(item);
    }
  }

  @Override
  public int size()
  {
    return mList.size();
  }

  @Override
  public boolean isEmpty()
  {
    return mList.size() == 0;
  }

  @Override
  public boolean contains(Object o)
  {
    for (T entry : mList)
    {
     if (entry.equals(o))
     {
       return true;
     }
    }
    return false;
  }

  @Nonnull
  @Override
  public Object[] toArray()
  {
    return mList.toArray();
  }

  @Nonnull
  @Override
  public <T> T[] toArray(T[] a)
  {
    return mList.toArray(a);
  }

  @Override
  public boolean add(T value)
  {
    return mList.add(value);
  }

  @Override
  public boolean remove(Object o)
  {
    return mList.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    return mList.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c)
  {
    return mList.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c)
  {
    return mList.addAll(index, c);
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    return mList.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    return mList.retainAll(c);
  }

  @Override
  public void clear()
  {
    mList.clear();
  }

  @Override
  public T get(int index)
  {
    return mList.get(index);
  }

  @Override
  public T set(int index, T element)
  {
    return mList.set(index, element);
  }

  @Override
  public void add(int index, T element)
  {
    mList.add(index, element);
  }

  @Override
  public T remove(int index)
  {
    return mList.remove(index);
  }

  @Override
  public int indexOf(Object o)
  {
    return mList.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o)
  {
    return mList.lastIndexOf(o);
  }

  @Nonnull
  @Override
  public ListIterator<T> listIterator()
  {
    return mList.listIterator();
  }

  @Nonnull
  @Override
  public ListIterator<T> listIterator(int index)
  {
    return mList.listIterator(index);
  }

  @Nonnull
  @Override
  public List<T> subList(int fromIndex, int toIndex)
  {
    return mList.subList(fromIndex, toIndex);
  }

  @Override
  public Iterator<T> iterator()
  {
    return mList.iterator();
  }

  public abstract String getType();

  public String toJsonString()
  {
    String s;
    try
    {
      s = sJsonWriter.writeValueAsString( this );
    }
    catch( Exception ex )
    {
      return null;
    }
    return s;
  }

  @Override
  public String toString()
  {
    return toJsonString();
  }
}
