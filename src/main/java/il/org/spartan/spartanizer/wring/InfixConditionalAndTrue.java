package il.org.spartan.spartanizer.wring;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.*;

/** convert
 *
 * <pre>
 * b &amp;&amp; true
 * </pre>
 *
 * to
 *
 * <pre>
 * b
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-07-20 */
public final class InfixConditionalAndTrue extends Wring.ReplaceCurrentNode<InfixExpression> implements Kind.NoImpact {
  @Override String description(@SuppressWarnings("unused") final InfixExpression __) {
    return "Remove 'true' argument to '&&'";
  }

  @Override Expression replacement(final InfixExpression x) {
    return Wrings.eliminateLiteral(x, true);
  }

  @Override boolean scopeIncludes(final InfixExpression x) {
    return iz.conditionalAnd(x) && have.trueLiteral(extract.allOperands(x));
  }
}
