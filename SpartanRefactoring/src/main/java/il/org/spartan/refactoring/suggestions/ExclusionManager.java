package il.org.spartan.refactoring.suggestions;

import il.org.spartan.refactoring.utils.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

public class ExclusionManager {
  final Set<ASTNode> inner = new HashSet<>();

  boolean isExcluded(final ASTNode n) {
    for (final ASTNode ancestor : new Ancestors(n))
      if (inner.contains(ancestor))
        return true;
    return false;
  }
  public void exclude(final ASTNode n) {
    inner.add(n);
  }
  void unExclude(final ASTNode n) {
    inner.remove(n);
  }
}