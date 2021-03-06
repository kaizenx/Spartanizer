package il.org.spartan.spartanizer.wring;

import static il.org.spartan.idiomatic.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.assemble.*;
import il.org.spartan.spartanizer.ast.*;

/** Replace <code>(double)X</code> by <code>1.*X</code>
 * @author Alex Kopzon
 * @author Dan Greenstein
 * @since 2016 */
public final class CastToDouble2Multiply1 extends Wring.ReplaceCurrentNode<CastExpression> implements Kind.NoImpact {
  private static NumberLiteral literal(final Expression x) {
    final NumberLiteral $ = x.getAST().newNumberLiteral();
    $.setToken("1.");
    return $;
  }

  private static InfixExpression replacement(final Expression $) {
    return subject.pair(literal($), $).to(TIMES);
  }

  @Override String description(final CastExpression x) {
    return "Use 1.*" + step.expression(x) + " instead of (double)" + step.expression(x);
  }

  @Override ASTNode replacement(final CastExpression x) {
    return eval(//
        () -> replacement(step.expression(x))//
    ).when(//
        step.type(x).isPrimitiveType() && "double".equals(step.type(x) + "") //
    );
  }
}
