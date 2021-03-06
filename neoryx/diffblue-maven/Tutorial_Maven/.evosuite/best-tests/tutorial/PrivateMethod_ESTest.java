/*
 * This file was automatically generated by EvoSuite
 * Thu Dec 17 09:12:20 GMT 2020
 */

package tutorial;

import org.junit.Test;
import static org.junit.Assert.*;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;
import tutorial.PrivateMethod;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class PrivateMethod_ESTest extends PrivateMethod_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      PrivateMethod privateMethod0 = new PrivateMethod();
      int int0 = privateMethod0.publicMethod(0);
      assertEquals(0, int0);
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      PrivateMethod privateMethod0 = new PrivateMethod();
      int int0 = privateMethod0.publicMethod(2);
      assertEquals(4, int0);
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      PrivateMethod privateMethod0 = new PrivateMethod();
      int int0 = privateMethod0.publicMethod((-39));
      assertEquals((-78), int0);
  }
}
