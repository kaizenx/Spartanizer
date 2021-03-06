package il.org.spartan.spartanizer.assemble;

import static il.org.spartan.spartanizer.ast.iz.*;
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.wring.*;

/** An empty <code><b>enum</b></code> for fluent programming. The name should
 * say it all: The name, followed by a dot, followed by a method name, should
 * read like a sentence phrase.
 * @author Yossi Gil
 * @since 2016 */
public enum make {
  ;
  /** Swap the order of the left and right operands to an expression, changing
   * the operator if necessary.
   * @param ¢ JD
   * @return a newly created expression with its operands thus swapped.
   * @throws IllegalArgumentException when the parameter has extra operands.
   * @see InfixExpression#hasExtendedOperands */
  public static InfixExpression conjugate(final InfixExpression ¢) {
    if (¢.hasExtendedOperands())
      throw new IllegalArgumentException(¢ + ": flipping undefined for an expression with extra operands ");
    return subject.pair(step.right(¢), step.left(¢)).to(wizard.conjugate(¢.getOperator()));
  }

  public static ASTHolder from(final ASTNode ¢) {
    assert ¢ != null;
    return new make.ASTHolder(¢.getAST());
  }

  public static IfStatement ifWithoutElse(final Statement s, final InfixExpression condition) {
    final IfStatement $ = condition.getAST().newIfStatement();
    $.setExpression(condition);
    $.setThenStatement(s);
    $.setElseStatement(null);
    return $;
  }

  public static Expression minus(final Expression x) {
    final PrefixExpression ¢ = az.prefixExpression(x);
    return ¢ == null ? minus(x, az.numberLiteral(x))
        : ¢.getOperator() == wizard.MINUS1 ? ¢.getOperand() //
            : ¢.getOperator() == wizard.PLUS1 ? subject.operand(¢.getOperand()).to(wizard.MINUS1)//
                : x;
  }

  /** Create a new {@link SimpleName} instance at the AST of the parameter
   * @param n JD
   * @param newName the name that the returned value shall bear
   * @return a new {@link SimpleName} instance at the AST of the parameter */
  public static SimpleName newSimpleName(final ASTNode n, final String newName) {
    return n.getAST().newSimpleName(newName);
  }

  /** @param ¢ JD
   * @return parameter, but logically negated and simplified */
  public static Expression notOf(final Expression ¢) {
    final PrefixExpression $ = subject.operand(¢).to(NOT);
    final Expression $$ = PrefixNotPushdown.simplifyNot($);
    return $$ == null ? $ : $$;
  }

  public static ParenthesizedExpression parethesized(final Expression x) {
    final ParenthesizedExpression $ = x.getAST().newParenthesizedExpression();
    $.setExpression(step.parent(x) == null ? x : duplicate.of(x));
    return $;
  }

  /** @param ¢ the expression to return in the return statement
   * @return new return statement */
  public static ThrowStatement throwOf(final Expression ¢) {
    return subject.operand(¢).toThrow();
  }

  static Expression minus(final Expression x, final NumberLiteral l) {
    return l == null ? minusOf(x) //
        : newLiteral(l, literal0(l) ? "0" : signAdjust(l.getToken())) //
    ;
  }

  static List<Expression> minus(final List<Expression> xs) {
    final List<Expression> $ = new ArrayList<>();
    $.add(lisp.first(xs));
    for (final Expression e : lisp.rest(xs))
      $.add(minusOf(e));
    return $;
  }

  static Expression minusOf(final Expression x) {
    return literal0(x) ? x : subject.operand(x).to(wizard.MINUS1);
  }

  static NumberLiteral newLiteral(final ASTNode n, final String token) {
    final NumberLiteral $ = n.getAST().newNumberLiteral();
    $.setToken(token);
    return $;
  }

  private static String signAdjust(final String token) {
    return token.startsWith("-") ? token.substring(1) //
        : "-" + token.substring(token.startsWith("+") ? 1 : 0);
  }

  public static StringLiteral makeStringLiteral(final ASTNode ¢) {
    return make.from(¢).literal("");
  }

  public static class ASTHolder {
    private final AST ast;

    public ASTHolder(final AST ast) {
      this.ast = ast;
    }

    public StringLiteral literal(final String s) {
      final StringLiteral $ = ast.newStringLiteral();
      $.setLiteralValue(s);
      return $;
    }

    public NumberLiteral literal(final int i) {
      return ast.newNumberLiteral(i + "");
    }
  }
}
