package com.monster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface IslandRepository extends JpaRepository <Island, Long> {

	Island findByNameOrderByNameAsc(@Param("name") String name);
	
}
