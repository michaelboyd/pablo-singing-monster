package com.monster.domain;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.monster.image.utils.ImageSize;
import com.monster.image.utils.ImageType;

@Entity
public class Picture { 

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;	
    
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="monster_id", nullable=true)
    private Monster monster;    

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

    @Transient
    public Long thumbImageId;

    @Transient
    public Long bigImageId;

    @Transient
    public Long fullSizeImageId;

	public Long getThumbImageId() {
		return thumbImageId;
	}

	public Long getBigImageId() {
		return bigImageId;
	}

	public Long getFullSizeImageId() {
		return fullSizeImageId;
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
	
	
	
}