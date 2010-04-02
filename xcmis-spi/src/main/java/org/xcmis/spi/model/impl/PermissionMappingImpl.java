/*
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

package org.xcmis.spi.model.impl;

import org.xcmis.spi.model.PermissionMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PermissionMappingImpl implements PermissionMapping
{

   private String key;

   private Set<String> permissions;

   public PermissionMappingImpl(String key, Collection<String> permissions)
   {
      this.key = key;
      if (permissions != null)
         this.permissions = new HashSet<String>(permissions);
   }

   /**
    * {@inheritDoc}
    */
   public String getKey()
   {
      return key;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<String> getPermissions()
   {
      if (permissions == null)
         return Collections.emptyList();
      return Collections.unmodifiableSet(permissions);
   }

}
