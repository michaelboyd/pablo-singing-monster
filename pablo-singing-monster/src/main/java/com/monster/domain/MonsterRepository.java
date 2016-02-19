package com.monster.domain;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface MonsterRepository extends PagingAndSortingRepository<Monster, Long> {
	
	List <Monster> findByName(@Param("name") String name);


}
