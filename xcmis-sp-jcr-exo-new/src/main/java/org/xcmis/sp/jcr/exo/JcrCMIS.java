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

package org.xcmis.sp.jcr.exo;

import org.xcmis.spi.CMIS;

/**
 * Constants.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface JcrCMIS extends CMIS
{

   String EXO_CMIS_NS_URI = "http://www.exoplatform.com/jcr/cmis/1.0";
   
   // --- CMIS node-types ---

   String CMIS_MIX_OBJECT = "cmis:object";

   String CMIS_MIX_DOCUMENT = "cmis:document";
   
   String CMIS_MIX_FOLDER = "cmis:folder";

   String CMIS_NT_RELATIONSHIP = "cmis:relationship";

   String CMIS_NT_POLICY = "cmis:policy";

   String CMIS_NT_RENDITION = "cmis:rendition";
   
   String CMIS_SYSTEM_NODETYPE = "xcmis:system";
   
   // --- CMIS system ---

   String ROOT_FOLDER_ID = org.exoplatform.services.jcr.impl.Constants.ROOT_UUID;
   
   String CMIS_SYSTEM = "xcmis:system";

   String CMIS_RELATIONSHIPS = "xcmis:relationships";

   String CMIS_WORKING_COPIES = "xcmis:workingCopies";

   // --- Renditions ---
   
   String CMIS_RENDITION_STREAM = "xcmis:renditionStream";

   String CMIS_RENDITION_MIME_TYPE = "xcmis:renditionMimeType";

   String CMIS_RENDITION_KIND = "xcmis:renditionKind";

   String CMIS_RENDITION_HEIGHT = "xcmis:renditionHeight";

   String CMIS_RENDITION_WIDTH = "xcmis:renditionWidth";
   
   // ---
   
   String CMIS_LATEST_VERSION = "xcmis:latestVersion";

   // TODO =============== move in configuration ===============

   String DEFAULT_FOLDER_NAME = "New Folder";

   String DEFAULT_DOCUMENT_NAME = "New Document";

   String DEFAULT_POLICY_NAME = "New Policy";

   // ===========================================================

   // JCR stuff

   String NT_FROZEN_NODE = "nt:frozenNode";

   String NT_FILE = "nt:file";

   String NT_FOLDER = "nt:folder";

   String NT_RESOURCE = "nt:resource";

   String NT_UNSTRUCTURED = "nt:unstructured";

   String NT_VERSION = "nt:version";

   String NT_VERSION_HISTORY = "nt:versionHistory";

   String MIX_VERSIONABLE = "mix:versionable";

   String JCR_CONTENT = "jcr:content";

   String JCR_CREATED = "jcr:created";

   String JCR_FROZEN_NODE = "jcr:frozenNode";

   String JCR_FROZEN_PRIMARY_TYPE = "jcr:frozenPrimaryType";

   String JCR_DATA = "jcr:data";

   String JCR_LAST_MODIFIED = "jcr:lastModified";

   String JCR_MIMETYPE = "jcr:mimeType";

   String EXO_PRIVILEGABLE = "exo:privilegeable";

}
