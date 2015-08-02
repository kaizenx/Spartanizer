package org.spartan.refactoring.wring;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.spartan.hamcrest.CoreMatchers.is;
import static org.spartan.hamcrest.MatcherAssert.assertThat;
import static org.spartan.hamcrest.OrderingComparison.greaterThanOrEqualTo;
import static org.spartan.refactoring.spartanizations.TESTUtils.asSingle;
import static org.spartan.refactoring.spartanizations.TESTUtils.assertSimilar;
import static org.spartan.refactoring.spartanizations.TESTUtils.c;
import static org.spartan.refactoring.spartanizations.TESTUtils.compressSpaces;
import static org.spartan.refactoring.spartanizations.TESTUtils.e;
import static org.spartan.refactoring.spartanizations.TESTUtils.i;
import static org.spartan.refactoring.spartanizations.TESTUtils.p;
import static org.spartan.refactoring.spartanizations.TESTUtils.peelExpression;
import static org.spartan.refactoring.spartanizations.TESTUtils.peelStatement;
import static org.spartan.refactoring.spartanizations.TESTUtils.s;
import static org.spartan.refactoring.spartanizations.TESTUtils.wrapExpression;
import static org.spartan.refactoring.spartanizations.TESTUtils.wrapStatement;
import static org.spartan.refactoring.utils.Funcs.asBlock;
import static org.spartan.refactoring.utils.Funcs.asIfStatement;
import static org.spartan.refactoring.utils.Restructure.flatten;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.junit.Test;
import org.junit.internal.builders.IgnoredClassRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.spartan.refactoring.spartanizations.Spartanization;
import org.spartan.refactoring.spartanizations.TESTUtils;
import org.spartan.refactoring.utils.As;
import org.spartan.refactoring.utils.Extract;
import org.spartan.refactoring.utils.Funcs;
import org.spartan.utils.Range;
import org.spartan.utils.Wrapper;

/**
 * @author Yossi Gil
 * @since 2015-07-18
 */
@SuppressWarnings({ "javadoc", "restriction" }) //
@RunWith(IgnoredClassRunner.class) //
public abstract class AbstractWringTest extends AbstractTestBase {
  protected final Wring inner;
  /**
   * Instantiates the enclosing class ({@link AbstractWringTest})
   *
   * @param inner JD
   */
  AbstractWringTest(final Wring inner) {
    this.inner = inner;
  }
  protected CompilationUnit asCompilationUnit() {
    final ASTNode $ = As.COMPILIATION_UNIT.ast(wrapExpression(input));
    assertThat($, is(notNullValue()));
    assertThat($, is(instanceOf(CompilationUnit.class)));
    return (CompilationUnit) $;
  }
  protected ConditionalExpression asConditionalExpression() {
    final ConditionalExpression $ = c(input);
    assertNotNull($);
    return $;
  }
  protected Expression asExpression() {
    final Expression $ = e(input);
    assertNotNull($);
    return $;
  }
  protected InfixExpression asInfixExpression() {
    final InfixExpression $ = i(input);
    assertNotNull($);
    return $;
  }
  protected Statement asMe() {
    final Statement $ = s(input);
    assertNotNull($);
    return $;
  }
  protected PrefixExpression asPrefixExpression() {
    final PrefixExpression $ = p(input);
    assertNotNull($);
    return $;
  }
  void assertLegible(final String expression) {
    assertTrue(inner.eligible((InfixExpression) As.EXPRESSION.ast(expression)));
  }
  void assertNotLegible(final Block b) {
    assertThat(inner.eligible(b), is(false));
  }
  void assertNotLegible(final Expression e) {
    assertThat(inner.eligible(e), is(false));
  }
  void assertNotLegible(final IfStatement b) {
    assertThat(inner.eligible(b), is(false));
  }
  void assertNotLegible(final String code) {
    for (As as : As.values())
      assertNotWithinScope(as.ast(code));
  }
  void assertNotWithinScope(final ASTNode n) {
    assertWithinScope(Funcs.asExpression(n));
    assertNotWithinScope(Funcs.asIfStatement(n));
    assertNotWithinScope(asBlock(n));
  }
  void assertNotWithinScope(final String expression) {
    assertNotWithinScope(e(expression));
  }
  void assertWithinScope(final Expression e) {
    assertTrue(inner.scopeIncludes(e));
  }
  void assertWithinScope(final String expression) {
    assertWithinScope(e(expression));
  }

  /**
   * @author Yossi Gil
   * @since 2015-07-18
   */
  public static abstract class Noneligible extends InScope {
    /** Description of a test case for {@link Parameter} annotation */
    protected static final String DESCRIPTION = "Test #{index}. ({0}) \"{1}\" ==>|";
    /**
     * Instantiates the enclosing class ({@link Noneligible})
     *
     * @param simplifier JD
     */
    Noneligible(final Wring simplifier) {
      super(simplifier);
    }
    @Override @Test public void correctSimplifier() {
      assertThat(Wrings.find(asExpression()), is(inner));
    }
    @Test public void eligible() {
      assertFalse(inner.eligible(asExpression()));
    }
    @Override @Test public void findsSimplifier() {
      assertNotNull(Wrings.find(asExpression()));
    }
    @Test public void noneligible() {
      assertTrue(inner.noneligible(asExpression()));
    }
    @Test public void noOpporunity() {
      final CompilationUnit u = asCompilationUnit();
      assertEquals(u.toString() + wringer.findOpportunities(u), 0, wringer.findOpportunities(u).size());
    }
    @Test public void simiplifies() throws MalformedTreeException, IllegalArgumentException, BadLocationException {
      final CompilationUnit u = asCompilationUnit();
      final ASTRewrite r = wringer.createRewrite(u, null);
      final Document d = asDocument();
      r.rewriteAST(d, null).apply(d);
      assertSimilar(compressSpaces(peelExpression(d.get())), compressSpaces(input));
      assertSimilar(wrapExpression(input), d.get());
    }
    protected final Document asDocument() {
      return new Document(wrapExpression(input));
    }

    public static abstract class Infix extends Noneligible {
      /** Instantiates the enclosing class ({@link Infix})@param simplifier */
      Infix(final Wring w) {
        super(w);
      }
      @Test public void correctSimplifieInfix() {
        assertThat(Wrings.find(asInfixExpression()), is(inner));
      }
      @Test public void flattenIsIdempotentt() {
        final InfixExpression flatten = flatten(asInfixExpression());
        assertThat(flatten(flatten).toString(), is(flatten.toString()));
      }
      @Test public void inputIsInfixExpression() {
        final InfixExpression e = asInfixExpression();
        assertNotNull(e);
      }
    }
  }

  /**
   * @author Yossi Gil
   * @since 2015-07-18
   */
  public static abstract class OutOfScope extends AbstractWringTest {
    /** Description of a test case for {@link Parameter} annotation */
    protected static final String DESCRIPTION = "Test #{index}. ({0}) \"{1}\" N/A";
    /** Instantiates the enclosing class ({@link OutOfScope})@param inner */
    public OutOfScope(final Wring inner) {
      super(inner);
    }

    public static abstract class Expression extends OutOfScope {
      public Expression(final Wring inner) {
        super(inner);
      }
      @Test public void scopeDoesNotIncludeAsBlock() {
        assertThat(inner.scopeIncludes(asBlock(asMe())), is(false));
      }
      @Test public void scopeDoesNotIncludeAsExpression() {
        assertThat(inner.scopeIncludes(asExpression()), is(false));
      }
      @Test public void scopeDoesNotIncludeAsIfStatement() {
        assertThat(inner.scopeIncludes(asIfStatement(asMe())), is(false));
      }

      public static abstract class Infix extends OutOfScope.Expression {
        /** Instantiates the enclosing class ({@link Infix})@param simplifier */
        Infix(final Wring w) {
          super(w);
        }
        @Test public void inputIsInfixExpression() {
          final InfixExpression e = asInfixExpression();
          assertNotNull(e);
        }
      }
    }
  }

  public static abstract class Wringed extends InScope {
    /** Description of a test case for {@link Parameter} annotation */
    protected static final String DESCRIPTION = "Test #{index}. ({0}) \"{1}\" ==> \"{2}\"";
    /** What should the output be */
    @Parameter(2) public String expected;
    Wringed(final Wring inner) {
      super(inner);
    }

    public static class Conditional extends WringedExpression {
      /**
       * Instantiates the enclosing class ({@link Conditional})
       *
       * @param w JD
       */
      Conditional(final Wring w) {
        super(w);
      }
      @Test public void inputIsConditionalExpression() {
        assertNotNull(asConditionalExpression());
      }
    }

    public static abstract class IfStatementAndSurrounding extends WringedIfStatement {
      /**
       * Instantiates the enclosing class ({@link IfStatementAndSurrounding})
       *
       * @param inner
       */
      IfStatementAndSurrounding(final Wring inner) {
        super(inner);
      }
      @Override @Test public void hasReplacement() {
        // Eliminate test; in surrounding, you do not replace the text.
      }
      @Test public void simiplifies() throws MalformedTreeException, IllegalArgumentException {
        final CompilationUnit u = asCompilationUnit();
        final Document excpected = TESTUtils.rewrite(wringer, u, asDocument());
        final String peeled = peelStatement(excpected.get());
        if (expected.equals(peeled))
          return;
        if (input.equals(peeled))
          fail("Nothing done on " + input);
        if (compressSpaces(peeled).equals(compressSpaces(input)))
          assertNotEquals("Wringing of " + input + " amounts to mere reformatting", compressSpaces(peeled), compressSpaces(input));
        assertSimilar(expected, peeled);
        assertSimilar(wrapStatement(expected), excpected);
      }
      /**
       * In case of an IfStatemnet and surrounding, we search and then find the
       * first If statement in the input.
       */
      @Override protected IfStatement asMe() {
        return Extract.firstIfStatement(As.STATEMENTS.ast(input));
      }
    }

    public static abstract class Infix extends WringedExpression {
      /** Instantiates the enclosing class ({@link Infix})@param simplifier */
      Infix(final Wring w) {
        super(w);
      }
      @Test public void flattenIsIdempotentt() {
        final InfixExpression flatten = flatten(asInfixExpression());
        assertThat(flatten(flatten).toString(), is(flatten.toString()));
      }
      @Test public void hasReplacementAsInfix() {
        assertNotNull(inner.replacement(asInfixExpression()));
      }
      @Test public void inputIsInfixExpression() {
        assertNotNull(asInfixExpression());
      }
    }
  }

  public static abstract class WringedBlock extends WringedStatement {
    /** Description of a test case for {@link Parameter} annotation */
    protected static final String DESCRIPTION = "Test #{index}. ({0}) \"{1}\" ==> \"{2}\"";
    /** What should the output be */
    @Parameter(2) public String expected;
    /**
     * Instantiates the enclosing class ({@link WringedBlock})
     *
     * @param inner
     */
    WringedBlock(final Wring inner) {
      super(inner);
    }
    @Test public void correctSimplifierAsBlock() {
      assertEquals(inner, Wrings.find(asBlock(asMe())));
    }
    @Test public void createRewrite() throws MalformedTreeException, IllegalArgumentException, BadLocationException {
      final Document d = new Document(wrapStatement(input));
      final CompilationUnit u = asCompilationUnit();
      final ASTRewrite r = wringer.createRewrite(u, null);
      assertThat(r.rewriteAST(d, null).apply(d), is(notNullValue()));
    }
    @Test public void eligible() {
      assertTrue(inner.eligible(asMe()));
    }
    @Test public void findsSimplifierAsBlock() {
      assertNotNull(Wrings.find(asMe()));
    }
    @Test public void hasOpportunity() {
      assertTrue(inner.scopeIncludes(asMe()));
      final CompilationUnit u = asCompilationUnit();
      assertThat(u.toString(), wringer.findOpportunities(u).size(), is(greaterThanOrEqualTo(0)));
    }
    @Test public void hasReplacement() {
      assertNotNull(inner.replacement(asMe()));
    }
    @Test public void noneligible() {
      assertFalse(inner.noneligible(asMe()));
    }
    @Test public void peelableOutput() {
      assertEquals(expected, peelStatement(wrapStatement(expected)));
    }
    @Test public void scopeIncludes() {
      assertTrue(inner.scopeIncludes(asMe()));
    }
    @Test public void simiplifies() throws MalformedTreeException, IllegalArgumentException {
      final CompilationUnit u = asCompilationUnit();
      final Document excpected = TESTUtils.rewrite(wringer, u, new Document(wrapStatement(input)));
      final String peeled = peelStatement(excpected.get());
      if (expected.equals(peeled))
        return;
      if (input.equals(peeled))
        fail("Nothing done on " + input);
      if (compressSpaces(peeled).equals(compressSpaces(input)))
        assertNotEquals("Wringing of " + input + " amounts to mere reformatting", compressSpaces(peeled), compressSpaces(input));
      assertSimilar(expected, peeled);
      assertSimilar(wrapStatement(expected), excpected);
    }
    @Override protected Block asMe() {
      final Statement s = s(input);
      assertNotNull(s);
      final Block b = asBlock(s);
      assertNotNull(b);
      return b;
    }

    public static class Conditional extends WringedExpression {
      /**
       * Instantiates the enclosing class ({@link Infix})
       *
       * @param w JD
       */
      Conditional(final Wring w) {
        super(w);
      }
      @Test public void inputIsConditionalExpression() {
        assertNotNull(asConditionalExpression());
      }
    }

    public static abstract class Infix extends WringedExpression {
      /** Instantiates the enclosing class ({@link Infix})@param simplifier */
      Infix(final Wring w) {
        super(w);
      }
      @Test public void flattenIsIdempotentt() {
        final InfixExpression flatten = flatten(asInfixExpression());
        assertThat(flatten(flatten).toString(), is(flatten.toString()));
      }
      @Test public void hasReplacementAsInfix() {
        assertNotNull(inner.replacement(asInfixExpression()));
      }
      @Test public void inputIsInfixExpression() {
        assertNotNull(asInfixExpression());
      }
      @Override protected final CompilationUnit asCompilationUnit() {
        final ASTNode $ = As.COMPILIATION_UNIT.ast(wrapStatement(input));
        assertThat($, is(notNullValue()));
        assertThat($, is(instanceOf(CompilationUnit.class)));
        return (CompilationUnit) $;
      }
    }
  }

  /**
   * @author Yossi Gil
   * @since 2015-07-15
   */
  public static abstract class WringedExpression extends InScope {
    /** Description of a test case for {@link Parameter} annotation */
    protected static final String DESCRIPTION = "Test #{index}. ({0}) \"{1}\" ==> \"{2}\"";
    /** What should the output be */
    @Parameter(2) public String expected;
    /**
     * Instantiates the enclosing class ( {@link WringedExpression})
     */
    WringedExpression(final Wring inner) {
      super(inner);
    }
    @Override @Test public void correctSimplifier() {
      assertEquals(inner, Wrings.find(asExpression()));
    }
    @Test public void createRewrite() throws MalformedTreeException, IllegalArgumentException, BadLocationException {
      final Document d = new Document(wrapExpression(input));
      final CompilationUnit u = asCompilationUnit();
      final ASTRewrite r = wringer.createRewrite(u, null);
      assertThat(r.rewriteAST(d, null).apply(d), is(notNullValue()));
    }
    @Test public void eligible() {
      assertTrue(inner.eligible(asExpression()));
    }
    @Override @Test public void findsSimplifier() {
      assertNotNull(Wrings.find(asExpression()));
    }
    @Test public void hasOpportunity() {
      assertTrue(inner.scopeIncludes(asExpression()));
      final CompilationUnit u = asCompilationUnit();
      final List<Range> findOpportunities = wringer.findOpportunities(u);
      assertThat(u.toString(), findOpportunities.size(), is(greaterThanOrEqualTo(1)));
    }
    @Test public void hasReplacement() {
      assertNotNull(inner.replacement(asExpression()));
    }
    @Test public void noneligible() {
      assertFalse(inner.noneligible(asExpression()));
    }
    @Test public void peelableOutput() {
      assertEquals(expected, peelExpression(wrapExpression(expected)));
    }
    @Test public void scopeIncludes() {
      assertTrue(inner.scopeIncludes(asExpression()));
    }
    @Test public void simiplifies() throws MalformedTreeException, IllegalArgumentException {
      final CompilationUnit u = asCompilationUnit();
      final Document actual = TESTUtils.rewrite(wringer, u, asDocument());
      final String peeled = peelExpression(actual.get());
      if (expected.equals(peeled))
        return;
      if (input.equals(peeled))
        fail("Nothing done on " + input);
      if (compressSpaces(peeled).equals(compressSpaces(input)))
        assertNotEquals("Wringing of " + input + " amounts to mere reformatting", compressSpaces(peeled), compressSpaces(input));
      assertSimilar(expected, peeled);
      assertSimilar(wrapExpression(expected), actual);
    }
    @Test public void simiplifiesExpanded() throws MalformedTreeException, IllegalArgumentException {
      final CompilationUnit u = asCompilationUnit();
      Document d = asDocument();
      Document actual = null;
      // TESTUtils.rewrite(wringer, u, d);
      try {
        wringer.createRewrite(u, null).rewriteAST(d, null).apply(d);
        actual = d;
      } catch (final MalformedTreeException e) {
        fail(e.getMessage());
      } catch (final IllegalArgumentException e) {
        e.printStackTrace();
        fail(e.getMessage());
      } catch (final BadLocationException e) {
        fail(e.getMessage());
      }
      if (actual == null)
        return;
      final String peeled = peelExpression(actual.get());
      if (expected.equals(peeled))
        return;
      if (input.equals(peeled))
        fail("Nothing done on " + input);
      if (compressSpaces(peeled).equals(compressSpaces(input)))
        assertNotEquals("Wringing of " + input + " amounts to mere reformatting", compressSpaces(peeled), compressSpaces(input));
      assertSimilar(expected, peeled);
      assertSimilar(wrapExpression(expected), actual);
    }
    @Override protected CompilationUnit asCompilationUnit() {
      final ASTNode $ = As.COMPILIATION_UNIT.ast(wrapExpression(input));
      assertThat($, is(notNullValue()));
      assertThat($, is(instanceOf(CompilationUnit.class)));
      return (CompilationUnit) $;
    }
    @Override protected Document asDocument() {
      return new Document(wrapExpression(input));
    }
    @Override protected Statement asMe() {
      final Statement $ = asSingle(input);
      assertNotNull($);
      return $;
    }

    public static class Conditional extends WringedExpression {
      /**
       * Instantiates the enclosing class ({@link Infix})
       *
       * @param w JD
       */
      Conditional(final Wring w) {
        super(w);
      }
      @Test public void inputIsConditionalExpression() {
        assertThat(asExpression(), instanceOf(ConditionalExpression.class));
        assertNotNull(asConditionalExpression());
      }
      @Test public void scopeIncludesAsConditionalExpression() {
        assertTrue(inner.scopeIncludes(asConditionalExpression()));
      }
    }

    public static abstract class Infix extends WringedExpression {
      /** Instantiates the enclosing class ({@link Infix})@param simplifier */
      Infix(final Wring w) {
        super(w);
      }
      @Test public void flattenIsIdempotentt() {
        final InfixExpression flatten = flatten(asInfixExpression());
        assertThat(flatten(flatten).toString(), is(flatten.toString()));
      }
      @Test public void hasReplacementAsInfix() {
        assertNotNull(inner.replacement(asInfixExpression()));
      }
      @Test public void inputIsInfixExpression() {
        assertNotNull(asInfixExpression());
      }
    }
  }

  public static abstract class WringedIfStatement extends WringedStatement {
    /** Description of a test case for {@link Parameter} annotation */
    protected static final String DESCRIPTION = "Test #{index}. ({0}) \"{1}\" ==> \"{2}\"";
    /** What should the output be */
    @Parameter(2) public String expected;
    /**
     * Instantiates the enclosing class ({@link WringedInput})
     *
     * @param inner
     */
    WringedIfStatement(final Wring inner) {
      super(inner);
    }
    @Override @Test public void correctSimplifier() {
      assertThat(asMe().toString(), Wrings.find(asMe()), is(inner));
    }
    @Test public void createRewrite() throws MalformedTreeException, IllegalArgumentException, BadLocationException {
      final Document d = new Document(wrapStatement(input));
      final CompilationUnit u = asCompilationUnit();
      final ASTRewrite r = wringer.createRewrite(u, null);
      assertThat(r.rewriteAST(d, null).apply(d), is(notNullValue()));
    }
    @Test public void eligible() {
      final IfStatement s = asMe();
      assertTrue(s.toString(), inner.eligible(s));
    }
    @Override @Test public void findsSimplifier() {
      assertNotNull(Wrings.find(asMe()));
    }
    @Test public void hasOpportunity() {
      assertTrue(inner.scopeIncludes(asMe()));
      final CompilationUnit u = asCompilationUnit();
      final List<Range> findOpportunities = wringer.findOpportunities(u);
      assertThat(u.toString(), findOpportunities.size(), is(greaterThanOrEqualTo(0)));
    }
    @Test public void hasReplacement() {
      assertNotNull(inner.replacement(asMe()));
    }
    @Test public void hasSimplifier() {
      assertThat(asMe().toString(), Wrings.find(asMe()), is(notNullValue()));
    }
    @Test public void noneligible() {
      assertFalse(inner.noneligible(asMe()));
    }
    @Test public void peelableOutput() {
      assertEquals(expected, peelStatement(wrapStatement(expected)));
    }
    @Test public void scopeIncludesAsMe() {
      assertThat(asMe().toString(), inner.scopeIncludes(asMe()), is(true));
    }
    @Test public void simiplifies() throws MalformedTreeException, IllegalArgumentException {
      final CompilationUnit u = asCompilationUnit();
      final Document excpected = TESTUtils.rewrite(wringer, u, new Document(wrapStatement(input)));
      final String peeled = peelStatement(excpected.get());
      if (expected.equals(peeled))
        return;
      if (input.equals(peeled))
        fail("Nothing done on " + input);
      if (compressSpaces(peeled).equals(compressSpaces(input)))
        assertNotEquals("Wringing of " + input + " amounts to mere reformatting", compressSpaces(peeled), compressSpaces(input));
      assertSimilar(expected, peeled);
      assertSimilar(wrapStatement(expected), excpected);
    }
    @Override protected IfStatement asMe() {
      final Statement $ = asSingle(input);
      assertNotNull($);
      return asIfStatement($);
    }

    public static class Conditional extends WringedExpression {
      /**
       * Instantiates the enclosing class ({@link Infix})
       *
       * @param w JD
       */
      Conditional(final Wring w) {
        super(w);
      }
      @Test public void inputIsConditionalExpression() {
        assertNotNull(asConditionalExpression());
      }
    }

    public static abstract class Infix extends WringedExpression {
      /** Instantiates the enclosing class ({@link Infix})@param simplifier */
      Infix(final Wring w) {
        super(w);
      }
      @Test public void flattenIsIdempotentt() {
        final InfixExpression flatten = flatten(asInfixExpression());
        assertThat(flatten(flatten).toString(), is(flatten.toString()));
      }
      @Test public void hasReplacementAsInfix() {
        assertNotNull(inner.replacement(asInfixExpression()));
      }
      @Test public void inputIsInfixExpression() {
        assertNotNull(asInfixExpression());
      }
    }
  }

  public static abstract class WringedStatement extends InScope {
    WringedStatement(Wring inner) {
      super(inner);
    }
    @Override @Test public void correctSimplifier() {
      assertEquals(inner, Wrings.find(asMe()));
    }
    @Override @Test public void findsSimplifier() {
      assertNotNull(Wrings.find(asMe()));
    }
    @Override protected final CompilationUnit asCompilationUnit() {
      final ASTNode $ = As.COMPILIATION_UNIT.ast(wrapStatement(input));
      assertThat($, is(notNullValue()));
      assertThat($, is(instanceOf(CompilationUnit.class)));
      return (CompilationUnit) $;
    }
    @Override protected final Document asDocument() {
      return new Document(wrapStatement(input));
    }
  }

  /**
   * @author Yossi Gil
   * @since 2015-07-18
   */
  static abstract class InScope extends AbstractWringTest {
    protected final Trimmer wringer = new Trimmer();
    InScope(final Wring inner) {
      super(inner);
    }
    @Test public void correctSimplifier() {
      assertEquals(inner, Wrings.find(asExpression()));
    }
    @Test public void findsSimplifier() {
      assertNotNull(Wrings.find(asExpression()));
    }
    protected abstract Document asDocument();

    /**
     * @author Yossi Gil
     * @since 2015-07-15
     */
    static abstract class WringedInput extends AbstractWringTest {
      /** How should a test case like this be described? */
      protected static final String DESCRIPTION = "{index}: \"{1}\" => \"{2}\" ({0})";
      static Document rewrite(final Spartanization s, final CompilationUnit u, final Document d) {
        try {
          s.createRewrite(u, null).rewriteAST(d, null).apply(d);
          return d;
        } catch (final MalformedTreeException e) {
          fail(e.getMessage());
        } catch (final IllegalArgumentException e) {
          fail(e.getMessage());
        } catch (final BadLocationException e) {
          fail(e.getMessage());
        }
        return null;
      }
      /** Where the expected output can be found? */
      @Parameter(2) public String output;
      protected final Trimmer trimmer = new Trimmer();
      /**
       * Instantiates the enclosing class ({@link WringedExpression})
       *
       * @param w JD
       */
      WringedInput(final Wring w) {
        super(w);
      }
      @Test public void correctSimplifier() {
        assertEquals(inner, Wrings.find(asExpression()));
      }
      @Test public void createRewrite() throws MalformedTreeException, IllegalArgumentException, BadLocationException {
        final Document d = new Document(wrapExpression(input));
        final CompilationUnit u = asCompilationUnit();
        final ASTRewrite r = trimmer.createRewrite(u, null);
        assertThat(r.rewriteAST(d, null).apply(d), is(notNullValue()));
      }
      @Test public void eligible() {
        assertTrue(inner.eligible(asExpression()));
      }
      @Test public void findsSimplifier() {
        assertNotNull(Wrings.find(asExpression()));
      }
      @Test public void hasReplacement() {
        assertNotNull(inner.replacement(asExpression()));
      }
      @Test public void noneligible() {
        assertFalse(inner.noneligible(asExpression()));
      }
      @Test public void oneOpporunity() {
        final CompilationUnit u = asCompilationUnit();
        assertEquals(u.toString(), 1, trimmer.findOpportunities(u).size());
        assertTrue(inner.scopeIncludes(asExpression()));
      }
      @Test public void peelableOutput() {
        assertEquals(output, peelExpression(wrapExpression(output)));
      }
      @Test public void scopeIncludes() {
        assertFalse(inner.scopeIncludes(asExpression()));
      }
      @Test public void simiplifies() throws MalformedTreeException, IllegalArgumentException {
        final CompilationUnit u = asCompilationUnit();
        final Document excpected = TESTUtils.rewrite(trimmer, u, new Document(wrapExpression(input)));
        final String peeled = peelExpression(excpected.get());
        if (output.equals(peeled))
          return;
        if (input.equals(peeled))
          fail("Nothing done on " + input);
        if (compressSpaces(peeled).equals(compressSpaces(input)))
          assertNotEquals("Wringing of " + input + " amounts to mere reformatting", compressSpaces(peeled), compressSpaces(input));
        assertSimilar(output, peeled);
        assertSimilar(wrapExpression(output), excpected);
      }
      protected abstract Document asDocument();
    }
  }
}