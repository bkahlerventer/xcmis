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
 * Description of SQL query.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Query.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class Query
{

   private final boolean searchAllVersions;

   private final String statement;

   public Query(String statement, boolean searchAllVersion)
   {
      this.statement = statement;
      this.searchAllVersions = searchAllVersion;
   }

   /**
    * @return SQL statement
    */
   public String getStatement()
   {
      return statement;
   }

   /**
    * @return TRUE if all versions must be searched. If FALSE then only latest
    *            versions will be searched. 
    */
   public boolean isSearchAllVersions()
   {
      return searchAllVersions;
   }
   
}
