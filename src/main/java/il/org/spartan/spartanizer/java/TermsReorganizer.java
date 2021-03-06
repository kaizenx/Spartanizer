package il.org.spartan.spartanizer.java;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.assemble.*;
import il.org.spartan.spartanizer.ast.*;

/** Reorganizer terms in a canonical way
 * @author Yossi Gil
 * @since 2016 */
public class TermsReorganizer {
  public static Expression simplify(final InfixExpression x) {
    return build(new TermsCollector(x));
  }

  private static Expression build(final List<Expression> plus, final List<Expression> minus) {
    return buildMinus(buildPlus(plus), minus);
  }

  private static Expression build(final TermsCollector c) {
    return build(c.plus(), c.minus());
  }

  private static Expression buildMinus(final Expression first, final List<Expression> rest) {
    if (first == null)
      return buildMinus(rest);
    if (rest.isEmpty())
      return first;
    rest.add(0, first);
    return subject.operands(rest).to(wizard.MINUS2);
  }

  private static Expression buildMinus(final List<Expression> xs) {
    final Expression $ = subject.operand(lisp.first(xs)).to(wizard.MINUS1);
    if (xs.size() == 1)
      return $;
    xs.remove(0);
    xs.add(0, $);
    return subject.operands(xs).to(wizard.MINUS2);
  }

  private static Expression buildPlus(final List<Expression> xs) {
    switch (xs.size()) {
      case 0:
        return null;
      case 1:
        return lisp.first(xs);
      case 2:
        return subject.pair(lisp.first(xs), lisp.second(xs)).to(wizard.PLUS2);
      default:
        return subject.operands(xs).to(wizard.PLUS2);
    }
  }
}
