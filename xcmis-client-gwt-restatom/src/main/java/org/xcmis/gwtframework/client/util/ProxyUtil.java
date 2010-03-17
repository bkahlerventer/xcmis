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

package org.xcmis.gwtframework.client.util;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window.Location;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProxyUtil
{

   private static native String getProxyServiceContext() /*-{
          return $wnd.proxyServiceContext;
       }-*/;

   private static String getCurrentHost()
   {
//      String moduleBaseURL = GWT.getModuleBaseURL();
//      String currentHost = moduleBaseURL.substring(0, moduleBaseURL.indexOf("//") + 2);
//      moduleBaseURL = moduleBaseURL.substring(moduleBaseURL.indexOf("//") + 2);
//      currentHost += moduleBaseURL.substring(0, moduleBaseURL.indexOf("/"));
      String currentHost = Location.getProtocol() + "//" + Location.getHost();
      return currentHost;
   }

   public static String getCheckedURL(String url)
   {
      String proxyServiceContext = getProxyServiceContext();
      if (proxyServiceContext == null || "".equals(proxyServiceContext))
      {
         return url;
      }

      if (!(url.startsWith("http://") || url.startsWith("https://")))
      {
         return url;
      }

      String currentHost = getCurrentHost();
      System.out.println("current host [" + currentHost + "]");

      if (url.startsWith(currentHost))
      {
         return url;
      }
      
      

      String proxyURL = proxyServiceContext + "?url=" + URL.encodeComponent(url);
      System.out.println("proxy url: " + proxyURL);
      return proxyURL;
   }

}
