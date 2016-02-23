package com.monster.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PictureRepository extends CrudRepository<Picture, Long> {
	
	List <Picture> findByMonster(Monster monster);

}
