package com.monster.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Monster {
	
	protected Monster() {}
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    private String name;
    
    @Lob 
    private String description;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="island_id", nullable=true)
    private Island island;
    
    //GIF
    //Song
    
    public Monster(String name, String description, Island island) {
		this.name = name;
		this.description = description;
		this.island = island;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		//builder.append("Monster [name=");
		builder.append(name);
		//builder.append("]");
		return builder.toString();
	}


	
	
}