/*
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.content.command.query;

import org.apache.commons.lang.Validate;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.VisitableCommand;
import org.xcmis.search.content.interceptors.Visitor;
import org.xcmis.search.model.Limit;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.Selector;

import java.util.List;

/**
 * Command for execution query with one single {@link Selector} filtered by
 * {@link Constraint}, limited and ordered if needed.
 * 
 */
public class ExecuteSelectorCommand implements VisitableCommand
{
   private final Selector selector;

   private final Constraint constrain;

   private final Limit limit;

   private final List<Ordering> orderings;

   /**
    * @param source
    * @param constrain
    * @param limit
    * @param orderings
    */
   public ExecuteSelectorCommand(Selector selector, Constraint constrain, Limit limit, List<Ordering> orderings)
   {
      Validate.notNull(selector, "The selector argument may not be null");
      Validate.notNull(constrain, "The constrain argument may not be null");
      Validate.notNull(limit, "The limit argument may not be null");
      Validate.notNull(orderings, "The orderings argument may not be null");
      this.selector = selector;
      this.constrain = constrain;
      this.limit = limit;
      this.orderings = orderings;
   }

   /**
    * @see org.xcmis.search.content.command.VisitableCommand#acceptVisitor(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.interceptors.Visitor)
    */
   public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable
   {
      return visitor.visitExecuteSelectorCommand(ctx, this);
   }

}
