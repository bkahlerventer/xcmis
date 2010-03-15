/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.sp.inmemory;

import org.xcmis.spi.object.ItemsIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: SimpleItemsIterator.java 2 2010-02-04 17:21:49Z andrew00x $
 * 
 * @param <T> 
 */
public class SimpleItemsIterator<T> implements ItemsIterator<T>
{

   /** Back-end iterator. */
   private final Iterator<T> iter;

   /** Number of items in iterator. */
   private final long size;

   /**
    * @param list source collection
    */
   public SimpleItemsIterator(Collection<T> list)
   {
      this.size = list.size();
      this.iter = list.iterator();
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasNext()
   {
      return iter.hasNext();
   }

   /**
    * {@inheritDoc}
    */
   public T next()
   {
      return iter.next();
   }

   /**
    * {@inheritDoc}
    */
   public void remove()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public long size()
   {
      return size;
   }

   /**
    * {@inheritDoc}
    */
   public void skip(long skip) throws NoSuchElementException
   {
      while (skip-- > 0)
         iter.next();
   }

}
