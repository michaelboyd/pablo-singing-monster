package com.monster.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.monster.domain.Island;
import com.monster.domain.Monster;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.service.PictureService;
import com.monster.ui.FormConstants;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class PictureUploader implements Receiver, SucceededListener, FormConstants {
	
	private File file;
	private PictureService pictureService;
	private PictureRepository pictureRepo;
	private Object entity;
	private Embedded image;
	private Upload upload;	
	private Button deletePicture;
	
	public PictureUploader(Object entity, Embedded image, Upload upload, Button deletePicture,
			PictureRepository pictureRepo, PictureService pictureService) {
		this.entity = entity;
		this.image = image;
		this.upload = upload;
		this.deletePicture = deletePicture;
		this.pictureRepo = pictureRepo;
		this.pictureService = pictureService;
	}
	
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		try {
			file = new File(UPLOAD_FOLDER_IMAGE + filename);
			fos = new FileOutputStream(file);
		} catch (final java.io.FileNotFoundException e) {
			new Notification("Could not open file", e.getMessage(),
					Notification.Type.ERROR_MESSAGE)
					.show(Page.getCurrent());
			return null;
		}
		return fos;
	}

	public void uploadSucceeded(SucceededEvent event) {
		Path path = Paths.get(file.getPath());
		Picture picture = null;
		try {
			byte[] fileData = Files.readAllBytes(path);
            if(fileData != null && fileData.length > 0) {
            	pictureService.savePicture(entity, fileData, event.getFilename());
            }				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(entity instanceof Monster) {
			Monster monster = (Monster) entity;
			picture = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.big);
		}
		else if(entity instanceof Island) {
			Island island = (Island) entity;
			picture = pictureRepo.findByIslandAndImageSize(island, ImageSize.big);
		}		
		
		showOrHidePicture(picture);
	}	
	
    private void showOrHidePicture(Picture picture) {
		if (picture != null) {
			StreamResource.StreamSource imagesource = new ImageSource(picture.getFile());
			image.setVisible(true);
			image.setSource(new StreamResource(imagesource, picture.getFileName()));
			upload.setVisible(false);
			deletePicture.setVisible(true);
		} 
		else {
			image.setVisible(false);
			image.setSource(null);
			upload.setVisible(true);
			deletePicture.setVisible(false);
		}		
    }

	public void setEntity(Object entity) {
		this.entity = entity;
	}

}