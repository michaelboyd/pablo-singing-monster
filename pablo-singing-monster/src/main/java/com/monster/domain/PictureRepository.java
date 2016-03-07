package com.monster.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.monster.utils.ImageSize;

public interface PictureRepository extends CrudRepository<Picture, Long> {
	
	List <Picture> findByMonster(Monster monster);
	
	Picture findByMonsterAndImageSizeAndMonsterNotNull(Monster monster, ImageSize imageSize);
	
	Picture findByIslandAndImageSizeAndIslandNotNull(Island island, ImageSize imageSize);
	
	//List <Picture> findByIslandAndImageSizeAndIslandNotNull(Island island, ImageSize imageSize);

}
