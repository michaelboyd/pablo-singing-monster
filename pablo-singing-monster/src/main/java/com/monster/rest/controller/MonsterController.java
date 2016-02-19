package com.monster.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;

@RestController
public class MonsterController {
	
	@Autowired
	private MonsterRepository repository;
	
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
	//returns JSON and is not mapped to /api like other REST resources mapped by spring-boot-starter-data-rest
	
	@RequestMapping("/list-monsters") 
    public List <Monster> getAllMonsters() {
		
		List <Monster> monsters = new ArrayList <Monster> ();
		for(Monster m : repository.findAll()) {
			monsters.add(m);
		}
        return monsters;
    }
	
}
