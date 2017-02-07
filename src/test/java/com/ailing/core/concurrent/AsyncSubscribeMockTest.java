package com.ailing.core.concurrent;

import javax.annotation.Resource;

import org.junit.Test;

import com.ailing.core.concurrent.mock.AsyncSubscribeMock;

public class AsyncSubscribeMockTest extends BaseTestCase{
	
	@Resource
	private AsyncSubscribeMock asyncSubscribeMock;
	
	@Test
	public void testMock(){
		int ret = 0;
		ret = asyncSubscribeMock.waitOneMinCase();
		System.out.println("***************=> ret="+ret);
		ret = asyncSubscribeMock.withOutCase();
		System.out.println("***************=> ret="+ret);
	}
}
