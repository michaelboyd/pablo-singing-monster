package com.monster.audio.utils;

import java.io.File;

import com.monster.ui.FormConstants;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Audio;
import com.vaadin.ui.VerticalLayout;



public class SongPlayer extends VerticalLayout implements FormConstants{
	
	private Audio song;
	
	public SongPlayer(String caption) {
		setCaption(caption);
		setup();
	}
	
	private void setup() {
		song = new Audio();
		addComponent(song);
	}
	
	public void setFileName(String fileName) {
		FileResource s1 = new FileResource(new File(UPLOAD_FOLDER_AUDIO + fileName));
        song.setSources(s1);
	}

}
