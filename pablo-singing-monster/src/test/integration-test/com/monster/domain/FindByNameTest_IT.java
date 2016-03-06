package com.monster.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.monster.Application;
import com.monster.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class) 
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
					     TransactionalTestExecutionListener.class,
					     DbUnitTestExecutionListener.class})
public class FindByNameTest_IT extends BaseTest{
	
	@Test
	public void findByName_IslandRecordContainsGivenName_ShouldReturnOneMonster() {
        Island searchResults = islandRepo.findByNameOrderByNameAsc("IslandOne");
        assertThat(searchResults != null);
    }	
	
	@Test
    public void findByName_MonsterRecordContainsGivenName_ShouldReturnOneMonster() {
        List <Monster> searchResults = monsterRepo.findByName("MonsterOne");
        assertThat(searchResults).hasSize(1);
    }

}
