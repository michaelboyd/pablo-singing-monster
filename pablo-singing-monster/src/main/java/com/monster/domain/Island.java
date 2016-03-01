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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
public class Island {

	protected Island() {}

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @NotNull    
    private String name;
    
//    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
//    private Set<Monster> monsters = new HashSet<Monster>();    
    
	public Island(String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
//	public Set<Monster> getMonsters() {
//		return monsters;
//	}

	@Override
	public String toString() {
		return name;
	}
	
}