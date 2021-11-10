/*
 * This file was automatically generated by EvoSuite
 * Tue Dec 22 09:59:20 GMT 2020
 */

package tutorial;

import org.junit.Test;
import static org.junit.Assert.*;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;
import tutorial.Address;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class Address_ESTest extends Address_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      Address address0 = new Address();
      address0.town = "=Ro^Dj'ila +m\r6";
      String string0 = address0.getTown();
      assertEquals("=Ro^Dj'ila +m\r6", string0);
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      Address address0 = new Address();
      address0.setTown("");
      String string0 = address0.getTown();
      assertEquals("", string0);
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      Address address0 = new Address();
      String string0 = address0.getTown();
      assertNull(string0);
  }
}