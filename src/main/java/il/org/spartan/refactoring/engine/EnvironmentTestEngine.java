package il.org.spartan.refactoring.engine;

import java.util.*;
import java.util.Map.*;

import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

import il.org.spartan.*;
import il.org.spartan.refactoring.ast.*;
import il.org.spartan.refactoring.java.*;
import il.org.spartan.refactoring.java.Environment.*;

public class EnvironmentTestEngine {
  static Set<Entry<String, Environment.Information>> generateSet() {
    return Collections.unmodifiableSet(new HashSet<>());
  }

  // s = "il.org.spartan.refactoring.java.EnvironmentCodeExamples.java"
  static CompilationUnit getCompilationUnit(final String from) {
    final IJavaProject javaProject = getJavaProject("spartenRefactoring");
    IType iType;
    CompilationUnit $ = null;
    try {
      iType = javaProject.findType(from);
      final ICompilationUnit iCompilationUnit = iType.getCompilationUnit();
      @SuppressWarnings("deprecation") final ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setSource(iCompilationUnit);
      parser.setResolveBindings(true); // we need bindings later on
      $ = (CompilationUnit) parser.createAST(null);
    } catch (final JavaModelException e) {
      e.printStackTrace();
    }
    return $;
  }

  private static IJavaProject getJavaProject(final String projectName) {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    if (project == null)
      return null;
    final IJavaProject $ = JavaCore.create(project);
    return $ != null && $.exists() ? $ : null;
  }

  static Set<Entry<String, Environment.Information>> listToSet(final List<Annotation> as) {
    final Set<Entry<String, Environment.Information>> $ = Collections.unmodifiableSet(new HashSet<>());
    for (final Annotation ¢ : as) {
    }
    return $;
  }

  private final CompilationUnit cUnit;
  
  Set<Entry<String, Environment.Information>> testNestedENV;
  Set<Entry<String, Environment.Information>> testFlatENV;
  Set<Entry<String, Environment.Information>> testBeginEnd;

  EnvironmentTestEngine(final CompilationUnit $) {
    cUnit = $;
    testNestedENV = generateSet();
    testFlatENV = generateSet();
    testBeginEnd = generateSet();
  }

  EnvironmentTestEngine(final String ¢) {
    cUnit = getCompilationUnit(¢);
    testNestedENV = generateSet();
    testFlatENV = generateSet();
    testBeginEnd = generateSet();
  }

  /*
   * ASTVisitor that goes over the ASTNodes in which annotations can be defined, and checks if the annotations are
   * of the kind that interests us. 
   * An array of @Id annotations is defined inside of each annotation of interest. 
   * I think it will be less error prone and more scalable to implement another, 
   * internal, ASTVisitor that will go over each ID node, 
   * and send everything to an outside function to add to the Sets as required. 
   * That means that each node of interest will be visited twice (or more), but that should not cause worry,
   * since the outside visitor will do nothing.
   * 
   *  TODO: internal node parsing. Think about Nested parsing.
   */
  public void runTest() {
    cUnit.accept(new ASTVisitor() {
      void addAnnotations(final List<Annotation> as) {
        for (final Annotation ¢ : as)
          dispatch(¢);
      }

      void dispatch(final Annotation a) {
        if (a.getTypeName() + "" == "nestedENV")
          nestedHandler(a);
        else if (a.getTypeName() + "" == "flatENV")
          flatHandler(a);
        else if (a.getTypeName() + "" == "BegingEndENV")
          beginEndHandler(a);
      }

      void flatHandler(final Annotation ¢) {
        flatHandler(az.normalAnnotation(¢));
      }

      void flatHandler(NormalAnnotation $) {
        if($ == null)
          return;
        $.accept(new ASTVisitor() {
          @SuppressWarnings("unchecked") List<MemberValuePair> values(NormalAnnotation $) {
            return $.values();
          }
          /*
          @Override public boolean visit(NormalAnnotation ¢){
            if (isNameId(¢.getTypeName()))
              return true;
            return true;
          }

          private boolean isNameId(Name ¢){
            return "Id".equals(wizard.asString(¢));
          }
          */
        });
      }

      private void nestedHandler(final Annotation a) {
      }
      
      private void beginEndHandler(final Annotation a) {
      }

      @Override public boolean visit(final MethodDeclaration d) {
        /* Set<Entry<String, Information>> useCheckSet = Environment.uses(d);
         * Set<Entry<String, Information>> declareCheckSet =
         * Environment.declares(d); */
        addAnnotations(extract.annotations(d));
        return true;
      }
    });
  }
  /**
   * Compares flat output Set (flat) with provided Set, that will be the result of the flat version of defines.
   * @param $
   */
  void compareFlatOutOfOrder(Set<Entry<String, Information>> $){
    //Check that each member of $ is contained in FlatENV, and that the size is equal. 
    //azzert.fail Otherwise.
    return;
  }
  
  /**
   * Compares output Set (testFlatENV) with provided set, that will be the result of the flat version of defines.
   * @param $
   */
  void compareFlatInOrder(Set<Entry<String, Information>> $){
    //Go over both sets in serial manner, and make sure every two members are equal.
    //Also, check size, to avoid the case Set A is contained in B.
    //azzert.fail Otherwise.
  }  
  
  /**
   * Compares output Set (testNestedENV) with provided Set, that will be the result of Nested version of Defines.
   * @param $
   */
  void compareNested(Set<Entry<String, Information>> $){
    //Go over both sets in serial manner, and make sure every two members are equal.
    //Also, check size, to avoid the case Set A is contained in B.
    //azzert.fail Otherwise.
  }
  /**
   * Compares output Set (testBeginEnd) with provided Set, that will be the result of Nested version of uses.
   * @param $
   */
  void compareUses(Set<Entry<String, Information>> $){
    //Go over both sets in serial manner, and make sure every two members are equal.
    //Also, check size, to avoid the case Set A is contained in B.
    //azzert.fail Otherwise.
  }
  
  
  
}
// Don't delete yet:
/* IWorkspace workspace = ResourcesPlugin.getWorkspace(); IWorkspaceRoot root =
 * workspace.getRoot(); // Get all projects in the workspace IProject[] projects
 * = root.getProjects(); */
/* IPackageFragment[] packages = javaProject.getPackageFragments(); for
 * (IPackageFragment mypackage : packages) { if (mypackage.getKind() ==
 * IPackageFragmentRoot.K_SOURCE) { for (ICompilationUnit unit :
 * mypackage.getCompilationUnits()) {
 *
 * } } } */