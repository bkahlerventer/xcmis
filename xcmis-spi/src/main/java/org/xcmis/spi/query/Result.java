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

package org.xcmis.spi.query;

/**
 * Query result.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Result.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public interface Result
{

   /**
    * Query result object's id (Row in result virtual table).
    * 
    * @return id of object. 
    */
   String getObjectId();

   /**
    * Range of property names (columns) specified in the SELECT clause.
    *
    * @return set of property names or <code>null</code> that minds all properties
    *            will be included in and result.
    */
   String[] getPropertyNames();

   /**
    * @return score of result for CONTAINS() functions
    */
   Score getScore();

}
