/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.qom.column;

import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;
import org.xcmis.search.qom.AbstractQueryObjectModelNode;

import javax.jcr.query.qom.Column;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: ColumnImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ColumnImpl extends AbstractQueryObjectModelNode implements Column
{
   /**
    * Column name; must be null if <code>getPropertyName</code> is null and non-null otherwise.
    */
   private final String columnName;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * Property name, or null to include a column for each single-value non-residual property of the
    * selector's node type.
    */
   private final InternalQName propertyName;

   /**
    * Name of the selector.
    */
   private final InternalQName selectorName;

   /**
    * @param selectorName - name of the selector.
    * @param columnName - column name.
    * @param propertyName - name of the property.
    */
   public ColumnImpl(LocationFactory locationFactory, InternalQName selectorName, InternalQName propertyName,
      String columnName)
   {
      super(locationFactory);
      this.selectorName = selectorName;
      this.columnName = columnName;
      this.propertyName = propertyName;
   }

   /**
    * {@inheritDoc}
    */
   public Object accept(QueryObjectModelVisitor visitor, Object context) throws Exception
   {
      return visitor.visit(this, context);
   }

   /**
    * {@inheritDoc}
    */
   public String getColumnName()
   {
      return columnName;
   }

   /**
    * {@inheritDoc}
    */
   public String getPropertyName()
   {
      return getJCRName(propertyName);
   }

   public InternalQName getPropertyQName()
   {
      return propertyName;
   }

   /**
    * {@inheritDoc}
    */
   public String getSelectorName()
   {
      return getJCRName(selectorName);
   }
}
