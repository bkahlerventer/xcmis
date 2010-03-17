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

package org.xcmis.gwtframework.client.model.restatom;

import org.xcmis.gwtframework.client.model.type.CmisTypeDefinitionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class TypeList
{
   /**
    * Types.
    */
   private List<CmisTypeDefinitionType> types;

   /**
    * @return List containing {@link CmisTypeDefinitionType}
    */
   public List<CmisTypeDefinitionType> getTypes()
   {
      if (types == null)
      {
         types = new ArrayList<CmisTypeDefinitionType>();
      }
      return types;
   }

   /**
    * @param types types
    */
   public void setTypes(List<CmisTypeDefinitionType> types)
   {
      this.types = types;
   }

}
