package com.monster.domain;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.monster.utils.ImageSize;

@Entity
public class Picture { 

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;	
    
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="monster_id", nullable=true)
    private Monster monster;  
    
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="island_id", nullable=true)
    private Island island;    

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @NotNull
    private byte[] file;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date createDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ImageSize imageSize;
    
    @NotNull
    private String fileName;
    
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public Monster getMonster() {
		return monster;
	}
	
	public Island getIsland() {
		return island;
	}

	public void setIsland(Island island) {
		this.island = island;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	public ImageSize getImageSize() {
		return imageSize;
	}

	public void setImageSize(ImageSize imageSize) {
		this.imageSize = imageSize;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Picture [id=");
		builder.append(id);
		builder.append(", monster=");
		builder.append(monster);
		builder.append(", file=");
		builder.append(Arrays.toString(file));
		builder.append(", createDate=");
		builder.append(createDate);
		builder.append(", imageSize=");
		builder.append(imageSize);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}