package com.monster.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.monster.Application;
import com.monster.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
public class MonsterControllerTest_IT extends BaseTest{

	RestTemplate template = new TestRestTemplate();
	
	@Test
	public void findMonsterById() {
		TestRestTemplate restTemplate = new TestRestTemplate(); 
		ResponseEntity <String> entity = restTemplate.getForEntity("http://localhost:8080/rest/monsters/1", String.class); 
		assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void findMonsterByName() {
		TestRestTemplate restTemplate = new TestRestTemplate(); 
		ResponseEntity <String> entity = restTemplate.getForEntity("http://localhost:8080/rest/monsters/search/findByName?name=MonsterOne", String.class); 
		assertEquals(entity.getStatusCode(), HttpStatus.OK);
		assertTrue(entity.getBody().toString().contains("MonsterOne"));
	}
		
}