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
package org.xcmis.search.model.source.join;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public enum JoinType {
   INNER("INNER JOIN"), LEFT_OUTER("LEFT OUTER JOIN"), RIGHT_OUTER("RIGHT OUTER JOIN")
   //, FULL_OUTER("FULL OUTER JOIN")
   //, CROSS(      "CROSS JOIN")
   ;

   private static final Map<String, JoinType> TYPE_BY_SYMBOL;
   static
   {
      Map<String, JoinType> typesBySymbol = new HashMap<String, JoinType>();
      for (JoinType type : JoinType.values())
      {
         typesBySymbol.put(type.getSymbol().toUpperCase(), type);
      }
      TYPE_BY_SYMBOL = Collections.unmodifiableMap(typesBySymbol);
   }

   private final String symbol;

   private JoinType(String symbol)
   {
      this.symbol = symbol;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.model.Readable#getString()
    */
   public String getString()
   {
      return getSymbol();
   }

   /**
    * @return symbol
    */
   public String getSymbol()
   {
      return symbol;
   }

   /**
    * Check if this join type is an outer join.
    * 
    * @return true if left/right/full outer, or false otherwise
    */
   public boolean isOuter()
   {
      return this.equals(LEFT_OUTER) || this.equals(RIGHT_OUTER)
      //|| this.equals(FULL_OUTER) 
      ;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Enum#toString()
    */
   @Override
   public String toString()
   {
      return symbol;
   }

   /**
    * Attempt to find the JoinType given a symbol. The matching is done independent of case.
    * 
    * @param symbol the symbol
    * @return the JoinType having the supplied symbol, or null if there is no JoinType with the supplied symbol
    * @throws IllegalArgumentException if the symbol is null
    */
   public static JoinType forSymbol(String symbol)
   {

      return TYPE_BY_SYMBOL.get(symbol.toUpperCase());
   }
}
