/*
 * This file was automatically generated by EvoSuite
 * Wed Dec 16 16:30:16 GMT 2020
 */

package tutorial;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;
import java.util.Iterator;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;
import tutorial.LinkedList;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class LinkedList_ESTest extends LinkedList_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      LinkedList<String> linkedList0 = new LinkedList<String>();
      linkedList0.add("Z");
      // Undeclared exception!
      try { 
        linkedList0.get((-2698));
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // No such element
         //
         verifyException("tutorial.LinkedList", e);
      }
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      LinkedList<String> linkedList0 = new LinkedList<String>();
      linkedList0.add((String) null);
      String string0 = linkedList0.getFirst();
      assertNull(string0);
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      LinkedList<String> linkedList0 = new LinkedList<String>();
      linkedList0.add("Z");
      String string0 = linkedList0.get(0);
      assertEquals("Z", string0);
  }

  @Test(timeout = 4000)
  public void test3()  throws Throwable  {
      LinkedList<Object> linkedList0 = new LinkedList<Object>();
      boolean boolean0 = linkedList0.isEmpty();
      assertTrue(boolean0);
  }

  @Test(timeout = 4000)
  public void test4()  throws Throwable  {
      LinkedList<Integer> linkedList0 = new LinkedList<Integer>();
      linkedList0.add((Integer) null);
      boolean boolean0 = linkedList0.isEmpty();
      assertFalse(boolean0);
  }

  @Test(timeout = 4000)
  public void test5()  throws Throwable  {
      LinkedList<String> linkedList0 = new LinkedList<String>();
      // Undeclared exception!
      try { 
        linkedList0.getFirst();
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // Empty list
         //
         verifyException("tutorial.LinkedList", e);
      }
  }

  @Test(timeout = 4000)
  public void test6()  throws Throwable  {
      LinkedList<String> linkedList0 = new LinkedList<String>();
      linkedList0.add("9");
      String string0 = linkedList0.getFirst();
      assertEquals("9", string0);
  }

  @Test(timeout = 4000)
  public void test7()  throws Throwable  {
      LinkedList<Object> linkedList0 = new LinkedList<Object>();
      Integer integer0 = new Integer(0);
      linkedList0.add(integer0);
      Iterator<Object> iterator0 = linkedList0.iterator();
      linkedList0.add(iterator0);
      linkedList0.add(integer0);
      assertFalse(linkedList0.isEmpty());
  }

  @Test(timeout = 4000)
  public void test8()  throws Throwable  {
      LinkedList<Object> linkedList0 = new LinkedList<Object>();
      Iterator<Object> iterator0 = linkedList0.iterator();
      linkedList0.add(iterator0);
      // Undeclared exception!
      try { 
        linkedList0.get(3501);
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // No such element
         //
         verifyException("tutorial.LinkedList", e);
      }
  }

  @Test(timeout = 4000)
  public void test9()  throws Throwable  {
      LinkedList<Integer> linkedList0 = new LinkedList<Integer>();
      linkedList0.add((Integer) null);
      Integer integer0 = linkedList0.get(0);
      assertNull(integer0);
  }
}
