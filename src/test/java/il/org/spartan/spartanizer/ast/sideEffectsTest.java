package il.org.spartan.spartanizer.ast;

import static il.org.spartan.spartanizer.engine.into.*;

import org.junit.*;
import org.junit.runners.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.java.*;

@SuppressWarnings({ "static-method", "javadoc" }) //
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
public class sideEffectsTest {
  @Test public void deterministicArray1() {
    azzert.nay(sideEffects.deterministic(e("new a[3]")));
  }

  @Test public void deterministicArray2() {
    azzert.nay(sideEffects.deterministic(e("new int[] {12,13}")));
  }

  @Test public void deterministicArray3() {
    azzert.nay(sideEffects.deterministic(e("new int[] {12,13, i++}")));
  }

  @Test public void deterministicArray4() {
    azzert.nay(sideEffects.deterministic(e("new int[f()]")));
  }

  @Test public void freeFunctionCall() {
    azzert.nay(sideEffects.free(e("f()")));
  }

  @Test public void freeFunctionCalla() {
    azzert.nay(sideEffects.free(e("i =f()")));
  }

  @Test public void seriesA01() {
    azzert.aye(sideEffects.free(e("a")));
  }

  @Test public void seriesA02() {
    azzert.aye(sideEffects.free(e("this.a")));
  }
}
