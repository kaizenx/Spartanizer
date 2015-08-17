package org.spartan.refactoring.spartanizations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.spartan.hamcrest.CoreMatchers.is;
import static org.spartan.hamcrest.MatcherAssert.assertThat;
import static org.spartan.refactoring.utils.Funcs.duplicate;
import static org.spartan.refactoring.utils.Into.e;
import static org.spartan.refactoring.utils.Into.i;
import static org.spartan.refactoring.utils.Restructure.flatten;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.spartan.refactoring.utils.All;
import org.spartan.refactoring.utils.Funcs;
import org.spartan.refactoring.utils.Subject;
import org.spartan.refactoring.wring.ExpressionComparator;
import org.spartan.refactoring.wring.Wrings;

/**
 * Test suite for {@link Wrings}
 *
 * @author Yossi Gil
 * @since 2015-07-17
 */
@SuppressWarnings({ "javadoc", "static-method" }) //
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
public class RefitTest {
  @Test public void refitDoesNotIntroduceList() {
    final List<Expression> operands = All.operands(Funcs.duplicate(i("a*b")));
    assertThat(operands.size(), is(2));
    final InfixExpression e = i("1+2");
    final InfixExpression refit = Subject.operands(operands).to(e.getOperator());
    assertThat(refit.hasExtendedOperands(), is(false));
    assertThat(refit.toString(), is("a + b"));
  }
  @Test public void refitIsCorrect() {
    final InfixExpression e = i("1+2+3");
    final List<Expression> operands = All.operands(Funcs.duplicate(i("a*b*c")));
    assertThat(Subject.operands(operands).to(e.getOperator()).toString(), is("a + b + c"));
  }
  @Test public void refitNotNull() {
    final InfixExpression e = i("1+2+3");
    final List<Expression> operands = All.operands(Funcs.duplicate(i("a+b+c")));
    assertThat(Subject.operands(operands).to(e.getOperator()), notNullValue());
  }
  @Test public void refitPreservesOrder() {
    final InfixExpression e = i("1 + 2 * 3");
    final List<Expression> operands = new ArrayList<>();
    operands.add(duplicate(e("3*4")));
    operands.add(duplicate(e("5")));
    final InfixExpression refit = Subject.operands(operands).to(e.getOperator());
    assertThat(refit, is(not(e)));
    assertThat(refit.toString(), is("3 * 4 + 5"));
  }
  @Test public void refitWithSort() {
    final InfixExpression e = i("1 + 2 * 3");
    final List<Expression> operands = All.operands(flatten(e));
    assertThat(operands.size(), is(2));
    assertThat(operands.get(0).toString(), is("1"));
    assertThat(operands.get(1).toString(), is("2 * 3"));
    assertTrue(Wrings.tryToSort(operands, ExpressionComparator.ADDITION));
    assertThat(operands.get(0).toString(), is("2 * 3"));
    assertThat(operands.get(1).toString(), is("1"));
    final InfixExpression refit = Subject.operands(operands).to(e.getOperator());
    assertThat(refit, is(not(e)));
    assertThat(refit.toString(), is("2 * 3 + 1"));
  }
}
