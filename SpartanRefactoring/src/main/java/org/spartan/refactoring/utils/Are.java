package org.spartan.refactoring.utils;

import java.util.List;

import org.eclipse.jdt.core.dom.Expression;

/**
 * A an empty <code><b>enum</b></code> for fluent programming. The name says it
 * all: The name, followed by a dot, followed by a method name, should read like
 * a word phrase.
 *
 * @author Yossi Gil
 * @since 2015-07-16
 *
 */
public enum Are {
  ;
  /**
   * @param es
   *          JD
   * @return <code><b>true</b></code> <i>iff</i> all elements in the argument
   *         are provably not a {@link String}.
   */
  public static boolean notString(final List<Expression> es) {
    for (final Expression e : es)
      if (!Is.notString(e))
        return false;
    return true;
  }
}