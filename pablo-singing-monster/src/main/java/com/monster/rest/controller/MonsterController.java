package com.monster.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;

@RestController
public class MonsterController {
	
	@Autowired
	private MonsterRepository repository;

	@RequestMapping("/list-monsters") 
    public List <Monster> getAllMonsters() {
		
		List <Monster> monsters = new ArrayList <Monster> ();
		for(Monster m : repository.findAll()) {
			monsters.add(m);
		}
        return monsters;
    }
	
}
