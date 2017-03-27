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

package com.zextras.lib.filters;

import org.openzal.zal.lib.Filter;

import java.util.Iterator;
import java.util.NoSuchElementException;


//tested by FilteredItemInfoIteratorTest
public class FilteredIterator<T> implements Iterator<T>
{
  private final Filter      mFilter;
  private final Iterator<T> mIterator;
  private       T           mNextItemInfo;

  public FilteredIterator(Filter<T> filter, Iterator<T> iterator)
  {
    mFilter = filter;
    mIterator = iterator;
  }

  @Override
  public boolean hasNext()
  {
    goToNext();

    return mNextItemInfo != null;
  }

  @Override
  public T next()
  {
    if(mNextItemInfo == null)
    {
      goToNext();

      if( mNextItemInfo == null ) {
        throw new NoSuchElementException();
      }
    }

    T toReturn = mNextItemInfo;
    mNextItemInfo = null;
    return toReturn;
  }

  private void goToNext()
  {
    if(mNextItemInfo != null)
    {
      return;
    }

    while(mIterator.hasNext())
    {
      T item = mIterator.next();

      if(!mFilter.filterOut(item))
      {
        mNextItemInfo = item;
        break;
      }
    }
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}
