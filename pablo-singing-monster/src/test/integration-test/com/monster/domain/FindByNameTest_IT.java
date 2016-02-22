package com.monster.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.aspectj.lang.annotation.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.monster.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class) 
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
					     TransactionalTestExecutionListener.class,
					     DbUnitTestExecutionListener.class})

public class FindByNameTest_IT {
	
	@Autowired
	private MonsterRepository monsterRepo;
	
	@Autowired
	private IslandRepository islandRepo;
	
	@Test
	@DatabaseSetup("classpath:test-island-records.xml")
	@DatabaseSetup("classpath:test-monster-records.xml")	
    public void findByName_IslandRecordContainsGivenName_ShouldReturnOneMonster() {
        List <Island> searchResults = islandRepo.findByName("IslandOne");
        assertThat(searchResults).hasSize(1);
        
        
    }	
	
	@Test
	@DatabaseSetup("classpath:test-island-records.xml")
	@DatabaseSetup("classpath:test-monster-records.xml")	
    public void findByName_MonsterRecordContainsGivenName_ShouldReturnOneMonster() {
        List <Monster> searchResults = monsterRepo.findByName("MonsterOne");
        assertThat(searchResults).hasSize(1);
    }
	
	@org.junit.After
	public void clearRecords() {
        monsterRepo.deleteAll();
        islandRepo.deleteAll();
	}

}
