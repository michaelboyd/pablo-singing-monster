package com.monster.domain;

import org.springframework.data.repository.CrudRepository;

import com.monster.utils.ImageSize;

public interface SongRepository extends CrudRepository<Song, Long> {
	
	Song findByMonster(Monster monster);

}
