package com.monster.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Monster {
	
	protected Monster() {
	}
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;	
    private String name;
    @Lob 
    private String description;
    
//    @ManyToOne(fetch=FetchType.LAZY)
//    @JoinColumn(name="island_id")
//    private Island island;
     
//    @OneToOne(fetch=FetchType.LAZY)
//    @JoinColumn(name="picture_id")
//    private Image picture;    
    
    //GIF
    //Song
    
    public Monster(String name, String description) {
		this.name = name;
		this.description = description;
	}


	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public Island getIsland() {
//		return island;
//	}
//
//	public void setIsland(Island island) {
//		this.island = island;
//	}

//	public Set<Image> getImages() {
//		return images;
//	}

}