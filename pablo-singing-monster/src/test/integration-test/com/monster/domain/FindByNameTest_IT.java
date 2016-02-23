package com.monster.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.monster.Application;
import com.monster.image.utils.ImageSize;

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
	
	@Autowired
	private PictureRepository pictureRepo;
	
	@Before
	public void createRecords() {
		//save the island
		Island island = new Island("IslandOne");
		islandRepo.save(island);
		
		//save the monster with island
		Monster monster = new Monster("MonsterOne", "", island);
		monsterRepo.save(monster);
		
		//save the monster back on the island
		island.getMonsters().add(monster);
		islandRepo.save(island);
		
		//save the picture
		Picture picture = new Picture();
		//set required fields
		picture.setMonster(monster);
		picture.setImageSize(ImageSize.big);
		picture.setCreateDate(new Date());
		Path path = Paths.get("test-image/test.jpg");
		byte[] file = null;
		try {
			file = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		if(file != null) {
			picture.setFile(file);
		}
		pictureRepo.save(picture);

		//having issues here
		//save the picture back on the monster
		//monster.getPictures().add(picture);
		//monsterRepo.save(monster);

	}
	
	@After
	public void destroyRecords() {
		
		pictureRepo.deleteAll();
		
		List <Island> islands = islandRepo.findByName("IslandOne");
		Island island = islands.get(0);
		islandRepo.delete(island);
	}
	
	@Test
	public void findByName_IslandRecordContainsGivenName_ShouldReturnOneMonster() {
        List <Island> searchResults = islandRepo.findByName("IslandOne");
        assertThat(searchResults).hasSize(1);
    }	
	
	@Test
    public void findByName_MonsterRecordContainsGivenName_ShouldReturnOneMonster() {
        List <Monster> searchResults = monsterRepo.findByName("MonsterOne");
        assertThat(searchResults).hasSize(1);
    }

}
