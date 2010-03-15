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

package org.xcmis.spi.impl;

import org.xcmis.spi.AccessControlEntry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AccessControlEntryImpl implements AccessControlEntry
{

   private String principal;
   
   private Set<String> permissions;
   
   public AccessControlEntryImpl()
   {
   }

   public AccessControlEntryImpl(String principal, Set<String> permissions)
   {
      this.principal = principal;
      this.permissions = permissions;
   }

   public Collection<String> getPermissions()
   {
      if (permissions == null)
         permissions = new HashSet<String>();
      return permissions;
   }

   public String getPrincipal()
   {
      return principal;
   }

   public void setPermissions(Set<String> permissions)
   {
      this.permissions = permissions;
   }

   public void setPrincipal(String principal)
   {
      this.principal = principal;
   }

}
