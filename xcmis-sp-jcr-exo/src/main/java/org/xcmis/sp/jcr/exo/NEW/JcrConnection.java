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

package org.xcmis.sp.jcr.exo.NEW;

import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.spi.Storage;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.impl.BaseConnection;
import org.xcmis.spi.impl.PropertyFilter;
import org.xcmis.spi.impl.RenditionFilter;
import org.xcmis.spi.object.CmisObject;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JcrConnection extends BaseConnection
{

   private final Storage storage;

   public JcrConnection(Storage storage)
   {
      this.storage = storage;
   }

   protected CmisObject getCmisObject(ObjectData object, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeAcl,
      PropertyFilter parsedPropertyFilter, RenditionFilter parsedRenditionFilter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void close()
   {
      // TODO Auto-generated method stub
   }

   protected void validateChangeToken(ObjectData object, String changeToken) throws UpdateConflictException
   {
      // TODO Auto-generated method stub
   }

   @Override
   protected void checkConnection() throws IllegalStateException
   {
      // TODO Auto-generated method stub
   }

   public Storage getStorage()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
