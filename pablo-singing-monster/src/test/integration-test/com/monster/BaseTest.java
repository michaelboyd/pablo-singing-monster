package com.monster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.monster.domain.Island;
import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.image.utils.ImageSize;
import com.monster.service.PictureService;

public class BaseTest {
	
	@Autowired
	public MonsterRepository monsterRepo;
	
	@Autowired
	public IslandRepository islandRepo;
	
	@Autowired
	public PictureRepository pictureRepo;
	
	@Autowired
	public PictureService pictureService;
	
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
		Path path = Paths.get("test-image/test.jpg");
		byte[] file = null;
		try {
			file = Files.readAllBytes(path);
			pictureService.saveImage(monster, file, pictureRepo);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@After
	public void destroyRecords() {
		List <Island> islands = islandRepo.findByName("IslandOne");
		Island island = islands.get(0);		
		Set <Monster> monsters = island.getMonsters();
		for(Monster monster : monsters) {
			List <Picture> pictures = pictureRepo.findByMonster(monster);
			for(Picture picture : pictures) {
				pictureRepo.delete(picture);
			}
 		}
		islandRepo.delete(island);
	}
}
