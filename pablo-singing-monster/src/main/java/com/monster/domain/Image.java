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

    @NotNull
    @ManyToOne
    @JoinColumn(name = "monster_id")
    private Monster monster;
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;	

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] file;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date createDate;

//    @ManyToMany(cascade = CascadeType.ALL)
//    private Set<ImageFile> imageFiles = new HashSet<ImageFile>();

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
}
