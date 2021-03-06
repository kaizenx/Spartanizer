package il.org.spartan.spartanizer.wring;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.engine.*;

/** A {@link Wring} to eliminate degenerate if statements such as
 *
 * <pre>
 * if (x)
 *   ;
 * else
 *   ;
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-08-26 */
public final class IfEmptyThenEmptyElse extends Wring<IfStatement> implements Kind.Canonicalization {
  @Override String description(@SuppressWarnings("unused") final IfStatement __) {
    return "Remove 'if' statement with vacous 'then' and 'else' parts";
  }

  @Override Rewrite make(final IfStatement s) {
    return new Rewrite(description(s), s) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        s.setElseStatement(null);
        r.remove(s, g);
      }
    };
  }

  @Override boolean scopeIncludes(final IfStatement s) {
    return s != null && iz.vacuousThen(s) && iz.vacuousElse(s);
  }
}
