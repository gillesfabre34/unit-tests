/*
 * This file was automatically generated by EvoSuite
 * Fri Dec 18 09:54:09 GMT 2020
 */

package tutorial;

import org.junit.Test;
import static org.junit.Assert.*;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class Cat_ESTest extends Cat_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      Cat cat0 = new Cat();
      cat0.setName("");
      assertEquals(4, cat0.numberOfLegs());
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      Cat cat0 = new Cat();
      int int0 = cat0.numberOfLegs();
      assertEquals(4, int0);
  }
}
