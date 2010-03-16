import org.junit.Before;
import org.junit.Test;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.query.parser.CmisQueryParser;

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

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class CmisSqlQueryParserTest
{

   private CmisQueryParser parser;

   @Before
   public void beforeEach()
   {
      parser = new CmisQueryParser();
   }

   // ----------------------------------------------------------------------------------------------------------------
   // parseQuery
   // ----------------------------------------------------------------------------------------------------------------

   @Test
   public void shouldParseNominalQueries() throws InvalidQueryException
   {
      parse("SELECT * FROM tableA");
      parse("SELECT column1 FROM tableA");
      parse("SELECT tableA.column1 FROM tableA");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA INNER JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA OUTER JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA LEFT OUTER JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA RIGHT OUTER JOIN tableB ON tableA.id = tableB.id");
   }

   @Test
   public void shouldParseQueriesWithNonSqlColumnNames() throws InvalidQueryException
   {
      parse("SELECT * FROM [exo:tableA]");
      parse("SELECT [jcr:column1] FROM [exo:tableA]");
      parse("SELECT 'jcr:column1' FROM 'exo:tableA'");
      parse("SELECT \"jcr:column1\" FROM \"exo:tableA\"");
   }

   @Test
   public void shouldParseQueriesSelectingFromAllTables() throws InvalidQueryException
   {
      parse("SELECT * FROM nt:base");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithNoFromClause() throws InvalidQueryException
   {
      parse("SELECT 'jcr:column1'");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithIncompleteFromClause() throws InvalidQueryException
   {
      parse("SELECT 'jcr:column1' FROM  ");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithUnmatchedSingleQuoteCharacters() throws InvalidQueryException
   {
      parse("SELECT 'jcr:column1' FROM \"exo:tableA'");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithUnmatchedDoubleQuoteCharacters() throws InvalidQueryException
   {
      parse("SELECT \"jcr:column1' FROM \"exo:tableA\"");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithUnmatchedBracketQuoteCharacters() throws InvalidQueryException
   {
      parse("SELECT [jcr:column1' FROM [exo:tableA]");
   }

   @Test
   public void shouldParseQueriesWithSelectStar() throws InvalidQueryException
   {
      parse("SELECT * FROM tableA");
      parse("SELECT tableA.* FROM tableA");
      parse("SELECT tableA.column1, tableB.* FROM tableA JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.*, tableB.column2 FROM tableA JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.*, tableB.* FROM tableA JOIN tableB ON tableA.id = tableB.id");
   }

   @Test
   public void shouldParseQueriesWithAllKindsOfJoins() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA INNER JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA OUTER JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA LEFT OUTER JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA RIGHT OUTER JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA FULL OUTER JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA CROSS JOIN tableB ON tableA.id = tableB.id");
   }

   @Test
   public void shouldParseQueriesWithMultipleJoins() throws InvalidQueryException
   {
      parse("SELECT * FROM tableA JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT * FROM tableA JOIN tableB ON tableA.id = tableB.id JOIN tableC ON tableA.id2 = tableC.id2");
   }

   @Test
   public void shouldParseQueriesWithEquiJoinCriteria() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON tableA.id = tableB.id");
      parse("SELECT * FROM tableA JOIN tableB ON tableA.id = tableB.id JOIN tableC ON tableA.id2 = tableC.id2");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseEquiJoinCriteriaMissingPropertyName() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON tableA = tableB.id");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseEquiJoinCriteriaMissingTableName() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON column1 = tableB.id");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseEquiJoinCriteriaMissingEquals() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON column1 tableB.id");
   }

   @Test
   public void shouldParseQueriesOnMultpleLines() throws InvalidQueryException
   {
      parse("SELECT * \nFROM tableA");
      parse("SELECT \ncolumn1 \nFROM tableA");
      parse("SELECT \ntableA.column1 \nFROM\n tableA");
      parse("SELECT tableA.\ncolumn1, \ntableB.column2 \nFROM tableA JOIN \ntableB ON tableA.id \n= tableB.\nid");
   }

   @Test
   public void shouldParseQueriesThatUseDifferentCaseForKeywords() throws InvalidQueryException
   {
      parse("select * from tableA");
      parse("SeLeCt * from tableA");
      parse("select column1 from tableA");
      parse("select tableA.column1 from tableA");
      parse("select tableA.column1, tableB.column2 from tableA join tableB on tableA.id = tableB.id");
   }

   @Test
   public void shouldParseUnionQueries() throws InvalidQueryException
   {
      parse("SELECT * FROM tableA UNION SELECT * FROM tableB");
      parse("SELECT * FROM tableA UNION ALL SELECT * FROM tableB");
      parse("SELECT * FROM tableA UNION SELECT * FROM tableB UNION SELECT * FROM tableC");
      parse("SELECT * FROM tableA UNION ALL SELECT * FROM tableB UNION SELECT * FROM tableC");
   }

   @Test
   public void shouldParseIntersectQueries() throws InvalidQueryException
   {
      parse("SELECT * FROM tableA INTERSECT SELECT * FROM tableB");
      parse("SELECT * FROM tableA INTERSECT ALL SELECT * FROM tableB");
      parse("SELECT * FROM tableA INTERSECT SELECT * FROM tableB INTERSECT SELECT * FROM tableC");
      parse("SELECT * FROM tableA INTERSECT ALL SELECT * FROM tableB INTERSECT ALL SELECT * FROM tableC");
   }

   @Test
   public void shouldParseExceptQueries() throws InvalidQueryException
   {
      parse("SELECT * FROM tableA EXCEPT SELECT * FROM tableB");
      parse("SELECT * FROM tableA EXCEPT ALL SELECT * FROM tableB");
      parse("SELECT * FROM tableA EXCEPT SELECT * FROM tableB EXCEPT SELECT * FROM tableC");
      parse("SELECT * FROM tableA EXCEPT ALL SELECT * FROM tableB EXCEPT ALL SELECT * FROM tableC");
   }

   // ----------------------------------------------------------------------------------------------------------------
   // parseSetQuery
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseSelect
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseFrom
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseJoinCondition
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseWhere
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseConstraint
   // ----------------------------------------------------------------------------------------------------------------

   //   @Test
   //   public void shouldParseConstraintFromStringWithValidExpressions()
   //   {
   //      assertParseConstraint("ISSAMENODE('/a/b')");
   //      assertParseConstraint("ISSAMENODE('/a/b') AND NOT(ISCHILDNODE('/parent'))");
   //      assertParseConstraint("ISSAMENODE('/a/b') AND (NOT(ISCHILDNODE('/parent')))");
   //      assertParseConstraint("ISSAMENODE('/a/b') AND (NOT(tableA.id < 1234)))");
   //   }
   //
   //   protected void assertParseConstraint(String expression)
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      parser.parseConstraint(tokens(expression), typeSystem, selector);
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseConstraint - between
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithValidBetweenExpressionUsing()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("tableA.id BETWEEN 'lower' AND 'upper'"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(Between.class)));
   //      Between between = (Between)constraint;
   //      assertThat(between.isLowerBoundIncluded(), is(true));
   //      assertThat(between.isUpperBoundIncluded(), is(true));
   //      assertThat(between.getOperand(), is(instanceOf(PropertyValue.class)));
   //      PropertyValue operand = (PropertyValue)between.getOperand();
   //      assertThat(operand.getSelectorName(), is(selector.getName()));
   //      assertThat(operand.getPropertyName(), is("id"));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat((Literal)between.getLowerBound(), is(literal("lower")));
   //      assertThat((Literal)between.getUpperBound(), is(literal("upper")));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithValidBetweenExpressionUsingExclusiveAndExclusive()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("tableA.id BETWEEN 'lower' EXCLUSIVE AND 'upper' EXCLUSIVE"), typeSystem,
   //            selector);
   //      assertThat(constraint, is(instanceOf(Between.class)));
   //      Between between = (Between)constraint;
   //      assertThat(between.isLowerBoundIncluded(), is(false));
   //      assertThat(between.isUpperBoundIncluded(), is(false));
   //      assertThat(between.getOperand(), is(instanceOf(PropertyValue.class)));
   //      PropertyValue operand = (PropertyValue)between.getOperand();
   //      assertThat(operand.getSelectorName(), is(selector.getName()));
   //      assertThat(operand.getPropertyName(), is("id"));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat((Literal)between.getLowerBound(), is(literal("lower")));
   //      assertThat((Literal)between.getUpperBound(), is(literal("upper")));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithValidBetweenExpressionUsingInclusiveAndExclusive()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("tableA.id BETWEEN 'lower' AND 'upper' EXCLUSIVE"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(Between.class)));
   //      Between between = (Between)constraint;
   //      assertThat(between.isLowerBoundIncluded(), is(true));
   //      assertThat(between.isUpperBoundIncluded(), is(false));
   //      assertThat(between.getOperand(), is(instanceOf(PropertyValue.class)));
   //      PropertyValue operand = (PropertyValue)between.getOperand();
   //      assertThat(operand.getSelectorName(), is(selector.getName()));
   //      assertThat(operand.getPropertyName(), is("id"));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat((Literal)between.getLowerBound(), is(literal("lower")));
   //      assertThat((Literal)between.getUpperBound(), is(literal("upper")));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithValidBetweenExpressionUsingExclusiveAndInclusive()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("tableA.id BETWEEN 'lower' EXCLUSIVE AND 'upper'"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(Between.class)));
   //      Between between = (Between)constraint;
   //      assertThat(between.isLowerBoundIncluded(), is(false));
   //      assertThat(between.isUpperBoundIncluded(), is(true));
   //      assertThat(between.getOperand(), is(instanceOf(PropertyValue.class)));
   //      PropertyValue operand = (PropertyValue)between.getOperand();
   //      assertThat(operand.getSelectorName(), is(selector.getName()));
   //      assertThat(operand.getPropertyName(), is("id"));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat((Literal)between.getLowerBound(), is(literal("lower")));
   //      assertThat((Literal)between.getUpperBound(), is(literal("upper")));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithValidNotBetweenExpression()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("tableA.id NOT BETWEEN 'lower' AND 'upper'"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(Not.class)));
   //      constraint = ((Not)constraint).getConstraint();
   //      assertThat(constraint, is(instanceOf(Between.class)));
   //      Between between = (Between)constraint;
   //      assertThat(between.isLowerBoundIncluded(), is(true));
   //      assertThat(between.isUpperBoundIncluded(), is(true));
   //      assertThat(between.getOperand(), is(instanceOf(PropertyValue.class)));
   //      PropertyValue operand = (PropertyValue)between.getOperand();
   //      assertThat(operand.getSelectorName(), is(selector.getName()));
   //      assertThat(operand.getPropertyName(), is("id"));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat(between.getLowerBound(), is(instanceOf(Literal.class)));
   //      assertThat((Literal)between.getLowerBound(), is(literal("lower")));
   //      assertThat((Literal)between.getUpperBound(), is(literal("upper")));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseConstraint - parentheses
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithOuterParentheses()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint = parser.parseConstraint(tokens("( ISSAMENODE('/a/b') )"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(SameNode.class)));
   //      SameNode same = (SameNode)constraint;
   //      assertThat(same.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(same.getPath(), is("/a/b"));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithMultipleOuterParentheses()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint = parser.parseConstraint(tokens("((( ISSAMENODE('/a/b') )))"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(SameNode.class)));
   //      SameNode same = (SameNode)constraint;
   //      assertThat(same.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(same.getPath(), is("/a/b"));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithParenthesesAndConjunctionAndDisjunctions()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("ISSAMENODE('/a/b') OR (ISSAMENODE('/c/d') AND ISSAMENODE('/e/f'))"),
   //            typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(Or.class)));
   //      Or or = (Or)constraint;
   //
   //      assertThat(or.getLeft(), is(instanceOf(SameNode.class)));
   //      SameNode first = (SameNode)or.getLeft();
   //      assertThat(first.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(first.getPath(), is("/a/b"));
   //
   //      assertThat(or.getRight(), is(instanceOf(And.class)));
   //      And and = (And)or.getRight();
   //
   //      assertThat(and.getLeft(), is(instanceOf(SameNode.class)));
   //      SameNode second = (SameNode)and.getLeft();
   //      assertThat(second.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(second.getPath(), is("/c/d"));
   //
   //      assertThat(and.getRight(), is(instanceOf(SameNode.class)));
   //      SameNode third = (SameNode)and.getRight();
   //      assertThat(third.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(third.getPath(), is("/e/f"));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseConstraint - AND
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithAndExpressionWithNoParentheses()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("ISSAMENODE('/a/b/c') AND CONTAINS(p1,term1)"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(And.class)));
   //      And and = (And)constraint;
   //
   //      assertThat(and.getLeft(), is(instanceOf(SameNode.class)));
   //      SameNode same = (SameNode)and.getLeft();
   //      assertThat(same.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(same.getPath(), is("/a/b/c"));
   //
   //      assertThat(and.getRight(), is(instanceOf(FullTextSearch.class)));
   //      FullTextSearch search = (FullTextSearch)and.getRight();
   //      assertThat(search.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(search.getPropertyName(), is("p1"));
   //      assertThat(search.getFullTextSearchExpression(), is("term1"));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithMultipleAndExpressions()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("ISSAMENODE('/a/b/c') AND CONTAINS(p1,term1) AND CONTAINS(p2,term2)"),
   //            typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(And.class)));
   //      And and = (And)constraint;
   //
   //      assertThat(and.getLeft(), is(instanceOf(SameNode.class)));
   //      SameNode same = (SameNode)and.getLeft();
   //      assertThat(same.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(same.getPath(), is("/a/b/c"));
   //
   //      assertThat(and.getRight(), is(instanceOf(And.class)));
   //      And secondAnd = (And)and.getRight();
   //
   //      assertThat(secondAnd.getLeft(), is(instanceOf(FullTextSearch.class)));
   //      FullTextSearch search1 = (FullTextSearch)secondAnd.getLeft();
   //      assertThat(search1.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(search1.getPropertyName(), is("p1"));
   //      assertThat(search1.getFullTextSearchExpression(), is("term1"));
   //
   //      assertThat(secondAnd.getRight(), is(instanceOf(FullTextSearch.class)));
   //      FullTextSearch search2 = (FullTextSearch)secondAnd.getRight();
   //      assertThat(search2.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(search2.getPropertyName(), is("p2"));
   //      assertThat(search2.getFullTextSearchExpression(), is("term2"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithAndExpressionWithNoSecondConstraint()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      parser.parseConstraint(tokens("ISSAMENODE('/a/b/c') AND WHAT THE HECK IS THIS"), typeSystem, selector);
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseConstraint - OR
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithOrExpressionWithNoParentheses()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("ISSAMENODE('/a/b/c') OR CONTAINS(p1,term1)"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(Or.class)));
   //      Or or = (Or)constraint;
   //
   //      assertThat(or.getLeft(), is(instanceOf(SameNode.class)));
   //      SameNode same = (SameNode)or.getLeft();
   //      assertThat(same.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(same.getPath(), is("/a/b/c"));
   //
   //      assertThat(or.getRight(), is(instanceOf(FullTextSearch.class)));
   //      FullTextSearch search = (FullTextSearch)or.getRight();
   //      assertThat(search.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(search.getPropertyName(), is("p1"));
   //      assertThat(search.getFullTextSearchExpression(), is("term1"));
   //
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithOrExpressionWithNoSecondConstraint()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      parser.parseConstraint(tokens("ISSAMENODE('/a/b/c') OR WHAT THE HECK IS THIS"), typeSystem, selector);
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseConstraint - NOT
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithNotSameNodeExpression()
   //   {
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("NOT(ISSAMENODE(tableA,'/a/b/c'))"), typeSystem, mock(Source.class));
   //      assertThat(constraint, is(instanceOf(Not.class)));
   //      Not not = (Not)constraint;
   //      assertThat(not.getConstraint(), is(instanceOf(SameNode.class)));
   //      SameNode same = (SameNode)not.getConstraint();
   //      assertThat(same.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(same.getPath(), is("/a/b/c"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithNotConstraintWithOutOpeningParenthesis()
   //   {
   //      parser.parseConstraint(tokens("NOT CONTAINS(propertyA 'term1 term2 -term3')"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithNotConstraintWithOutClosingParenthesis()
   //   {
   //      parser.parseConstraint(tokens("NOT( CONTAINS(propertyA 'term1 term2 -term3') BLAH"), typeSystem,
   //         mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseConstraint - CONTAINS
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithIsContainsExpressionWithPropertyAndNoSelectorNameOnlyIfThereIsOneSelectorSource()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("CONTAINS(propertyA,'term1 term2 -term3')"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(FullTextSearch.class)));
   //      FullTextSearch search = (FullTextSearch)constraint;
   //      assertThat(search.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(search.getPropertyName(), is("propertyA"));
   //      assertThat(search.getFullTextSearchExpression(), is("term1 term2 -term3"));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithIsContainsExpressionWithSelectorNameAndProperty()
   //   {
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("CONTAINS(tableA.propertyA,'term1 term2 -term3')"), typeSystem,
   //            mock(Source.class));
   //      assertThat(constraint, is(instanceOf(FullTextSearch.class)));
   //      FullTextSearch search = (FullTextSearch)constraint;
   //      assertThat(search.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(search.getPropertyName(), is("propertyA"));
   //      assertThat(search.getFullTextSearchExpression(), is("term1 term2 -term3"));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithIsContainsExpressionWithSelectorNameAndAnyProperty()
   //   {
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("CONTAINS(tableA.*,'term1 term2 -term3')"), typeSystem, mock(Source.class));
   //      assertThat(constraint, is(instanceOf(FullTextSearch.class)));
   //      FullTextSearch search = (FullTextSearch)constraint;
   //      assertThat(search.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(search.getPropertyName(), is(nullValue()));
   //      assertThat(search.getFullTextSearchExpression(), is("term1 term2 -term3"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithContainsExpressionWithNoCommaAfterSelectorName()
   //   {
   //      parser.parseConstraint(tokens("CONTAINS(propertyA 'term1 term2 -term3')"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithContainsExpressionWithNoClosingParenthesis()
   //   {
   //      parser.parseConstraint(tokens("CONTAINS(propertyA,'term1 term2 -term3' OTHER"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithContainsExpressionWithNoOpeningParenthesis()
   //   {
   //      parser.parseConstraint(tokens("CONTAINS propertyA,'term1 term2 -term3')"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithContainsExpressionWithNoSelectorNameIfSourceIsNotSelector()
   //   {
   //      parser.parseConstraint(tokens("CONTAINS(propertyA,'term1 term2 -term3')"), typeSystem, mock(Join.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseConstraint - ISSAMENODE
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithIsSameNodeExpressionWithPathOnlyIfThereIsOneSelectorSource()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint = parser.parseConstraint(tokens("ISSAMENODE('/a/b/c')"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(SameNode.class)));
   //      SameNode same = (SameNode)constraint;
   //      assertThat(same.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(same.getPath(), is("/a/b/c"));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithIsSameNodeExpressionWithSelectorNameAndPath()
   //   {
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("ISSAMENODE(tableA,'/a/b/c')"), typeSystem, mock(Source.class));
   //      assertThat(constraint, is(instanceOf(SameNode.class)));
   //      SameNode same = (SameNode)constraint;
   //      assertThat(same.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(same.getPath(), is("/a/b/c"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsSameNodeExpressionWithNoCommaAfterSelectorName()
   //   {
   //      parser.parseConstraint(tokens("ISSAMENODE(tableA '/a/b/c')"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsSameNodeExpressionWithNoClosingParenthesis()
   //   {
   //      parser.parseConstraint(tokens("ISSAMENODE(tableA,'/a/b/c' AND"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsSameNodeExpressionWithNoOpeningParenthesis()
   //   {
   //      parser.parseConstraint(tokens("ISSAMENODE tableA,'/a/b/c')"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsSameNodeExpressionWithNoSelectorNameIfSourceIsNotSelector()
   //   {
   //      parser.parseConstraint(tokens("ISSAMENODE('/a/b/c')"), typeSystem, mock(Join.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseConstraint - ISCHILDNODE
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithIsChildNodeExpressionWithPathOnlyIfThereIsOneSelectorSource()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint = parser.parseConstraint(tokens("ISCHILDNODE('/a/b/c')"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(ChildNode.class)));
   //      ChildNode child = (ChildNode)constraint;
   //      assertThat(child.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(child.getParentPath(), is("/a/b/c"));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithIsChildNodeExpressionWithSelectorNameAndPath()
   //   {
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("ISCHILDNODE(tableA,'/a/b/c')"), typeSystem, mock(Source.class));
   //      assertThat(constraint, is(instanceOf(ChildNode.class)));
   //      ChildNode child = (ChildNode)constraint;
   //      assertThat(child.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(child.getParentPath(), is("/a/b/c"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsChildNodeExpressionWithNoCommaAfterSelectorName()
   //   {
   //      parser.parseConstraint(tokens("ISCHILDNODE(tableA '/a/b/c')"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsChildNodeExpressionWithNoClosingParenthesis()
   //   {
   //      parser.parseConstraint(tokens("ISCHILDNODE(tableA,'/a/b/c' AND"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsChildNodeExpressionWithNoOpeningParenthesis()
   //   {
   //      parser.parseConstraint(tokens("ISCHILDNODE tableA,'/a/b/c')"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsChildNodeExpressionWithNoSelectorNameIfSourceIsNotSelector()
   //   {
   //      parser.parseConstraint(tokens("ISCHILDNODE('/a/b/c')"), typeSystem, mock(Join.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseConstraint - ISDESCENDANTNODE
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithIsDescendantNodeExpressionWithPathOnlyIfThereIsOneSelectorSource()
   //   {
   //      NamedSelector selector = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint = parser.parseConstraint(tokens("ISDESCENDANTNODE('/a/b/c')"), typeSystem, selector);
   //      assertThat(constraint, is(instanceOf(DescendantNode.class)));
   //      DescendantNode descendant = (DescendantNode)constraint;
   //      assertThat(descendant.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(descendant.getAncestorPath(), is("/a/b/c"));
   //   }
   //
   //   @Test
   //   public void shouldParseConstraintFromStringWithIsDescendantNodeExpressionWithSelectorNameAndPath()
   //   {
   //      Constraint constraint =
   //         parser.parseConstraint(tokens("ISDESCENDANTNODE(tableA,'/a/b/c')"), typeSystem, mock(Source.class));
   //      assertThat(constraint, is(instanceOf(DescendantNode.class)));
   //      DescendantNode descendant = (DescendantNode)constraint;
   //      assertThat(descendant.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(descendant.getAncestorPath(), is("/a/b/c"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsDescendantNodeExpressionWithNoCommaAfterSelectorName()
   //   {
   //      parser.parseConstraint(tokens("ISDESCENDANTNODE(tableA '/a/b/c')"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsDescendantNodeExpressionWithNoClosingParenthesis()
   //   {
   //      parser.parseConstraint(tokens("ISDESCENDANTNODE(tableA,'/a/b/c' AND"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsDescendantNodeExpressionWithNoOpeningParenthesis()
   //   {
   //      parser.parseConstraint(tokens("ISDESCENDANTNODE tableA,'/a/b/c')"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseConstraintFromStringWithIsDescendantNodeExpressionWithNoSelectorNameIfSourceIsNotSelector()
   //   {
   //      parser.parseConstraint(tokens("ISDESCENDANTNODE('/a/b/c')"), typeSystem, mock(Join.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseInClause
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseInClauseFromStringWithSingleValidLiteral()
   //   {
   //      List<StaticOperand> result = parser.parseInClause(tokens("IN ('value1')"), typeSystem);
   //      assertThat(result.size(), is(1));
   //      assertThat(result.get(0), is((StaticOperand)literal("value1")));
   //   }
   //
   //   @Test
   //   public void shouldParseInClauseFromStringWithTwoValidLiteral()
   //   {
   //      List<StaticOperand> result = parser.parseInClause(tokens("IN ('value1','value2')"), typeSystem);
   //      assertThat(result.size(), is(2));
   //      assertThat(result.get(0), is((StaticOperand)literal("value1")));
   //      assertThat(result.get(1), is((StaticOperand)literal("value2")));
   //   }
   //
   //   @Test
   //   public void shouldParseInClauseFromStringWithThreeValidLiteral()
   //   {
   //      List<StaticOperand> result = parser.parseInClause(tokens("IN ('value1','value2','value3')"), typeSystem);
   //      assertThat(result.size(), is(3));
   //      assertThat(result.get(0), is((StaticOperand)literal("value1")));
   //      assertThat(result.get(1), is((StaticOperand)literal("value2")));
   //      assertThat(result.get(2), is((StaticOperand)literal("value3")));
   //   }
   //
   //   @Test
   //   public void shouldParseInClauseFromStringWithSingleValidLiteralCast()
   //   {
   //      List<StaticOperand> result = parser.parseInClause(tokens("IN (CAST('value1' AS STRING))"), typeSystem);
   //      assertThat(result.size(), is(1));
   //      assertThat(result.iterator().next(), is((StaticOperand)literal("value1")));
   //
   //      result = parser.parseInClause(tokens("IN (CAST('3' AS LONG))"), typeSystem);
   //      assertThat(result.size(), is(1));
   //      assertThat(result.iterator().next(), is((StaticOperand)literal(new Long(3))));
   //   }
   //
   //   @Test
   //   public void shouldParseInClauseFromStringWithMultipleValidLiteralCasts()
   //   {
   //      List<StaticOperand> result =
   //         parser.parseInClause(tokens("IN (CAST('value1' AS STRING),CAST('3' AS LONG),'4')"), typeSystem);
   //      assertThat(result.size(), is(3));
   //      assertThat(result.get(0), is((StaticOperand)literal("value1")));
   //      assertThat(result.get(1), is((StaticOperand)literal(new Long(3))));
   //      assertThat(result.get(2), is((StaticOperand)literal("4")));
   //   }
   //
   //   protected Literal literal(Object literalValue)
   //   {
   //      return new Literal(literalValue);
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseFullTextSearchExpression
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseFullTextSearchExpressionFromStringWithValidExpression()
   //   {
   //      Position pos = new Position(500, 100, 13);
   //      FullTextSearch.Term result =
   //         parser.parseFullTextSearchExpression("term1 term2 OR -term3 OR -term4 OR term5", pos);
   //      assertThat(result, is(notNullValue()));
   //      assertThat(result, is(instanceOf(Disjunction.class)));
   //      Disjunction disjunction = (Disjunction)result;
   //      assertThat(disjunction.getTerms().size(), is(4));
   //      Conjunction conjunction1 = (Conjunction)disjunction.getTerms().get(0);
   //      Term term3 = disjunction.getTerms().get(1);
   //      Term term4 = disjunction.getTerms().get(2);
   //      Term term5 = disjunction.getTerms().get(3);
   //      FullTextSearchParserTest.assertHasSimpleTerms(conjunction1, "term1", "term2");
   //      FullTextSearchParserTest.assertSimpleTerm(term3, "term3", true, false);
   //      FullTextSearchParserTest.assertSimpleTerm(term4, "term4", true, false);
   //      FullTextSearchParserTest.assertSimpleTerm(term5, "term5", false, false);
   //   }
   //
   //   @Test
   //   public void shouldConvertPositionWhenUnableToParseFullTextSearchExpression()
   //   {
   //      try
   //      {
   //         parser.parseFullTextSearchExpression("", new Position(500, 100, 13));
   //         fail("Should have thrown an exception");
   //      }
   //      catch (InvalidQueryException e)
   //      {
   //         assertThat(e.getPosition().getLine(), is(100));
   //         assertThat(e.getPosition().getColumn(), is(13));
   //         assertThat(e.getPosition().getIndexInContent(), is(500));
   //      }
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseComparisonOperator
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseComparisonOperator()
   //   {
   //      // Same case
   //      for (Operator operator : Operator.values())
   //      {
   //         assertThat(parser.parseComparisonOperator(tokens(operator.getSymbol())), is(operator));
   //      }
   //      // Upper case
   //      for (Operator operator : Operator.values())
   //      {
   //         assertThat(parser.parseComparisonOperator(tokens(operator.getSymbol().toUpperCase())), is(operator));
   //      }
   //      // Lower case
   //      for (Operator operator : Operator.values())
   //      {
   //         assertThat(parser.parseComparisonOperator(tokens(operator.getSymbol().toLowerCase())), is(operator));
   //      }
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseComparisonOperatorIfOperatorIsUnknown()
   //   {
   //      parser.parseComparisonOperator(tokens("FOO"));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseOrderBy
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParserOrderByWithOneOrdering()
   //   {
   //      List<Ordering> orderBy = parser.parseOrderBy(tokens("ORDER BY NAME(tableA) ASC"), typeSystem, mock(Source.class));
   //      assertThat(orderBy.size(), is(1));
   //      Ordering first = orderBy.get(0);
   //      assertThat(first.getOperand(), is(instanceOf(NodeName.class)));
   //      assertThat(first.getOrder(), is(Order.ASCENDING));
   //   }
   //
   //   @Test
   //   public void shouldParserOrderByWithTwoOrderings()
   //   {
   //      List<Ordering> orderBy =
   //         parser.parseOrderBy(tokens("ORDER BY NAME(tableA) ASC, SCORE(tableB) DESC"), typeSystem, mock(Source.class));
   //      assertThat(orderBy.size(), is(2));
   //      Ordering first = orderBy.get(0);
   //      assertThat(first.getOperand(), is(instanceOf(NodeName.class)));
   //      assertThat(first.getOrder(), is(Order.ASCENDING));
   //      Ordering second = orderBy.get(1);
   //      assertThat(second.getOperand(), is(instanceOf(FullTextSearchScore.class)));
   //      assertThat(second.getOrder(), is(Order.DESCENDING));
   //   }
   //
   //   @Test
   //   public void shouldParserOrderByWithMultipleOrderings()
   //   {
   //      List<Ordering> orderBy =
   //         parser.parseOrderBy(tokens("ORDER BY NAME(tableA) ASC, SCORE(tableB) DESC, LENGTH(tableC.id) ASC"),
   //            typeSystem, mock(Source.class));
   //      assertThat(orderBy.size(), is(3));
   //      Ordering first = orderBy.get(0);
   //      assertThat(first.getOperand(), is(instanceOf(NodeName.class)));
   //      assertThat(first.getOrder(), is(Order.ASCENDING));
   //      Ordering second = orderBy.get(1);
   //      assertThat(second.getOperand(), is(instanceOf(FullTextSearchScore.class)));
   //      assertThat(second.getOrder(), is(Order.DESCENDING));
   //      Ordering third = orderBy.get(2);
   //      assertThat(third.getOperand(), is(instanceOf(Length.class)));
   //      assertThat(third.getOrder(), is(Order.ASCENDING));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseOrderByIfCommaNotFollowedByAnotherOrdering()
   //   {
   //      parser.parseOrderBy(tokens("ORDER BY NAME(tableA) ASC, NOT A VALID ORDERING"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test
   //   public void shouldReturnNullFromParseOrderByWithoutOrderByKeywords()
   //   {
   //      assertThat(parser.parseOrderBy(tokens("NOT ORDER BY"), typeSystem, mock(Source.class)), is(nullValue()));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseOrdering
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseOrderingFromDynamicOperandFollowedByAscendingKeyword()
   //   {
   //      Ordering ordering = parser.parseOrdering(tokens("NAME(tableA) ASC"), typeSystem, mock(Source.class));
   //      assertThat(ordering.getOperand(), is(instanceOf(NodeName.class)));
   //      assertThat(ordering.getOrder(), is(Order.ASCENDING));
   //   }
   //
   //   @Test
   //   public void shouldParseOrderingFromDynamicOperandFollowedByDecendingKeyword()
   //   {
   //      Ordering ordering = parser.parseOrdering(tokens("NAME(tableA) DESC"), typeSystem, mock(Source.class));
   //      assertThat(ordering.getOperand(), is(instanceOf(NodeName.class)));
   //      assertThat(ordering.getOrder(), is(Order.DESCENDING));
   //   }
   //
   //   @Test
   //   public void shouldParseOrderingFromDynamicOperandAndDefaultToAscendingWhenNotFollowedByAscendingOrDescendingKeyword()
   //   {
   //      Ordering ordering = parser.parseOrdering(tokens("NAME(tableA) OTHER"), typeSystem, mock(Source.class));
   //      assertThat(ordering.getOperand(), is(instanceOf(NodeName.class)));
   //      assertThat(ordering.getOrder(), is(Order.ASCENDING));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parsePropertyExistance
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParsePropertyExistanceFromPropertyNameWithSelectorNameAndPropertyNameFollowedByIsNotNull()
   //   {
   //      Constraint constraint =
   //         parser.parsePropertyExistance(tokens("tableA.property1 IS NOT NULL"), typeSystem, mock(Source.class));
   //      assertThat(constraint, is(instanceOf(PropertyExistence.class)));
   //      PropertyExistence p = (PropertyExistence)constraint;
   //      assertThat(p.getPropertyName(), is("property1"));
   //      assertThat(p.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParsePropertyExistanceFromPropertyNameWithPropertyNameAndNoSelectorNameFollowedByIsNotNull()
   //   {
   //      NamedSelector source = new NamedSelector(selectorName("tableA"));
   //      Constraint constraint = parser.parsePropertyExistance(tokens("property1 IS NOT NULL"), typeSystem, source);
   //      assertThat(constraint, is(instanceOf(PropertyExistence.class)));
   //      PropertyExistence p = (PropertyExistence)constraint;
   //      assertThat(p.getPropertyName(), is("property1"));
   //      assertThat(p.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParsePropertyExistanceFromPropertyNameWithNoSelectorNameIfSourceIsNotSelector()
   //   {
   //      parser.parsePropertyExistance(tokens("property1 IS NOT NULL"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test
   //   public void shouldParseNotPropertyExistanceFromPropertyNameWithSelectorNameAndPropertyNameFollowedByIsNull()
   //   {
   //      Constraint constraint =
   //         parser.parsePropertyExistance(tokens("tableA.property1 IS NULL"), typeSystem, mock(Source.class));
   //      assertThat(constraint, is(instanceOf(Not.class)));
   //      Not not = (Not)constraint;
   //      assertThat(not.getConstraint(), is(instanceOf(PropertyExistence.class)));
   //      PropertyExistence p = (PropertyExistence)not.getConstraint();
   //      assertThat(p.getPropertyName(), is("property1"));
   //      assertThat(p.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldReturnNullFromParsePropertyExistanceIfExpressionDoesNotMatchPattern()
   //   {
   //      Source s = mock(Source.class);
   //      assertThat(parser.parsePropertyExistance(tokens("tableA WILL NOT"), typeSystem, s), is(nullValue()));
   //      assertThat(parser.parsePropertyExistance(tokens("tableA.property1 NOT NULL"), typeSystem, s), is(nullValue()));
   //      assertThat(parser.parsePropertyExistance(tokens("tableA.property1 IS NOT SOMETHING"), typeSystem, s),
   //         is(nullValue()));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseStaticOperand
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseStaticOperandFromStringWithBindVariable()
   //   {
   //      StaticOperand operand = parser.parseStaticOperand(tokens("$VAR"), typeSystem);
   //      assertThat(operand, is(instanceOf(BindVariableName.class)));
   //      BindVariableName var = (BindVariableName)operand;
   //      assertThat(var.getVariableName(), is("VAR"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseStaticOperandFromStringWithBindVariableWithNoVariableName()
   //   {
   //      parser.parseStaticOperand(tokens("$"), typeSystem);
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseStaticOperandFromStringWithBindVariableWithCharactersThatAreNotFromNCName()
   //   {
   //      parser.parseStaticOperand(tokens("$#2VAR"), typeSystem);
   //   }
   //
   //   @Test
   //   public void shouldParseStaticOperandFromStringWithLiteralValue()
   //   {
   //      StaticOperand operand = parser.parseStaticOperand(tokens("CAST(123 AS DOUBLE)"), typeSystem);
   //      assertThat(operand, is(instanceOf(Literal.class)));
   //      Literal literal = (Literal)operand;
   //      assertThat((Double)literal.getValue(), is(typeSystem.getDoubleFactory().create("123")));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseLiteral
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseLiteralFromStringWithCastBooleanLiteralToString()
   //   {
   //      assertThat((String)parser.parseLiteral(tokens("CAST(true AS STRING)"), typeSystem).getValue(), is(Boolean.TRUE
   //         .toString()));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(false AS STRING)"), typeSystem).getValue(), is(Boolean.FALSE
   //         .toString()));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(TRUE AS STRING)"), typeSystem).getValue(), is(Boolean.TRUE
   //         .toString()));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(FALSE AS STRING)"), typeSystem).getValue(), is(Boolean.FALSE
   //         .toString()));
   //      assertThat((String)parser.parseLiteral(tokens("CAST('true' AS stRinG)"), typeSystem).getValue(), is(Boolean.TRUE
   //         .toString()));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(\"false\" AS string)"), typeSystem).getValue(),
   //         is(Boolean.FALSE.toString()));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralFromStringWithCastBooleanLiteralToBinary()
   //   {
   //      Binary binaryTrue = (Binary)typeSystem.getTypeFactory(PropertyType.BINARY.getName()).create(true);
   //      Binary binaryFalse = (Binary)typeSystem.getTypeFactory(PropertyType.BINARY.getName()).create(false);
   //      assertThat((Binary)parser.parseLiteral(tokens("CAST(true AS BINARY)"), typeSystem).getValue(), is(binaryTrue));
   //      assertThat((Binary)parser.parseLiteral(tokens("CAST(false AS BINARY)"), typeSystem).getValue(), is(binaryFalse));
   //      assertThat((Binary)parser.parseLiteral(tokens("CAST(TRUE AS BINARY)"), typeSystem).getValue(), is(binaryTrue));
   //      assertThat((Binary)parser.parseLiteral(tokens("CAST(FALSE AS BINARY)"), typeSystem).getValue(), is(binaryFalse));
   //      assertThat((Binary)parser.parseLiteral(tokens("CAST('true' AS biNarY)"), typeSystem).getValue(), is(binaryTrue));
   //      assertThat((Binary)parser.parseLiteral(tokens("CAST(\"false\" AS binary)"), typeSystem).getValue(),
   //         is(binaryFalse));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLiteralFromStringWithCastBooleanLiteralToLong()
   //   {
   //      parser.parseLiteral(tokens("CAST(true AS LONG)"), typeSystem);
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLiteralFromStringWithCastBooleanLiteralToDouble()
   //   {
   //      parser.parseLiteral(tokens("CAST(true AS DOUBLE)"), typeSystem);
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLiteralFromStringWithCastBooleanLiteralToDate()
   //   {
   //      parser.parseLiteral(tokens("CAST(true AS DATE)"), typeSystem);
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralFromStringWithCastLongLiteralToString()
   //   {
   //      assertThat((String)parser.parseLiteral(tokens("CAST(123 AS STRING)"), typeSystem).getValue(), is("123"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(+123 AS STRING)"), typeSystem).getValue(), is("123"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(-123 AS STRING)"), typeSystem).getValue(), is("-123"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(0 AS STRING)"), typeSystem).getValue(), is("0"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralFromStringWithCastLongLiteralToLong()
   //   {
   //      assertThat((Long)parser.parseLiteral(tokens("CAST(123 AS LONG)"), typeSystem).getValue(), is(123L));
   //      assertThat((Long)parser.parseLiteral(tokens("CAST(+123 AS LONG)"), typeSystem).getValue(), is(123L));
   //      assertThat((Long)parser.parseLiteral(tokens("CAST(-123 AS LONG)"), typeSystem).getValue(), is(-123L));
   //      assertThat((Long)parser.parseLiteral(tokens("CAST(0 AS LONG)"), typeSystem).getValue(), is(0L));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralFromStringWithCastDoubleLiteralToString()
   //   {
   //      assertThat((String)parser.parseLiteral(tokens("CAST(1.23 AS STRING)"), typeSystem).getValue(), is("1.23"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(+1.23 AS STRING)"), typeSystem).getValue(), is("1.23"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(-1.23 AS STRING)"), typeSystem).getValue(), is("-1.23"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(1.23e10 AS STRING)"), typeSystem).getValue(), is("1.23E10"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(1.23e+10 AS STRING)"), typeSystem).getValue(), is("1.23E10"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(1.23e-10 AS STRING)"), typeSystem).getValue(), is("1.23E-10"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralFromStringWithCastDateLiteralToString()
   //   {
   //      assertThat(
   //         (String)parser.parseLiteral(tokens("CAST(2009-03-22T03:22:45.345Z AS STRING)"), typeSystem).getValue(),
   //         is("2009-03-22T03:22:45.345Z"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(2009-03-22T03:22:45.345UTC AS STRING)"), typeSystem)
   //         .getValue(), is("2009-03-22T03:22:45.345Z"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(2009-03-22T03:22:45.3-01:00 AS STRING)"), typeSystem)
   //         .getValue(), is("2009-03-22T04:22:45.300Z"));
   //      assertThat((String)parser.parseLiteral(tokens("CAST(2009-03-22T03:22:45.345+01:00 AS STRING)"), typeSystem)
   //         .getValue(), is("2009-03-22T02:22:45.345Z"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralFromStringWithCastStringLiteralToName()
   //   {
   //      assertThat((Name)parser.parseLiteral(tokens("CAST([mode:name] AS NAME)"), typeSystem).getValue(),
   //         is(name("mode:name")));
   //      assertThat((Name)parser.parseLiteral(tokens("CAST('mode:name' AS NAME)"), typeSystem).getValue(),
   //         is(name("mode:name")));
   //      assertThat((Name)parser.parseLiteral(tokens("CAST(\"mode:name\" AS NAME)"), typeSystem).getValue(),
   //         is(name("mode:name")));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralFromStringWithCastStringLiteralToPath()
   //   {
   //      assertThat((Path)parser.parseLiteral(tokens("CAST([/mode:name/a/b] AS PATH)"), typeSystem).getValue(),
   //         is(path("/mode:name/a/b")));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralFromStringWithUncastLiteralValueAndRepresentValueAsStringRepresentation()
   //   {
   //      assertThat(parser.parseLiteral(tokens("true"), typeSystem).getValue(), is((Object)Boolean.TRUE.toString()));
   //      assertThat(parser.parseLiteral(tokens("false"), typeSystem).getValue(), is((Object)Boolean.FALSE.toString()));
   //      assertThat(parser.parseLiteral(tokens("TRUE"), typeSystem).getValue(), is((Object)Boolean.TRUE.toString()));
   //      assertThat(parser.parseLiteral(tokens("FALSE"), typeSystem).getValue(), is((Object)Boolean.FALSE.toString()));
   //      assertThat(parser.parseLiteral(tokens("123"), typeSystem).getValue(), is((Object)"123"));
   //      assertThat(parser.parseLiteral(tokens("+123"), typeSystem).getValue(), is((Object)"123"));
   //      assertThat(parser.parseLiteral(tokens("-123"), typeSystem).getValue(), is((Object)"-123"));
   //      assertThat(parser.parseLiteral(tokens("1.23"), typeSystem).getValue(), is((Object)"1.23"));
   //      assertThat(parser.parseLiteral(tokens("+1.23"), typeSystem).getValue(), is((Object)"1.23"));
   //      assertThat(parser.parseLiteral(tokens("-1.23"), typeSystem).getValue(), is((Object)"-1.23"));
   //      assertThat(parser.parseLiteral(tokens("1.23e10"), typeSystem).getValue(), is((Object)"1.23E10"));
   //      assertThat(parser.parseLiteral(tokens("1.23e+10"), typeSystem).getValue(), is((Object)"1.23E10"));
   //      assertThat(parser.parseLiteral(tokens("1.23e-10"), typeSystem).getValue(), is((Object)"1.23E-10"));
   //      assertThat(parser.parseLiteral(tokens("0"), typeSystem).getValue(), is((Object)"0"));
   //      assertThat(parser.parseLiteral(tokens("2009-03-22T03:22:45.345Z"), typeSystem).getValue(),
   //         is((Object)"2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteral(tokens("2009-03-22T03:22:45.345UTC"), typeSystem).getValue(),
   //         is((Object)"2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteral(tokens("2009-03-22T03:22:45.3-01:00"), typeSystem).getValue(),
   //         is((Object)"2009-03-22T04:22:45.300Z"));
   //      assertThat(parser.parseLiteral(tokens("2009-03-22T03:22:45.345+01:00"), typeSystem).getValue(),
   //         is((Object)"2009-03-22T02:22:45.345Z"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLiteralFromStringWithCastAndNoEndingParenthesis()
   //   {
   //      parser.parseLiteral(tokens("CAST(123 AS STRING OTHER"), typeSystem);
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLiteralFromStringWithCastAndNoOpeningParenthesis()
   //   {
   //      parser.parseLiteral(tokens("CAST 123 AS STRING) OTHER"), typeSystem);
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLiteralFromStringWithCastAndInvalidType()
   //   {
   //      parser.parseLiteral(tokens("CAST(123 AS FOOD) OTHER"), typeSystem);
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLiteralFromStringWithCastAndNoAsKeyword()
   //   {
   //      parser.parseLiteral(tokens("CAST(123 STRING) OTHER"), typeSystem);
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLiteralFromStringWithCastAndNoLiteralValueBeforeAs()
   //   {
   //      parser.parseLiteral(tokens("CAST(AS STRING) OTHER"), typeSystem);
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseLiteralValue - unquoted
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseLiteralValueFromStringWithPositiveAndNegativeIntegerValues()
   //   {
   //      assertThat(parser.parseLiteralValue(tokens("123"), typeSystem), is("123"));
   //      assertThat(parser.parseLiteralValue(tokens("-123"), typeSystem), is("-123"));
   //      assertThat(parser.parseLiteralValue(tokens("- 123"), typeSystem), is("-123"));
   //      assertThat(parser.parseLiteralValue(tokens("+123"), typeSystem), is("123"));
   //      assertThat(parser.parseLiteralValue(tokens("+ 123"), typeSystem), is("123"));
   //      assertThat(parser.parseLiteralValue(tokens("0"), typeSystem), is("0"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralValueFromStringWithPositiveAndNegativeDecimalValues()
   //   {
   //      assertThat(parser.parseLiteralValue(tokens("1.23"), typeSystem), is("1.23"));
   //      assertThat(parser.parseLiteralValue(tokens("-1.23"), typeSystem), is("-1.23"));
   //      assertThat(parser.parseLiteralValue(tokens("+0.123"), typeSystem), is("0.123"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralValueFromStringWithPositiveAndNegativeDecimalValuesInScientificNotation()
   //   {
   //      assertThat(parser.parseLiteralValue(tokens("1.23"), typeSystem), is("1.23"));
   //      assertThat(parser.parseLiteralValue(tokens("1.23e10"), typeSystem), is("1.23E10"));
   //      assertThat(parser.parseLiteralValue(tokens("- 1.23e10"), typeSystem), is("-1.23E10"));
   //      assertThat(parser.parseLiteralValue(tokens("- 1.23e-10"), typeSystem), is("-1.23E-10"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralValueFromStringWithBooleanValues()
   //   {
   //      assertThat(parser.parseLiteralValue(tokens("true"), typeSystem), is(Boolean.TRUE.toString()));
   //      assertThat(parser.parseLiteralValue(tokens("false"), typeSystem), is(Boolean.FALSE.toString()));
   //      assertThat(parser.parseLiteralValue(tokens("TRUE"), typeSystem), is(Boolean.TRUE.toString()));
   //      assertThat(parser.parseLiteralValue(tokens("FALSE"), typeSystem), is(Boolean.FALSE.toString()));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralValueFromStringWithDateValues()
   //   {
   //      // sYYYY-MM-DDThh:mm:ss.sssTZD
   //      assertThat(parser.parseLiteralValue(tokens("2009-03-22T03:22:45.345Z"), typeSystem),
   //         is("2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteralValue(tokens("2009-03-22T03:22:45.345UTC"), typeSystem),
   //         is("2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteralValue(tokens("2009-03-22T03:22:45.3-01:00"), typeSystem),
   //         is("2009-03-22T04:22:45.300Z"));
   //      assertThat(parser.parseLiteralValue(tokens("2009-03-22T03:22:45.345+01:00"), typeSystem),
   //         is("2009-03-22T02:22:45.345Z"));
   //
   //      assertThat(parser.parseLiteralValue(tokens("-2009-03-22T03:22:45.345Z"), typeSystem),
   //         is("-2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteralValue(tokens("-2009-03-22T03:22:45.345UTC"), typeSystem),
   //         is("-2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteralValue(tokens("-2009-03-22T03:22:45.3-01:00"), typeSystem),
   //         is("-2009-03-22T04:22:45.300Z"));
   //      assertThat(parser.parseLiteralValue(tokens("-2009-03-22T03:22:45.345+01:00"), typeSystem),
   //         is("-2009-03-22T02:22:45.345Z"));
   //
   //      assertThat(parser.parseLiteralValue(tokens("+2009-03-22T03:22:45.345Z"), typeSystem),
   //         is("2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteralValue(tokens("+2009-03-22T03:22:45.345UTC"), typeSystem),
   //         is("2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteralValue(tokens("+2009-03-22T03:22:45.3-01:00"), typeSystem),
   //         is("2009-03-22T04:22:45.300Z"));
   //      assertThat(parser.parseLiteralValue(tokens("+2009-03-22T03:22:45.345+01:00"), typeSystem),
   //         is("2009-03-22T02:22:45.345Z"));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseLiteralValue - quoted
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseLiteralValueFromQuotedStringWithPositiveAndNegativeIntegerValues()
   //   {
   //      assertThat(parser.parseLiteralValue(tokens("'123'"), typeSystem), is("123"));
   //      assertThat(parser.parseLiteralValue(tokens("'-123'"), typeSystem), is("-123"));
   //      assertThat(parser.parseLiteralValue(tokens("'- 123'"), typeSystem), is("- 123"));
   //      assertThat(parser.parseLiteralValue(tokens("'+123'"), typeSystem), is("+123"));
   //      assertThat(parser.parseLiteralValue(tokens("'+ 123'"), typeSystem), is("+ 123"));
   //      assertThat(parser.parseLiteralValue(tokens("'0'"), typeSystem), is("0"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralValueFromQuotedStringWithPositiveAndNegativeDecimalValues()
   //   {
   //      assertThat(parser.parseLiteralValue(tokens("'1.23'"), typeSystem), is("1.23"));
   //      assertThat(parser.parseLiteralValue(tokens("'-1.23'"), typeSystem), is("-1.23"));
   //      assertThat(parser.parseLiteralValue(tokens("'+0.123'"), typeSystem), is("+0.123"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralValueFromQuotedStringWithPositiveAndNegativeDecimalValuesInScientificNotation()
   //   {
   //      assertThat(parser.parseLiteralValue(tokens("'1.23'"), typeSystem), is("1.23"));
   //      assertThat(parser.parseLiteralValue(tokens("'1.23e10'"), typeSystem), is("1.23e10"));
   //      assertThat(parser.parseLiteralValue(tokens("'- 1.23e10'"), typeSystem), is("- 1.23e10"));
   //      assertThat(parser.parseLiteralValue(tokens("'- 1.23e-10'"), typeSystem), is("- 1.23e-10"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralValueFromQuotedStringWithBooleanValues()
   //   {
   //      assertThat(parser.parseLiteralValue(tokens("'true'"), typeSystem), is("true"));
   //      assertThat(parser.parseLiteralValue(tokens("'false'"), typeSystem), is("false"));
   //      assertThat(parser.parseLiteralValue(tokens("'TRUE'"), typeSystem), is("TRUE"));
   //      assertThat(parser.parseLiteralValue(tokens("'FALSE'"), typeSystem), is("FALSE"));
   //   }
   //
   //   @Test
   //   public void shouldParseLiteralValueFromQuotedStringWithDateValues()
   //   {
   //      // sYYYY-MM-DDThh:mm:ss.sssTZD
   //      assertThat(parser.parseLiteralValue(tokens("'2009-03-22T03:22:45.345Z'"), typeSystem),
   //         is("2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteralValue(tokens("'2009-03-22T03:22:45.345UTC'"), typeSystem),
   //         is("2009-03-22T03:22:45.345UTC"));
   //      assertThat(parser.parseLiteralValue(tokens("'2009-03-22T03:22:45.3-01:00'"), typeSystem),
   //         is("2009-03-22T03:22:45.3-01:00"));
   //      assertThat(parser.parseLiteralValue(tokens("'2009-03-22T03:22:45.345+01:00'"), typeSystem),
   //         is("2009-03-22T03:22:45.345+01:00"));
   //
   //      assertThat(parser.parseLiteralValue(tokens("'-2009-03-22T03:22:45.345Z'"), typeSystem),
   //         is("-2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteralValue(tokens("'-2009-03-22T03:22:45.345UTC'"), typeSystem),
   //         is("-2009-03-22T03:22:45.345UTC"));
   //      assertThat(parser.parseLiteralValue(tokens("'-2009-03-22T03:22:45.3-01:00'"), typeSystem),
   //         is("-2009-03-22T03:22:45.3-01:00"));
   //      assertThat(parser.parseLiteralValue(tokens("'-2009-03-22T03:22:45.345+01:00'"), typeSystem),
   //         is("-2009-03-22T03:22:45.345+01:00"));
   //
   //      assertThat(parser.parseLiteralValue(tokens("'+2009-03-22T03:22:45.345Z'"), typeSystem),
   //         is("+2009-03-22T03:22:45.345Z"));
   //      assertThat(parser.parseLiteralValue(tokens("'+2009-03-22T03:22:45.345UTC'"), typeSystem),
   //         is("+2009-03-22T03:22:45.345UTC"));
   //      assertThat(parser.parseLiteralValue(tokens("'+2009-03-22T03:22:45.3-01:00'"), typeSystem),
   //         is("+2009-03-22T03:22:45.3-01:00"));
   //      assertThat(parser.parseLiteralValue(tokens("'+2009-03-22T03:22:45.345+01:00'"), typeSystem),
   //         is("+2009-03-22T03:22:45.345+01:00"));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - LENGTH
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingLengthOfPropertyValue()
   //   {
   //      DynamicOperand operand =
   //         parser.parseDynamicOperand(tokens("LENGTH(tableA.property)"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(Length.class)));
   //      Length length = (Length)operand;
   //      assertThat(length.getPropertyValue().getPropertyName(), is("property"));
   //      assertThat(length.getPropertyValue().getSelectorName(), is(selectorName("tableA")));
   //      assertThat(length.getSelectorName(), is(selectorName("tableA")));
   //
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      operand = parser.parseDynamicOperand(tokens("LENGTH(property)"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(Length.class)));
   //      length = (Length)operand;
   //      assertThat(length.getPropertyValue().getPropertyName(), is("property"));
   //      assertThat(length.getPropertyValue().getSelectorName(), is(selectorName("tableA")));
   //      assertThat(length.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingLengthWithoutClosingParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("LENGTH(tableA.property other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingLengthWithoutOpeningParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("LENGTH tableA.property other"), typeSystem, mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - LOWER
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingLowerOfAnotherDynamicOperand()
   //   {
   //      DynamicOperand operand =
   //         parser.parseDynamicOperand(tokens("LOWER(tableA.property)"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(LowerCase.class)));
   //      LowerCase lower = (LowerCase)operand;
   //      assertThat(lower.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(lower.getOperand(), is(instanceOf(PropertyValue.class)));
   //      PropertyValue value = (PropertyValue)lower.getOperand();
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      operand = parser.parseDynamicOperand(tokens("LOWER(property)"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(LowerCase.class)));
   //      lower = (LowerCase)operand;
   //      assertThat(lower.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(lower.getOperand(), is(instanceOf(PropertyValue.class)));
   //      value = (PropertyValue)lower.getOperand();
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingLowerOfUpperCaseOfAnotherOperand()
   //   {
   //      DynamicOperand operand =
   //         parser.parseDynamicOperand(tokens("LOWER(UPPER(tableA.property))"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(LowerCase.class)));
   //      LowerCase lower = (LowerCase)operand;
   //      assertThat(lower.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(lower.getOperand(), is(instanceOf(UpperCase.class)));
   //      UpperCase upper = (UpperCase)lower.getOperand();
   //      assertThat(upper.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(upper.getOperand(), is(instanceOf(PropertyValue.class)));
   //      PropertyValue value = (PropertyValue)upper.getOperand();
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingLowerWithoutClosingParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("LOWER(tableA.property other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingLowerWithoutOpeningParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("LOWER tableA.property other"), typeSystem, mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - UPPER
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingUpperOfAnotherDynamicOperand()
   //   {
   //      DynamicOperand operand =
   //         parser.parseDynamicOperand(tokens("UPPER(tableA.property)"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(UpperCase.class)));
   //      UpperCase upper = (UpperCase)operand;
   //      assertThat(upper.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(upper.getOperand(), is(instanceOf(PropertyValue.class)));
   //      PropertyValue value = (PropertyValue)upper.getOperand();
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      operand = parser.parseDynamicOperand(tokens("UPPER(property)"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(UpperCase.class)));
   //      upper = (UpperCase)operand;
   //      assertThat(upper.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(upper.getOperand(), is(instanceOf(PropertyValue.class)));
   //      value = (PropertyValue)upper.getOperand();
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingUpperOfLowerCaseOfAnotherOperand()
   //   {
   //      DynamicOperand operand =
   //         parser.parseDynamicOperand(tokens("UPPER(LOWER(tableA.property))"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(UpperCase.class)));
   //      UpperCase upper = (UpperCase)operand;
   //      assertThat(upper.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(upper.getOperand(), is(instanceOf(LowerCase.class)));
   //      LowerCase lower = (LowerCase)upper.getOperand();
   //      assertThat(lower.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(lower.getOperand(), is(instanceOf(PropertyValue.class)));
   //      PropertyValue value = (PropertyValue)lower.getOperand();
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingUpperWithoutClosingParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("UPPER(tableA.property other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingUpperWithoutOpeningParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("Upper tableA.property other"), typeSystem, mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - DEPTH
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingDepthOfSelector()
   //   {
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("DEPTH(tableA)"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(NodeDepth.class)));
   //      NodeDepth depth = (NodeDepth)operand;
   //      assertThat(depth.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingDepthWithNoSelectorOnlyIfThereIsOneSelectorAsSource()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("DEPTH()"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(NodeDepth.class)));
   //      NodeDepth depth = (NodeDepth)operand;
   //      assertThat(depth.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingDepthWithNoSelectorIfTheSourceIsNotASelector()
   //   {
   //      parser.parseDynamicOperand(tokens("DEPTH()"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingDepthWithSelectorNameAndProperty()
   //   {
   //      parser.parseDynamicOperand(tokens("DEPTH(tableA.property) other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingDepthWithoutClosingParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("DEPTH(tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingDepthWithoutOpeningParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("Depth  tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - PATH
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingPathOfSelector()
   //   {
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("PATH(tableA)"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(NodePath.class)));
   //      NodePath path = (NodePath)operand;
   //      assertThat(path.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingPathWithNoSelectorOnlyIfThereIsOneSelectorAsSource()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("PATH()"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(NodePath.class)));
   //      NodePath path = (NodePath)operand;
   //      assertThat(path.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingPathWithNoSelectorIfTheSourceIsNotASelector()
   //   {
   //      parser.parseDynamicOperand(tokens("PATH()"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingPathWithSelectorNameAndProperty()
   //   {
   //      parser.parseDynamicOperand(tokens("PATH(tableA.property) other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingPathWithoutClosingParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("PATH(tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingPathWithoutOpeningParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("Path  tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - NAME
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingNameOfSelector()
   //   {
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("NAME(tableA)"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(NodeName.class)));
   //      NodeName name = (NodeName)operand;
   //      assertThat(name.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingNameWithNoSelectorOnlyIfThereIsOneSelectorAsSource()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("NAME()"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(NodeName.class)));
   //      NodeName name = (NodeName)operand;
   //      assertThat(name.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingNameWithNoSelectorIfTheSourceIsNotASelector()
   //   {
   //      parser.parseDynamicOperand(tokens("NAME()"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingNameWithSelectorNameAndProperty()
   //   {
   //      parser.parseDynamicOperand(tokens("NAME(tableA.property) other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingNameWithoutClosingParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("NAME(tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingNameWithoutOpeningParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("Name  tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - LOCALNAME
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingLocalNameOfSelector()
   //   {
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("LOCALNAME(tableA)"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(NodeLocalName.class)));
   //      NodeLocalName name = (NodeLocalName)operand;
   //      assertThat(name.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingLocalNameWithNoSelectorOnlyIfThereIsOneSelectorAsSource()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("LOCALNAME()"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(NodeLocalName.class)));
   //      NodeLocalName name = (NodeLocalName)operand;
   //      assertThat(name.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingLocalNameWithNoSelectorIfTheSourceIsNotASelector()
   //   {
   //      parser.parseDynamicOperand(tokens("LOCALNAME()"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingLocalNameWithSelectorNameAndProperty()
   //   {
   //      parser.parseDynamicOperand(tokens("LOCALNAME(tableA.property) other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingLocalNameWithoutClosingParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("LOCALNAME(tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingLocalNameWithoutOpeningParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("LocalName  tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - SCORE
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingFullTextSearchScoreOfSelector()
   //   {
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("SCORE(tableA)"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(FullTextSearchScore.class)));
   //      FullTextSearchScore score = (FullTextSearchScore)operand;
   //      assertThat(score.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingFullTextSearchScoreWithNoSelectorOnlyIfThereIsOneSelectorAsSource()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("SCORE()"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(FullTextSearchScore.class)));
   //      FullTextSearchScore score = (FullTextSearchScore)operand;
   //      assertThat(score.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingFullTextSearchScoreWithNoSelectorIfTheSourceIsNotASelector()
   //   {
   //      parser.parseDynamicOperand(tokens("SCORE()"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingFullTextSearchScoreWithWithSelectorNameAndProperty()
   //   {
   //      parser.parseDynamicOperand(tokens("SCORE(tableA.property) other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingFullTextSearchScoreWithoutClosingParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("SCORE(tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingFullTextSearchScoreWithoutOpeningParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("Score  tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - PropertyValue
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringWithUnquotedSelectorNameAndUnquotedPropertyName()
   //   {
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("tableA.property"), typeSystem, mock(Join.class));
   //      assertThat(operand, is(instanceOf(PropertyValue.class)));
   //      PropertyValue value = (PropertyValue)operand;
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringWithQuotedSelectorNameAndUnquotedPropertyName()
   //   {
   //      DynamicOperand operand =
   //         parser.parseDynamicOperand(tokens("[mode:tableA].property"), typeSystem, mock(Join.class));
   //      assertThat(operand, is(instanceOf(PropertyValue.class)));
   //      PropertyValue value = (PropertyValue)operand;
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("mode:tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringWithQuotedSelectorNameAndQuotedPropertyName()
   //   {
   //      DynamicOperand operand =
   //         parser.parseDynamicOperand(tokens("[mode:tableA].[mode:property]"), typeSystem, mock(Join.class));
   //      assertThat(operand, is(instanceOf(PropertyValue.class)));
   //      PropertyValue value = (PropertyValue)operand;
   //      assertThat(value.getPropertyName(), is("mode:property"));
   //      assertThat(value.getSelectorName(), is(selectorName("mode:tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringWithOnlyPropertyNameIfSourceIsSelector()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("property"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(PropertyValue.class)));
   //      PropertyValue value = (PropertyValue)operand;
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToDynamicOperandValueFromStringWithOnlyPropertyNameIfSourceIsNotSelector()
   //   {
   //      parser.parsePropertyValue(tokens("property"), typeSystem, mock(Join.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringWithOnlySelectorNameAndPeriod()
   //   {
   //      parser.parsePropertyValue(tokens("tableA. "), typeSystem, mock(Join.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseDynamicOperand - ReferenceValue
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingReferenceValueOfSelector()
   //   {
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("REFERENCE(tableA)"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(ReferenceValue.class)));
   //      ReferenceValue value = (ReferenceValue)operand;
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(value.getPropertyName(), is(nullValue()));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingReferenceValueWithNoSelectorOnlyIfThereIsOneSelectorAsSource()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("REFERENCE()"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(ReferenceValue.class)));
   //      ReferenceValue value = (ReferenceValue)operand;
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(value.getPropertyName(), is(nullValue()));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingReferenceValueWithWithOnlyPropertyNameIfThereIsOneSelectorAsSource()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("REFERENCE(property) other"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(ReferenceValue.class)));
   //      ReferenceValue value = (ReferenceValue)operand;
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(value.getPropertyName(), is("property"));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingReferenceValueWithWithSelectorNameAndPropertyNameIfThereIsOneSelectorAsSource()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand =
   //         parser.parseDynamicOperand(tokens("REFERENCE(tableA.property) other"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(ReferenceValue.class)));
   //      ReferenceValue value = (ReferenceValue)operand;
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(value.getPropertyName(), is("property"));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingReferenceValueWithWithOnlySelectorNameMatchingThatOfOneSelectorAsSource()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      DynamicOperand operand = parser.parseDynamicOperand(tokens("REFERENCE(tableA) other"), typeSystem, source);
   //      assertThat(operand, is(instanceOf(ReferenceValue.class)));
   //      ReferenceValue value = (ReferenceValue)operand;
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(value.getPropertyName(), is(nullValue()));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingReferenceValueWithNoSelectorIfTheSourceIsNotASelector()
   //   {
   //      parser.parseDynamicOperand(tokens("REFERENCE()"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test
   //   public void shouldParseDynamicOperandFromStringContainingReferenceValueWithWithSelectorNameAndProperty()
   //   {
   //      DynamicOperand operand =
   //         parser.parseDynamicOperand(tokens("REFERENCE(tableA.property) other"), typeSystem, mock(Source.class));
   //      assertThat(operand, is(instanceOf(ReferenceValue.class)));
   //      ReferenceValue value = (ReferenceValue)operand;
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //      assertThat(value.getPropertyName(), is("property"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingReferenceValueWithoutClosingParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("REFERENCE(tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingReferenceValueWithoutSelectorOrPropertyIfTheSourceIsNotASelector()
   //   {
   //      parser.parseDynamicOperand(tokens("REFERENCE() other"), typeSystem, mock(Source.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseDynamicOperandFromStringContainingReferenceValueWithoutOpeningParenthesis()
   //   {
   //      parser.parseDynamicOperand(tokens("Reference  tableA other"), typeSystem, mock(Source.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parsePropertyValue
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParsePropertyValueFromStringWithUnquotedSelectorNameAndUnquotedPropertyName()
   //   {
   //      PropertyValue value = parser.parsePropertyValue(tokens("tableA.property"), typeSystem, mock(Join.class));
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParsePropertyValueFromStringWithQuotedSelectorNameAndUnquotedPropertyName()
   //   {
   //      PropertyValue value = parser.parsePropertyValue(tokens("[mode:tableA].property"), typeSystem, mock(Join.class));
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("mode:tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParsePropertyValueFromStringWithQuotedSelectorNameAndQuotedPropertyName()
   //   {
   //      PropertyValue value =
   //         parser.parsePropertyValue(tokens("[mode:tableA].[mode:property]"), typeSystem, mock(Join.class));
   //      assertThat(value.getPropertyName(), is("mode:property"));
   //      assertThat(value.getSelectorName(), is(selectorName("mode:tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParsePropertyValueFromStringWithOnlyPropertyNameIfSourceIsSelector()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      PropertyValue value = parser.parsePropertyValue(tokens("property"), typeSystem, source);
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParsePropertyValueFromStringWithOnlyPropertyNameIfSourceIsNotSelector()
   //   {
   //      parser.parsePropertyValue(tokens("property"), typeSystem, mock(Join.class));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParsePropertyValueFromStringWithOnlySelectorNameAndPeriod()
   //   {
   //      parser.parsePropertyValue(tokens("tableA. "), typeSystem, mock(Join.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseReferenceValue
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseReferenceValueFromStringWithUnquotedSelectorNameAndUnquotedPropertyName()
   //   {
   //      ReferenceValue value = parser.parseReferenceValue(tokens("tableA.property"), typeSystem, mock(Join.class));
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      value = parser.parseReferenceValue(tokens("tableA.property"), typeSystem, source);
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseReferenceValueFromStringWithQuotedSelectorNameAndUnquotedPropertyName()
   //   {
   //      ReferenceValue value = parser.parseReferenceValue(tokens("[mode:tableA].property"), typeSystem, mock(Join.class));
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("mode:tableA")));
   //
   //      Source source = new NamedSelector(selectorName("mode:tableA"));
   //      value = parser.parseReferenceValue(tokens("[mode:tableA].property"), typeSystem, source);
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("mode:tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseReferenceValueFromStringWithQuotedSelectorNameAndQuotedPropertyName()
   //   {
   //      ReferenceValue value =
   //         parser.parseReferenceValue(tokens("[mode:tableA].[mode:property]"), typeSystem, mock(Join.class));
   //      assertThat(value.getPropertyName(), is("mode:property"));
   //      assertThat(value.getSelectorName(), is(selectorName("mode:tableA")));
   //
   //      Source source = new NamedSelector(selectorName("mode:tableA"));
   //      value = parser.parseReferenceValue(tokens("[mode:tableA].[mode:property]"), typeSystem, source);
   //      assertThat(value.getPropertyName(), is("mode:property"));
   //      assertThat(value.getSelectorName(), is(selectorName("mode:tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseReferenceValueFromStringWithOnlyPropertyNameIfSourceIsSelector()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      ReferenceValue value = parser.parseReferenceValue(tokens("property)"), typeSystem, source);
   //      assertThat(value.getPropertyName(), is("property"));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseReferenceValueFromStringWithMatchingSelectorNameIfSourceIsSelector()
   //   {
   //      Source source = new NamedSelector(selectorName("tableA"));
   //      ReferenceValue value = parser.parseReferenceValue(tokens("tableA)"), typeSystem, source);
   //      assertThat(value.getPropertyName(), is(nullValue()));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test
   //   public void shouldParseReferenceValueFromStringWithOnlySelectorNameIfSourceIsNotSelector()
   //   {
   //      Source source = mock(Join.class);
   //      ReferenceValue value = parser.parseReferenceValue(tokens("tableA)"), typeSystem, source);
   //      assertThat(value.getPropertyName(), is(nullValue()));
   //      assertThat(value.getSelectorName(), is(selectorName("tableA")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseReferenceValueFromStringWithOnlySelectorNameAndPeriod()
   //   {
   //      parser.parseReferenceValue(tokens("tableA. "), typeSystem, mock(Join.class));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseLimit
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseLimitFromFormWithJustOneNumber()
   //   {
   //      Limit limit = parser.parseLimit(tokens("LIMIT 10"));
   //      assertThat(limit.getRowLimit(), is(10));
   //      assertThat(limit.getOffset(), is(0));
   //
   //      limit = parser.parseLimit(tokens("LIMIT 10 NONOFFSET"));
   //      assertThat(limit.getRowLimit(), is(10));
   //      assertThat(limit.getOffset(), is(0));
   //   }
   //
   //   @Test
   //   public void shouldParseLimitFromFormWithRowLimitAndOffset()
   //   {
   //      Limit limit = parser.parseLimit(tokens("LIMIT 10 OFFSET 30"));
   //      assertThat(limit.getRowLimit(), is(10));
   //      assertThat(limit.getOffset(), is(30));
   //
   //      limit = parser.parseLimit(tokens("LIMIT 10 OFFSET 30 OTHER"));
   //      assertThat(limit.getRowLimit(), is(10));
   //      assertThat(limit.getOffset(), is(30));
   //   }
   //
   //   @Test
   //   public void shouldParseLimitFromFormWithTwoCommaSeparatedNumbers()
   //   {
   //      Limit limit = parser.parseLimit(tokens("LIMIT 10,30"));
   //      assertThat(limit.getRowLimit(), is(20));
   //      assertThat(limit.getOffset(), is(10));
   //   }
   //
   //   @Test
   //   public void shouldReturnNullFromParseLimitWithNoLimitKeyword()
   //   {
   //      assertThat(parser.parseLimit(tokens("OTHER")), is(nullValue()));
   //      assertThat(parser.parseLimit(tokens("  ")), is(nullValue()));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLimitIfRowLimitNumberTokenIsNotAnInteger()
   //   {
   //      parser.parseLimit(tokens("LIMIT 10a OFFSET 30"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLimitIfOffsetNumberTokenIsNotAnInteger()
   //   {
   //      parser.parseLimit(tokens("LIMIT 10 OFFSET 30a"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLimitIfStartingRowNumberTokenIsNotAnInteger()
   //   {
   //      parser.parseLimit(tokens("LIMIT 10a,20"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseLimitIfEndingRowNumberTokenIsNotAnInteger()
   //   {
   //      parser.parseLimit(tokens("LIMIT 10,20a"));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseNamedSelector
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseNamedSelectorFromUnquotedNameWithNoAlias()
   //   {
   //      NamedSelector selector = parser.parseNamedSelector(tokens("name"));
   //      assertThat(selector.getName(), is(selectorName("name")));
   //      assertThat(selector.getAlias(), is(nullValue()));
   //      assertThat(selector.getAliasOrName(), is(selectorName("name")));
   //   }
   //
   //   @Test
   //   public void shouldParseNamedSelectorFromUnquotedNameWithUnquotedAlias()
   //   {
   //      NamedSelector selector = parser.parseNamedSelector(tokens("name AS alias"));
   //      assertThat(selector.getName(), is(selectorName("name")));
   //      assertThat(selector.getAlias(), is(selectorName("alias")));
   //      assertThat(selector.getAliasOrName(), is(selectorName("alias")));
   //   }
   //
   //   @Test
   //   public void shouldParseNamedSelectorFromQuotedNameWithUnquotedAlias()
   //   {
   //      NamedSelector selector = parser.parseNamedSelector(tokens("'name' AS alias"));
   //      assertThat(selector.getName(), is(selectorName("name")));
   //      assertThat(selector.getAlias(), is(selectorName("alias")));
   //      assertThat(selector.getAliasOrName(), is(selectorName("alias")));
   //   }
   //
   //   @Test
   //   public void shouldParseNamedSelectorFromQuotedNameWithQuotedAlias()
   //   {
   //      NamedSelector selector = parser.parseNamedSelector(tokens("'name' AS [alias]"));
   //      assertThat(selector.getName(), is(selectorName("name")));
   //      assertThat(selector.getAlias(), is(selectorName("alias")));
   //      assertThat(selector.getAliasOrName(), is(selectorName("alias")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailInParseNamedSelectorIfNoMoreTokens()
   //   {
   //      parser.parseNamedSelector(tokens("  "));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseSelectorName
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseSelectorNameFromUnquotedString()
   //   {
   //      assertThat(parser.parseSelectorName(tokens("name")), is(selectorName("name")));
   //   }
   //
   //   @Test
   //   public void shouldParseSelectorNameFromQuotedString()
   //   {
   //      assertThat(parser.parseSelectorName(tokens("'name'")), is(selectorName("name")));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailInParseSelectorNameIfNoMoreTokens()
   //   {
   //      parser.parseSelectorName(tokens("  "));
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parseName
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParseNameFromSingleQuotedString()
   //   {
   //      assertThat(parser.parseName(tokens("'jcr:name'"), typeSystem), is("jcr:name"));
   //   }
   //
   //   @Test
   //   public void shouldParseNameFromDoubleQuotedString()
   //   {
   //      assertThat(parser.parseName(tokens("\"jcr:name\""), typeSystem), is("jcr:name"));
   //   }
   //
   //   @Test
   //   public void shouldParseNameFromBracketedString()
   //   {
   //      assertThat(parser.parseName(tokens("[jcr:name]"), typeSystem), is("jcr:name"));
   //   }
   //
   //   @Test
   //   public void shouldParseNameFromUnquotedStringWithoutPrefix()
   //   {
   //      assertThat(parser.parseName(tokens("name"), typeSystem), is("name"));
   //   }
   //
   //   @Test
   //   public void shouldParseNameFromSingleQuotedStringWithoutPrefix()
   //   {
   //      assertThat(parser.parseName(tokens("'name'"), typeSystem), is("name"));
   //   }
   //
   //   @Test
   //   public void shouldParseNameFromDoubleQuotedStringWithoutPrefix()
   //   {
   //      assertThat(parser.parseName(tokens("\"name\""), typeSystem), is("name"));
   //   }
   //
   //   @Test
   //   public void shouldParseNameFromBracketedStringWithoutPrefix()
   //   {
   //      assertThat(parser.parseName(tokens("[name]"), typeSystem), is("name"));
   //   }
   //
   //   @Test
   //   public void shouldParseNameFromBracketedAndQuotedStringWithoutPrefix()
   //   {
   //      assertThat(parser.parseName(tokens("['name']"), typeSystem), is("name"));
   //      assertThat(parser.parseName(tokens("[\"name\"]"), typeSystem), is("name"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailToParseNameIfNoMoreTokens()
   //   {
   //      parser.parseName(tokens("  "), typeSystem);
   //   }
   //
   //   // ----------------------------------------------------------------------------------------------------------------
   //   // parsePath
   //   // ----------------------------------------------------------------------------------------------------------------
   //
   //   @Test
   //   public void shouldParsePathFromUnquotedStringConsistingOfSql92Identifiers()
   //   {
   //      String identifier = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
   //      assertThat(parser.parsePath(tokens(identifier), typeSystem), is(identifier));
   //   }
   //
   //   @Test
   //   public void shouldParsePathFromSingleQuotedString()
   //   {
   //      assertThat(parser.parsePath(tokens("'/a/b/c/mode:something/d'"), typeSystem), is("/a/b/c/mode:something/d"));
   //   }
   //
   //   @Test
   //   public void shouldParsePathFromDoubleQuotedString()
   //   {
   //      assertThat(parser.parsePath(tokens("\"/a/b/c/mode:something/d\""), typeSystem), is("/a/b/c/mode:something/d"));
   //   }
   //
   //   @Test(expected = InvalidQueryException.class)
   //   public void shouldFailInParsePathIfNoMoreTokens()
   //   {
   //      parser.parsePath(tokens("  "), typeSystem);
   //   }

   // ----------------------------------------------------------------------------------------------------------------
   // removeBracketsAndQuotes
   // ----------------------------------------------------------------------------------------------------------------

   //   @Test
   //   public void shouldRemoveBracketsAndQuotes()
   //   {
   //      assertThat(parser.removeBracketsAndQuotes("string"), is("string"));
   //      assertThat(parser.removeBracketsAndQuotes("[string]"), is("string"));
   //      assertThat(parser.removeBracketsAndQuotes("'string'"), is("string"));
   //      assertThat(parser.removeBracketsAndQuotes("\"string\""), is("string"));
   //      assertThat(parser.removeBracketsAndQuotes("word one and two"), is("word one and two"));
   //      assertThat(parser.removeBracketsAndQuotes("[word one and two]"), is("word one and two"));
   //      assertThat(parser.removeBracketsAndQuotes("'word one and two'"), is("word one and two"));
   //      assertThat(parser.removeBracketsAndQuotes("\"word one and two\""), is("word one and two"));
   //   }

   // ----------------------------------------------------------------------------------------------------------------
   // Utility methods
   // ----------------------------------------------------------------------------------------------------------------

   protected void parse(String query) throws InvalidQueryException
   {
      parser.parseQuery(query);
   }

   protected SelectorName selectorName(String name)
   {
      return new SelectorName(name);
   }
   //
   //   protected Name name(String name)
   //   {
   //      return (Name)typeSystem.getTypeFactory(PropertyType.NAME.getName()).create(name);
   //   }
   //
   //   protected Path path(String path)
   //   {
   //      return (Path)typeSystem.getTypeFactory(PropertyType.PATH.getName()).create(path);
   //   }
   //
   //   protected TokenStream tokens(String content)
   //   {
   //      return new TokenStream(content, new SqlQueryParser.SqlTokenizer(false), false).start();
   //   }

}
