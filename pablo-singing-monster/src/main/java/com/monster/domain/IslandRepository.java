package com.monster.domain;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface IslandRepository extends PagingAndSortingRepository<Island, Long> {

	List <Island> findByName(@Param("name") String name);
}
