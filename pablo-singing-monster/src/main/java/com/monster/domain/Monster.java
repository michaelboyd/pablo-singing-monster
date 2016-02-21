package com.monster.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

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
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="island_id")
    private Island island;
    
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<Image>();    
    
    //GIF
    //Song
    
    public Monster(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Monster [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
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

	public Island getIsland() {
		return island;
	}

	public void setIsland(Island island) {
		this.island = island;
	}

}