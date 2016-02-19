package com.monster.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
    
    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="island")
    private List <Monster> monsters;

	public Island(String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Monster> getMonsters() {
		return monsters;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Island [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", monsters=");
		builder.append(monsters);
		builder.append("]");
		return builder.toString();
	}
}