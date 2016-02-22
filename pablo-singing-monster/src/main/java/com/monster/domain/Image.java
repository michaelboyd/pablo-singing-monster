package com.monster.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

//    @OneToOne(fetch=FetchType.LAZY, mappedBy="picture")
//    private Monster monster;    

//    @Lob
//    @Basic(fetch = FetchType.LAZY)
    @Transient
    private byte[] file;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date createDate;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy="image")
//    private Set<ImageFile> imageFiles = new HashSet<ImageFile>();
    
//    @OneToMany(mappedBy="parent")
//    private Set<ImageFile> imageFiles = new HashSet<ImageFile>();
    
//    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
//  private Set<ImageFile> imageFiles = new HashSet<ImageFile>();   
    
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
