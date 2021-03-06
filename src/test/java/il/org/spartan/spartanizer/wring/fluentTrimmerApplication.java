package il.org.spartan.spartanizer.wring;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.jface.text.*;
import org.eclipse.text.edits.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.engine.*;

public class fluentTrimmerApplication extends Trimmer.With {
  public final String codeFragment;
  public final GuessedContext guessedContext;
  public final String wrappedFragment;
  public final CompilationUnit compilationUnit;
  public final Document document;
  public final ASTRewrite createRewrite;
  public final TextEdit textEdit;
  public final UndoEdit undoEdit;

  public fluentTrimmerApplication(final Trimmer t, final String codeFragment) {
    t.super();
    this.codeFragment = codeFragment;
    assert codeFragment != null;
    dump.go(codeFragment);
    guessedContext = GuessedContext.find(codeFragment);
    assert guessedContext != null;
    dump.go(guessedContext);
    wrappedFragment = guessedContext.on(codeFragment);
    dump.go(wrappedFragment, "wrappedFragment");
    assert wrappedFragment != null;
    document = new Document(wrappedFragment);
    assert document != null;
    dump.go(document, "This is the document");
    dump.go(document.get(), "and this is its content");
    compilationUnit = guessedContext.intoCompilationUnit(document.get());
    assert compilationUnit != null;
    createRewrite = trimmer().createRewrite(compilationUnit, new NullProgressMonitor());
    assert createRewrite != null;
    textEdit = createRewrite.rewriteAST(document, null);
    assert textEdit != null;
    try {
      undoEdit = textEdit.apply(document);
      assert undoEdit != null;
      dump.go(document.get(), "DOC Content now");
    } catch (MalformedTreeException | BadLocationException x) {
      throw new AssertionError("BUG", x);
    }
    assert undoEdit != null;
  }

  /** creates an ASTRewrite which contains the changes
   * @return an ASTRewrite which contains the changes */
  public final ASTRewrite createRewrite() {
    return createRewrite(new NullProgressMonitor());
  }

  /** creates an ASTRewrite which contains the changes
   * @param pm a progress monitor in which the progress of the refactoring is
   *        displayed
   * @return an ASTRewrite which contains the changes */
  public final ASTRewrite createRewrite(final IProgressMonitor m) {
    return createRewrite(m, (IMarker) null);
  }

  public fluentTrimmerApplication gives(final String expected) {
    if (aboutTheSame(expected, codeFragment) != null) {
      dump.go(this);
      azzert.fail(//
          "no CHNAGE\n" //
              + "I guessed the context of " + guessedContext //
              + "\n and in this context it seems as if your expectation is" ///
              + "\n that'" + codeFragment + "' is spartanized" //
              + "\n   to '" + expected + "', but, both look" //
              + "\n pretty much the same to me. You may want"//
              + "\n change your @Test to <pre>" + //
              "\n\t\t spartiziation.of(" + codeFragment + ").stays()" + "\n </pre>." //
      );
    }
    if (common(codeFragment) != null) {
      dump.go(codeFragment);
      dump.go(compilationUnit);
      dump.go(document);
      azzert.fail(//
          "no CHNAGE\n" //
              + "\n Guessing the context of " + guessedContext //
              + "\n this '" + codeFragment + "' should have converted" //
              + "\n   to '" + expected + "', but it did appears to me"//
              + "\n that it did not change at all. What I got"//
              + "\n   was '" + document.get() + "' which" //
              + "\n which looks to me pretty much the same" //
              + "\n    as '" + codeFragment + "---the original"//
              + "\n snippet.");
    }
    final String difference = common(codeFragment);
    if (difference != null)
      azzert.fail(//
          "something ELSE:\n" //
              + "\n Guessing the context of " + guessedContext //
              + "\n this '" + codeFragment + "' should have converted" //
              + "\n   to '" + expected + "', but for it converted instead" //
              + "\n   to '" + difference + "'!" //
      );
    return new fluentTrimmerApplication(trimmer(), document.get());
  }

  public void stays() {
    final String difference = common(codeFragment);
    if (difference != null)
      azzert.fail(//
          "Should not change" //
              + "\n With guessed context of " //
              + guessedContext //
              + "\n this '" + codeFragment + "' does not stay." //
              + "\n It converts instead to  '" + difference + "'" //
      );
  }

  protected final void fillRewrite(final ASTRewrite r, final IMarker m) {
    compilationUnit.accept(trimmer().new DispatchingVisitor() {
      @Override <N extends ASTNode> boolean go(final N n) {
        if (!trimmer().inRange(m, n))
          return true;
        final Wring<N> w = trimmer().toolbox.find(n);
        if (w != null) {
          final Rewrite make = w.make(n, exclude);
          if (make != null)
            make.go(r, null);
        }
        return true;
      }
    });
  }

  String aboutTheSame(final String s1, final String s2) {
    assert s1 != null;
    assert s2 != null;
    if (s1.equals(s2)) // Highly unlikely, but what the hack
      return null;
    final String g1 = tide.clean(s1);
    assert g1 != null;
    final String g2a = guessedContext.off(s2);
    assert g2a != null;
    final String g2b = tide.clean(g2a);
    assert g2b != null;
    return tide.eq(g1, g2b) || tide.eq(s1, g2b) || tide.eq(g1, g2a) ? g2b : null;
  }

  String common(final String expected) {
    return aboutTheSame(expected, document.get());
  }

  <N extends ASTNode> N findNode(final Class<N> clazz) {
    final GuessedContext guessContext = GuessedContext.find(codeFragment);
    assert guessContext != null;
    final N $ = firstInstance(clazz);
    assert $ != null;
    return $;
  }

  <N extends ASTNode> N firstInstance(final Class<N> clazz) {
    final Wrapper<N> $ = new Wrapper<>();
    compilationUnit.accept(new ASTVisitor() {
      /** The implementation of the visitation procedure in the JDT seems to be
       * buggy. Each time we find a node which is an instance of the sought
       * class, we return false. Hence, we do not anticipate any further calls
       * to this function after the first such node is found. However, this does
       * not seem to be the case. So, in the case our wrapper is not null, we do
       * not carry out any further tests.
       * @param n the node currently being visited.
       * @return <code><b>true</b></code> <i>iff</i> the sought node is
       *         found. */
      @SuppressWarnings("unchecked") @Override public boolean preVisit2(final ASTNode n) {
        if ($.get() != null)
          return false;
        if (!clazz.isAssignableFrom(n.getClass()))
          return true;
        $.set((N) n);
        return false;
      }
    });
    return $.get();
  }

  private ASTRewrite createRewrite(final IProgressMonitor pm, final IMarker m) {
    pm.beginTask("Creating rewrite operation...", 1);
    final ASTRewrite $ = ASTRewrite.create(compilationUnit.getAST());
    fillRewrite($, m);
    pm.done();
    return $;
  }
}
