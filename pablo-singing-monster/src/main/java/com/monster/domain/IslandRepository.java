package com.monster.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface IslandRepository extends JpaRepository <Island, Long> {

	Island findByNameOrderByNameAsc(@Param("name") String name);
	List <Island> findByNameStartsWithIgnoreCase(@Param("name") String name);
	
}
