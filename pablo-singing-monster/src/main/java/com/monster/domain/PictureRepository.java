package com.monster.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.monster.image.utils.ImageSize;

public interface PictureRepository extends CrudRepository<Picture, Long> {
	
	List <Picture> findByMonster(Monster monster);
	Picture findByMonsterAndImageSize(Monster monster, ImageSize imageSize);

}
