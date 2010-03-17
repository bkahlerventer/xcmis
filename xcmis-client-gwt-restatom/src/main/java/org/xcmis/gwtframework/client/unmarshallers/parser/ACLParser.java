/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.gwtframework.client.unmarshallers.parser;

import org.xcmis.gwtframework.client.CmisNameSpace;
import org.xcmis.gwtframework.client.model.acl.CmisAccessControlEntryType;
import org.xcmis.gwtframework.client.model.acl.CmisAccessControlListType;
import org.xcmis.gwtframework.client.model.acl.CmisAccessControlPrincipalType;

import com.google.gwt.xml.client.Node;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class ACLParser
{
   
   /**
    * Constructor.
    */
   protected ACLParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Parse xml to get {@link CmisAccessControlListType}.
    * 
    * @param aclNode ACL node
    * @param acl ACL
    */
   public static void parse(Node aclNode, CmisAccessControlListType acl)
   {
      for (int i = 0; i < aclNode.getChildNodes().getLength(); i++)
      {
         Node node = aclNode.getChildNodes().item(i);
         if (node.getNodeName().equals(CmisNameSpace.CMIS_PERMISSION))
         {
            acl.getPermission().add(getACE(node));
         }
      }
   }

   /**
    * Get ACE from xml element.
    * 
    * @param aceNode ACE node
    * @return {@link CmisAccessControlEntryType}
    */
   public static CmisAccessControlEntryType getACE(Node aceNode)
   {
      CmisAccessControlEntryType accessControlEntry = new CmisAccessControlEntryType();
      for (int i = 0; i < aceNode.getChildNodes().getLength(); i++)
      {
         Node node = aceNode.getChildNodes().item(i);
         if (node.getNodeName().equals(CmisNameSpace.CMIS_PERMISSION))
         {
            String value = node.getFirstChild().getNodeValue();
            accessControlEntry.getPermission().add(value);
         }
         else if (node.getNodeName().equals(CmisNameSpace.CMIS_DIRECT))
         {
            String value = node.getFirstChild().getNodeValue();
            accessControlEntry.setDirect(Boolean.parseBoolean(value));
         }
         else if (node.getNodeName().equals(CmisNameSpace.CMIS_PRINCIPAL))
         {
            Node childNode = node.getFirstChild();
            if (childNode.getNodeName().equals(CmisNameSpace.CMIS_PRINCIPAL_ID))
            {
               String value = childNode.getFirstChild().getNodeValue();
               CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
               principal.setPrincipalId(value);
               accessControlEntry.setPrincipal(principal);
            }
         }
      }
      return accessControlEntry;
   }
}
