package il.org.spartan.spartanizer.wring;

import static il.org.spartan.azzert.*;
import static il.org.spartan.spartanizer.wring.TrimmerTestsUtils.*;

import java.util.*;

import org.junit.*;
import org.junit.runners.*;

import il.org.spartan.*;

/** Unit tests for {@link NameYourClassHere}
 * @author TODO // Write your name here
 * @since 2016 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
@SuppressWarnings({ "static-method", "javadoc" }) //
public class AAA_TemplateTestTemplate {
  /** if fails, suite did not compile... */
  @Test public void seriesZ_00() {
    new Object().hashCode();
  }

  /** if fails, assertions do not work */
  @Test public void seriesZ_01() {
    assert null == null;
  }

  /** if fails, enable assertions (flag '-va') to the JVM */
  @Test(expected = AssertionError.class) //
  public void seriesZ_02() {
    assert null != null;
  }

  /** This is the incorrect way you should check for nulls, using {@link azzert}
   * makes sure we get more informative messages */
  @Test public void seriesZ_03() {
    azzert.isNull($null());
  }

  /** Correct way of checking for nulls. {@link azzert} cannot provide further
   * information if the test fails, since failures give null which carries no
   * information informative messages */
  @Test public void seriesZ_04() {
    assert new Object() != null;
  }

  /** Correct way of checking for nulls. {@link azzert} cannot provide further
   * information if the test fails, since failures give null which carries no
   * information informative messages */
  @Test public void seriesZ_05() {
    assert new Object() != null;
  }

  /** Correct way of checking for true value. {@link azzert} cannot provide
   * further information if the test fails, since failures give nothing but
   * boolean value. */
  @Test public void seriesZ_06() {
    assert $true();
    assert $true() : "Failure in " + object();
  }

  /** Correct way of checking for false value. {@link azzert} cannot provide
   * further information if the test fails, since failures give nothing but
   * boolean value. */
  @Test public void seriesZ_07() {
    assert !$false();
    assert !$false() : "Failure in " + object();
  }

  /** Correct way of checking types */
  @Test public void seriesZ_08() {
    azzert.that(new ArrayList<>(), instanceOf(List.class));
  }

  /** Correct way of checking for inequality of values */
  @Test public void seriesZ_09() {
    azzert.that(object(), not(object()));
  }

  /** Correct way of checking for equality of values */
  @Test public void seriesZ_10() {
    azzert.that(sameSomeObject(), is(sameSomeObject()));
  }

  /** Correct way of checking for equality of numbers */
  @Test public void seriesZ_11() {
    azzert.that($0(), is($0()));
    azzert.that($0(), not(is($1())));
  }

  /** Correct ways of comparing numbers */
  @Test public void seriesZ_12() {
    azzert.that($0(), greaterThanOrEqualTo($0()));
    azzert.that($1(), greaterThanOrEqualTo($0()));
    azzert.that($1(), greaterThan($0()));
    azzert.that($0(), lessThanOrEqualTo($1()));
    azzert.that($0(), lessThanOrEqualTo($0()));
    azzert.that($0(), lessThan($1()));
  }

  /** Correct way of trimming does not change */
  @Test public void seriesZ_13() {
    trimming("a").stays();
  }

  /** Correct way of trimming does not change */
  @Test public void seriesZ_14() {
    trimming("a").stays();
  }

  private int $0() {
    return 0;
  }

  private int $1() {
    return 1;
  }

  private boolean $false() {
    return false;
  }

  private boolean $true() {
    return true;
  }

  private Object $null() {
    return null;
  }

  private static Object object() {
    return new Object();
  }

  private Object sameSomeObject() {
    return this;
  }
}
