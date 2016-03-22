package com.monster.bean;

public class MonsterBean {
	
    private long id;    
    private String name;
    private String description;
    private String pictureFileName;
    private String songFileName;
    
	public MonsterBean(long id, String name, String description, String pictureFileName, String songFileName) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.pictureFileName = pictureFileName;
		this.songFileName = songFileName;
	}
	
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getPictureFileName() {
		return pictureFileName;
	}
	public String getSongFileName() {
		return songFileName;
	}
}