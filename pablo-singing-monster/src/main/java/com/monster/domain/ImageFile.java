package com.monster.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
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

import com.monster.image.utils.ImageSize;

@Entity
public class ImageFile {  

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

    @NotNull
    @Enumerated
    private ImageSize imageSize;
    
//    @ManyToOne(fetch=FetchType.LAZY)
//    @JoinColumn(name="image_id")
//    private Image image;

//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Id: ").append(getId()).append(", ");
//        sb.append("Artwork: ").append(getImage().getArtwork().getTitle());
//        return sb.toString();
//    }
}
