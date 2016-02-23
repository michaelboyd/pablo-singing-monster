package com.monster.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.monster.Application;
import com.monster.domain.IslandRepository;
import com.monster.domain.MonsterRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
@DatabaseSetup("classpath:test-island-records.xml")
@DatabaseSetup("classpath:test-monster-records.xml")
public class MonsterControllerTest_IT {

	@Autowired
	private MonsterRepository monsterRepo;
	
	@Autowired
	private IslandRepository islandRepo;	
	
	RestTemplate template = new TestRestTemplate();
	
	@Test
	public void findMonsterById() {
		TestRestTemplate restTemplate = new TestRestTemplate(); 
		ResponseEntity <String> entity = restTemplate.getForEntity("http://localhost:8080/rest/monsters/1", String.class); 
		assertEquals(entity.getStatusCode(), HttpStatus.OK);
		assertTrue(entity.getBody().toString().contains("MonsterOne"));
	}

	@Test
	public void findMonsterByName() {
		TestRestTemplate restTemplate = new TestRestTemplate(); 
		ResponseEntity <String> entity = restTemplate.getForEntity("http://localhost:8080/rest/monsters/search/findByName?name=MonsterOne", String.class); 
		assertEquals(entity.getStatusCode(), HttpStatus.OK);
		assertTrue(entity.getBody().toString().contains("MonsterOne"));
	}
	
	@org.junit.After
	public void clearRecords() {
        monsterRepo.deleteAll();
        islandRepo.deleteAll();
	}		
}