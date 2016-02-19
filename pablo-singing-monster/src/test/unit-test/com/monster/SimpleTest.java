package com.monster;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

	@Test
	public void test() {
		SampleExample example = new SampleExample();
		example.addInteger(10);
		example.addInteger(100);
		Assert.assertEquals(example.getSize(), 2);
	}

	public class SampleExample {

		private List<Integer> integers = null;

		public SampleExample() {
			integers = new ArrayList<Integer>();
		}

		public void addInteger(int num) {
			integers.add(num);
		}

		public int getSize() {
			return integers.size();
		}
	}

}
