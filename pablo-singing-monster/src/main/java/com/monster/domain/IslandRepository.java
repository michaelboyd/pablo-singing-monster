package com.monster.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface IslandRepository extends PagingAndSortingRepository<Island, Long> {

	Island findByName(@Param("name") String name);
	
}
