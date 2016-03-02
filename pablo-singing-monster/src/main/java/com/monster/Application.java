package com.monster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.monster.domain.Island;
import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;

@SpringBootApplication
public class Application {
	
	@Autowired
	private MonsterRepository monsterRepo;
	
	@Autowired
	private IslandRepository islandRepo;
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public CommandLineRunner loadSampleData() {
		return (args) -> {
			
			//islands
			String islandNames[] = {"Monstro-city", "IslandNumberTwo"};
			
			for(int i=0; i<islandNames.length; i++) {
				if(islandRepo.findByName(islandNames[i]) == null) {
					islandRepo.save(new Island(islandNames[i]));
				}
			}
			
			Island island1 = islandRepo.findByName("Monstro-city");
			
			//monster names
			String monsterNames[] = {"Skele-tone", "Reggae Turtle", "Rock Stomp", "Blubber Slapper", "Vampfire", "Toung-Flicker", "Rain-Bulb", "Run Drummer", "Blunger", "I-Scream", "Balloon-Tune", "Peacemaker"};
			Monster m = null;
			
			for(int i=0; i<monsterNames.length; i++) {
				if(monsterRepo.findByName(monsterNames[i]).isEmpty()) {
					m = new Monster(monsterNames[i], "", island1);
					monsterRepo.save(m);
					//island1.getMonsters().add(m);
				}
			}
			
			//persist the island to update monsters
			islandRepo.save(island1);
			
			log.info("Monsters found with findAll():");
			log.info("-------------------------------");
			for (Monster monster : monsterRepo.findAll()) {
				log.info(monster.toString());
			}
			log.info("-------------------------------");

			log.info("Islands found with findAll():");
			log.info("-------------------------------");
			for (Island island : islandRepo.findAll()) {
				log.info(island.toString());
			}
			log.info("-------------------------------");
			
		};
	}

}
