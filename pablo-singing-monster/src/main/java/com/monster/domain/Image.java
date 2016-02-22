package com.monster.domain;

import java.util.Date;

import javax.persistence.Basic;
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

import com.monster.image.utils.ImageType;

@Entity
public class Image { 

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;	
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="image_id", nullable = true)
    private Image picture;    

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Transient
    private byte[] file;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date createDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

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
    
    
    
}