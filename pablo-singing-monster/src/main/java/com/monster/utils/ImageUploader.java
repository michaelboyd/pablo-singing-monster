package com.monster.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;

import com.monster.domain.Picture;
import com.monster.service.PictureService;
import com.monster.ui.FormConstants;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class ImageUploader implements Receiver, SucceededListener, FormConstants {
	
	public File file;
	
	@Autowired
	private PictureService pictureService;
	
	private Object entity;
	
	public ImageUploader(Object entity) {
		this.entity = entity;
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
		
//		picture = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.big);
//		showOrHidePicture(picture);
	}	

}
