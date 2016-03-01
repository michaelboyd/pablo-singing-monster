package com.monster.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MonsterRepository extends JpaRepository <Monster, Long> {
	
	List <Monster> findByName(@Param("name") String name);
	List <Monster> findByNameStartsWithIgnoreCase(@Param("name") String name);
	List <Monster> findByIsland(Island island);


}
