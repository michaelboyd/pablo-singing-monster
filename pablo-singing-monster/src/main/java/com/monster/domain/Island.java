package com.monster.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Island {

	protected Island() {}

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;	
    private String name;
    
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private Set<Monster> monsters = new HashSet<Monster>();    
    
	public Island(String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public Set<Monster> getMonsters() {
		return monsters;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Island [id=").append(id).append(", name=").append(name).append("]");
		return builder.toString();
	}

	
	
}