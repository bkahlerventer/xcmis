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

package org.xcmis.restatom;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: AtomCmisBootstrapListener.java 1711 2010-09-17 15:10:40Z
 *          andrew00x $
 */
public class AtomCmisBootstrapListener implements ServletContextListener
{

   /**
    * {@inheritDoc}
    */
   public void contextDestroyed(ServletContextEvent sce)
   {
   }

   /**
    * {@inheritDoc}
    */
   public void contextInitialized(ServletContextEvent sce)
   {
      ServletContext sctx = sce.getServletContext();
      ProviderImpl provider = new ProviderImpl();
      provider.init(AbderaFactory.getInstance(), new HashMap<String, String>());
      sctx.setAttribute(ProviderImpl.class.getName(), provider);
   }

}
