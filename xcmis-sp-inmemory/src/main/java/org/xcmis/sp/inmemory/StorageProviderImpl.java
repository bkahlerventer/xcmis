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

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.Connection;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.StorageProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageProviderImpl implements StorageProvider
{

   public static class StorageProviderConfig
   {

      /**
       * The list of storages configuration.
       */
      private List<StorageConfiguration> configs;

      /**
       * @return the list of storages configuration
       */
      public List<StorageConfiguration> getConfigs()
      {
         if (configs == null)
         {
            configs = new ArrayList<StorageConfiguration>();
         }
         return configs;
      }

      /**
       * @param configs the list of storages configuration
       */
      public void setConfigs(List<StorageConfiguration> configs)
      {
         this.configs = configs;
      }
   }

   private static final Log LOG = ExoLogger.getLogger(StorageProviderImpl.class);

   private final Map<String, StorageImpl> storages = new HashMap<String, StorageImpl>();

   public StorageProviderImpl(InitParams initParams)
   {
      if (initParams != null)
      {
         ObjectParameter param = initParams.getObjectParam("configs");

         if (param == null)
         {
            LOG.error("Init-params does not contain configuration for any CMIS repository.");
         }

         StorageProviderConfig confs = (StorageProviderConfig)param.getObject();

         for (StorageConfiguration conf : confs.getConfigs())
         {
            storages.put(conf.getId(), new StorageImpl(conf));
         }
      }
      else
      {
         LOG.error("Not found configuration for any storages.");
      }
   }

   public Connection getConnection(String storageId)
   {
      StorageImpl storage = storages.get(storageId);
      if (storage == null)
      {
         throw new InvalidArgumentException("CMIS repository " + storageId + " does not exists.");
      }

      return new InmemConnection(storage);
   }

   public Connection getConnection(String storageId, String user, String password) throws LoginException,
      InvalidArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Set<String> getStorageIDs()
   {
      return Collections.unmodifiableSet(storages.keySet());
   }

}