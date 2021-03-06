/*
 * This file was automatically generated by EvoSuite
 * Wed Dec 16 16:32:57 GMT 2020
 */

package tutorial;

import org.junit.Test;
import static org.junit.Assert.*;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;
import tutorial.Node;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class Node_ESTest extends Node_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      Integer integer0 = new Integer(1909);
      Node<Integer> node0 = new Node<Integer>(integer0);
      Node<Integer> node1 = new Node<Integer>(integer0);
      node0.setNext(node1);
      assertNotSame(node0, node1);
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      Node<Object> node0 = new Node<Object>((Object) null);
      Object object0 = node0.getObject();
      assertNull(object0);
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      Integer integer0 = new Integer(1909);
      Node<Integer> node0 = new Node<Integer>(integer0);
      node0.setNext(node0);
      Node<Integer> node1 = node0.getNext();
      assertSame(node1, node0);
  }

  @Test(timeout = 4000)
  public void test3()  throws Throwable  {
      Integer integer0 = new Integer(1909);
      Node<Integer> node0 = new Node<Integer>(integer0);
      Integer integer1 = node0.getObject();
      assertEquals(1909, (int)integer1);
  }

  @Test(timeout = 4000)
  public void test4()  throws Throwable  {
      Integer integer0 = new Integer(1909);
      Node<Integer> node0 = new Node<Integer>(integer0);
      Node<Integer> node1 = node0.getNext();
      assertNull(node1);
  }
}
