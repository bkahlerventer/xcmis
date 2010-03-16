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

package org.xcmis.spi.object.impl;

import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.AllowableActions;
import org.xcmis.spi.Rendition;
import org.xcmis.spi.object.ChangeInfo;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.ObjectInfo;
import org.xcmis.spi.object.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CmisObjectImpl implements CmisObject
{

   private Properties properties;

   private List<AccessControlEntry> acl;

   private boolean exactACL;

   private Set<String> policyIds;

   private List<CmisObject> relationships;

   private List<Rendition> renditions;

   private AllowableActions allowableActions;

   private ChangeInfo changeInfo;

   private ObjectInfo objectInfo;

   private String pathSegment;

   public CmisObjectImpl()
   {
   }

   public CmisObjectImpl(Properties properties, List<AccessControlEntry> acl, boolean exactACL, Set<String> policyIds,
      List<CmisObject> relationships, List<Rendition> renditions, AllowableActions allowableActions,
      ChangeInfo changeInfo, ObjectInfo objectInfo, String pathSegment)
   {
      this.properties = properties;
      this.acl = acl;
      this.exactACL = exactACL;
      this.policyIds = policyIds;
      this.relationships = relationships;
      this.renditions = renditions;
      this.allowableActions = allowableActions;
      this.changeInfo = changeInfo;
      this.objectInfo = objectInfo;
      this.pathSegment = pathSegment;
   }

   /**
    * {@inheritDoc}
    */
   public List<AccessControlEntry> getACL()
   {
      if (acl == null)
         acl = new ArrayList<AccessControlEntry>();
      return acl;
   }

   /**
    * {@inheritDoc}
    */
   public AllowableActions getAllowableActions()
   {
      return allowableActions;
   }

   /**
    * {@inheritDoc}
    */
   public ChangeInfo getChangeInfo()
   {
      return changeInfo;
   }

   /**
    * {@inheritDoc}
    */
   public ObjectInfo getObjectInfo()
   {
      return objectInfo;
   }

   /**
    * {@inheritDoc}
    */
   public String getPathSegment()
   {
      return pathSegment;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<String> getPolicyIds()
   {
      if (policyIds == null)
         policyIds = new HashSet<String>();
      return policyIds;
   }

   /**
    * {@inheritDoc}
    */
   public Properties getProperties()
   {
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObject> getRelationship()
   {
      if (relationships == null)
         relationships = new ArrayList<CmisObject>();
      return relationships;
   }

   /**
    * {@inheritDoc}
    */
   public List<Rendition> getRenditions()
   {
      if (renditions == null)
         renditions = new ArrayList<Rendition>();
      return renditions;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isExactACL()
   {
      return exactACL;
   }

   public void setAllowableActions(AllowableActions allowableActions)
   {
      this.allowableActions = allowableActions;
   }

   public void setChangeInfo(ChangeInfo changeInfo)
   {
      this.changeInfo = changeInfo;
   }

   public void setObjectInfo(ObjectInfo objectInfo)
   {
      this.objectInfo = objectInfo;
   }

   public void setProperties(Properties properties)
   {
      this.properties = properties;
   }

}
