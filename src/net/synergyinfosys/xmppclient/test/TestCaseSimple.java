package net.synergyinfosys.xmppclient.test;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

public class TestCaseSimple extends TestCase {
	@Override
	public void setUp() {
		System.out.println("Setting up..");
	}

	@Override
	public void tearDown() {
		System.out.println("Tearing down..");
	}
	
	@SmallTest
	public void testmytest(){
		assertTrue("hello".compareTo("hello") == 0);
	}
	

}
