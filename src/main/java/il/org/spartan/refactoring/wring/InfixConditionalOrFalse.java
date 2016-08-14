package il.org.spartan.refactoring.wring;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.preferences.*;
import il.org.spartan.refactoring.utils.*;

/**
 * A {@link Wring} to convert <code>b || false</code> to <code>b</code>
 *
 * @author Yossi Gil
 * @since 2015-07-20
 */
public final class InfixConditionalOrFalse extends Wring.ReplaceCurrentNode<InfixExpression> implements Kind.Simplify {
  @Override Expression replacement(final InfixExpression e) {
    return Wrings.eliminateLiteral(e, false);
  }
  @Override boolean scopeIncludes(final InfixExpression e) {
    return e != null && Is.conditionalOr(e) && Have.falseLiteral(extract.allOperands(e));
  }
  @Override String description(@SuppressWarnings("unused") final InfixExpression __) {
    return "Remove 'false' argument to '||'";
  }
}