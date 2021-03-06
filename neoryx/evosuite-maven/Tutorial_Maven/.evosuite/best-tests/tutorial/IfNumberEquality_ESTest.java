/*
 * This file was automatically generated by EvoSuite
 * Tue Dec 22 09:59:08 GMT 2020
 */

package tutorial;

import org.junit.Test;
import static org.junit.Assert.*;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;
import tutorial.IfNumberEquality;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class IfNumberEquality_ESTest extends IfNumberEquality_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      IfNumberEquality ifNumberEquality0 = new IfNumberEquality();
      double double0 = ifNumberEquality0.ifNumberEquality(1944.7630727);
      assertEquals(1.0, double0, 0.01);
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      IfNumberEquality ifNumberEquality0 = new IfNumberEquality();
      double double0 = ifNumberEquality0.ifNumberEquality(5.0);
      assertEquals(0.0, double0, 0.01);
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      IfNumberEquality ifNumberEquality0 = new IfNumberEquality();
      double double0 = ifNumberEquality0.ifNumberEquality(0.0);
      assertEquals(1.0, double0, 0.01);
  }
}
