package org.spartan.refactoring.wring;

import static org.eclipse.jdt.core.dom.Assignment.Operator.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;
import static org.spartan.refactoring.utils.Funcs.duplicate;
import static org.spartan.refactoring.utils.Funcs.left;
import static org.spartan.refactoring.utils.Funcs.right;
import static org.spartan.refactoring.wring.Wrings.size;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Assignment.Operator;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEditGroup;
import org.spartan.refactoring.utils.*;
import org.spartan.utils.Wrapper;

/**
 * A wring is a transformation that works on an AstNode. Such a transformation
 * make a single simplification of the tree. A wring is so small that it is
 * idempotent: Applying a wring to the output of itself is the empty operation.
 *
 * @param <N> type of node which triggers the transformation.
 * @author Yossi Gil
 * @since 2015-07-09
 */
public abstract class Wring<N extends ASTNode> {
  abstract String description(N n);
  /**
   * Determine whether the parameter is "eligible" for application of this
   * instance. The parameter must be within the scope of the current instance.
   *
   * @param n JD
   * @return <code><b>true</b></code> <i>iff</i> the argument is eligible for
   *         the simplification offered by this object.
   */
  boolean eligible(@SuppressWarnings("unused") final N n) {
    return true;
  }
  Rewrite make(final N n) {
    return make(n, null);
  }
  Rewrite make(final N n, @SuppressWarnings("unused") final ExclusionManager exclude) {
    return make(n);
  }
  /**
   * Determines whether this {@link Wring} object is not applicable for a given
   * {@link PrefixExpression} is within the "scope" of this . Note that a
   * {@link Wring} is applicable in principle to an object, but that actual
   * application will be vacuous.
   *
   * @param e JD
   * @return <code><b>true</b></code> <i>iff</i> the argument is noneligible for
   *         the simplification offered by this object.
   * @see #eligible(InfixExpression)
   */
  final boolean nonEligible(final N n) {
    return !eligible(n);
  }
  /**
   * Determines whether this {@link Wring} object is applicable for a given
   * {@link InfixExpression} is within the "scope" of this . Note that it could
   * be the case that a {@link Wring} is applicable in principle to an object,
   * but that actual application will be vacuous.
   *
   * @param n JD
   * @return <code><b>true</b></code> <i>iff</i> the argument is within the
   *         scope of this object
   */
  boolean scopeIncludes(final N n) {
    return make(n, null) != null;
  }

  static abstract class AbstractSorting extends ReplaceCurrentNode<InfixExpression> {
    @Override final String description(final InfixExpression e) {
      return "Reorder operands of " + e.getOperator();
    }
    abstract boolean sort(List<Expression> operands);
  }

  static abstract class InfixSorting extends AbstractSorting {
    @Override boolean eligible(final InfixExpression e) {
      final List<Expression> es = Extract.allOperands(e);
      return !Wrings.mixedLiteralKind(es) && sort(es);
    }
    @Override Expression replacement(final InfixExpression e) {
      final List<Expression> operands = Extract.allOperands(e);
      return !sort(operands) ? null : Subject.operands(operands).to(e.getOperator());
    }
  }

  static abstract class InfixSortingOfCDR extends AbstractSorting {
    @Override boolean eligible(final InfixExpression e) {
      final List<Expression> es = Extract.allOperands(e);
      es.remove(0);
      return !Wrings.mixedLiteralKind(es) && sort(es);
    }
    @Override Expression replacement(final InfixExpression e) {
      final List<Expression> operands = Extract.allOperands(e);
      final Expression first = operands.remove(0);
      if (!sort(operands))
        return null;
      operands.add(0, first);
      return Subject.operands(operands).to(e.getOperator());
    }
  }

  static abstract class ReplaceCurrentNode<N extends ASTNode> extends Wring<N> {
    @Override final Rewrite make(final N n) {
      return !eligible(n) ? null : new Rewrite(description(n), n) {
        @Override public void go(final ASTRewrite r, final TextEditGroup g) {
          r.replace(n, replacement(n), g);
        }
      };
    }
    abstract ASTNode replacement(N n);
    @Override boolean scopeIncludes(final N n) {
      return replacement(n) != null;
    }
  }

  static abstract class ReplaceToNextStatement<N extends ASTNode> extends Wring<N> {
    abstract ASTRewrite go(ASTRewrite r, N n, Statement nextStatement, TextEditGroup g);
    @Override Rewrite make(final N n, final ExclusionManager exclude) {
      final Statement nextStatement = Extract.nextStatement(n);
      if (nextStatement == null || !eligible(n))
        return null;
      exclude.exclude(nextStatement);
      return new Rewrite(description(n), n, nextStatement) {
        @Override public void go(final ASTRewrite r, final TextEditGroup g) {
          ReplaceToNextStatement.this.go(r, n, nextStatement, g);
        }
      };
    }
    @Override boolean scopeIncludes(final N n) {
      final Statement nextStatement = Extract.nextStatement(n);
      return nextStatement != null && go(ASTRewrite.create(n.getAST()), n, nextStatement, null) != null;
    }
  }

  static abstract class VariableDeclarationFragementAndStatement extends ReplaceToNextStatement<VariableDeclarationFragment> {
    protected static InfixExpression.Operator asInfix(final Assignment.Operator o) {
      return o == PLUS_ASSIGN ? PLUS
          : o == MINUS_ASSIGN ? MINUS
              : o == TIMES_ASSIGN ? TIMES
                  : o == DIVIDE_ASSIGN ? DIVIDE
                      : o == BIT_AND_ASSIGN ? AND
                          : o == BIT_OR_ASSIGN ? OR
                              : o == BIT_XOR_ASSIGN ? XOR
                                  : o == REMAINDER_ASSIGN ? REMAINDER
                                      : o == LEFT_SHIFT_ASSIGN ? LEFT_SHIFT //
                                          : o == RIGHT_SHIFT_SIGNED_ASSIGN ? RIGHT_SHIFT_SIGNED //
                                              : o == RIGHT_SHIFT_UNSIGNED_ASSIGN ? RIGHT_SHIFT_UNSIGNED : null;
    }
    static Expression assignmentAsExpression(final Assignment a) {
      final Operator o = a.getOperator();
      return o == ASSIGN ? duplicate(right(a)) : Subject.pair(left(a), right(a)).to(asInfix(o));
    }
    static boolean doesUseForbiddenSiblings(final VariableDeclarationFragment f, final ASTNode... ns) {
      for (final VariableDeclarationFragment b : forbiddenSiblings(f))
        if (Search.BOTH_SEMANTIC.of(b).existIn(ns))
          return true;
      return false;
    }
    static List<VariableDeclarationFragment> forbiddenSiblings(final VariableDeclarationFragment f) {
      final List<VariableDeclarationFragment> $ = new ArrayList<>();
      boolean collecting = false;
      for (final VariableDeclarationFragment brother : (List<VariableDeclarationFragment>) ((VariableDeclarationStatement) f.getParent()).fragments()) {
        if (brother == f) {
          collecting = true;
          continue;
        }
        if (collecting)
          $.add(brother);
      }
      return $;
    }
    static int removeSavings(final VariableDeclarationFragment f) {
      final VariableDeclarationStatement parent = (VariableDeclarationStatement) f.getParent();
      final List<VariableDeclarationFragment> live = live(f, parent.fragments());
      if (live.isEmpty())
        return size(parent);
      final VariableDeclarationStatement newParent = duplicate(parent);
      newParent.fragments().clear();
      newParent.fragments().addAll(live);
      return size(parent) + size(newParent);
    }
    static void remove(final VariableDeclarationFragment f, final ASTRewrite r, final TextEditGroup g) {
      final VariableDeclarationStatement parent = (VariableDeclarationStatement) f.getParent();
      final List<VariableDeclarationFragment> live = live(f, parent.fragments());
      if (live.isEmpty()) {
        r.remove(parent, g);
        return;
      }
      final VariableDeclarationStatement newParent = duplicate(parent);
      newParent.fragments().clear();
      newParent.fragments().addAll(live);
      r.replace(parent, newParent, g);
    }
    private static List<VariableDeclarationFragment> live(final VariableDeclarationFragment f, final List<VariableDeclarationFragment> fs) {
      final List<VariableDeclarationFragment> $ = new ArrayList<>();
      for (final VariableDeclarationFragment brother : fs)
        if (brother != null && brother != f && brother.getInitializer() != null)
          $.add(duplicate(brother));
      return $;
    }
    abstract ASTRewrite go(ASTRewrite r, VariableDeclarationFragment f, SimpleName n, Expression initializer, Statement nextStatement, TextEditGroup g);
    @Override final ASTRewrite go(final ASTRewrite r, final VariableDeclarationFragment f, final Statement nextStatement, final TextEditGroup g) {
      if (!Is.variableDeclarationStatement(f.getParent()))
        return null;
      final SimpleName n = f.getName();
      return n == null ? null : go(r, f, n, f.getInitializer(), nextStatement, g);
    }
    @Override Rewrite make(final VariableDeclarationFragment f, final ExclusionManager exclude) {
      final Rewrite $ = super.make(f, exclude);
      if ($ != null && exclude != null)
        exclude.exclude(f.getParent());
      return $;
    }
  }
}

final class LocalInliner {
  static Wrapper<ASTNode>[] wrap(final ASTNode[] ts) {
    @SuppressWarnings("unchecked") final Wrapper<ASTNode>[] $ = new Wrapper[ts.length];
    int i = 0;
    for (final ASTNode t : ts)
      $[i++] = new Wrapper<>(t);
    return $;
  }
  final SimpleName name;
  final ASTRewrite rewriter;
  final TextEditGroup editGroup;
  LocalInliner(final SimpleName name) {
    this(name, null, null);
  }
  LocalInliner(final SimpleName name, final ASTRewrite rewriter, final TextEditGroup editGroup) {
    this.name = name;
    this.rewriter = rewriter;
    this.editGroup = editGroup;
  }
  LocalInlineWithValue byValue(final Expression replacement) {
    return new LocalInlineWithValue(replacement);
  }

  class LocalInlineWithValue extends Wrapper<Expression> {
    LocalInlineWithValue(final Expression replacement) {
      super(Extract.core(replacement));
    }
    @SafeVarargs protected final void inlineInto(final ASTNode... es) {
      inlineInto(wrap(es));
    }
    @SafeVarargs private final void inlineInto(final Wrapper<ASTNode>... es) {
      for (final Wrapper<ASTNode> e : es)
        inlineIntoSingleton(get(), e);
    }
    /**
     * Computes the number of AST nodes in the replaced parameters
     *
     * @param es JD
     * @return A non-negative integer, computed from original size of the
     *         parameters, the number of occurrences of {@link #name} in the
     *         operands, and the size of the replacement.
     */
    int replacedSize(final ASTNode... es) {
      return size(es) + uses(es).size() * (size(get()) - 1);
    }
    boolean canInlineInto(final ASTNode... es) {
      return !Search.findsDefinitions(name).in(es) && (Is.sideEffectFree(get()) || uses(es).size() <= 1);
    }
    private List<Expression> uses(final ASTNode... es) {
      return Search.forAllOccurencesOf(name).in(es);
    }
    private void inlineIntoSingleton(final ASTNode replacement, final Wrapper<ASTNode> e) {
      final ASTNode oldExpression = e.get();
      final ASTNode newExpression = duplicate(e.get());
      e.set(newExpression);
      rewriter.replace(oldExpression, newExpression, editGroup);
      for (final ASTNode use : Search.forAllOccurencesExcludingDefinitions(name).in(newExpression))
        if (use instanceof Expression)
          rewriter.replace(use, new Plant((Expression) replacement).into(use.getParent()), editGroup);
        else
          rewriter.replace(use, replacement, editGroup);
    }
  }
}