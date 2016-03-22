package com.monster.audio.utils;

import java.io.File;

import com.monster.ui.FormConstants;
import com.vaadin.server.FileResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Audio;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;



public class SongPlayer extends VerticalLayout implements FormConstants{
	
	private Audio song;
	private Button play = new Button("Play Song", this::playSong);
	private Button pause = new Button("Pause Song", this::pauseSong);
	
	
	public SongPlayer(String caption) {
		setCaption(caption);
		setup();
	}
	
	private void setup() {
		song = new Audio();
		
		//song.setComponentError(new UserError("Shit"));
		
		addComponent(song);
//		addComponent(play);
//		addComponent(pause);
		
//		addComponent(new Button("Play audio", new ClickListener() {
//			@Override
//			public void buttonClick(ClickEvent event) {
//				song.play();
//			}
//		}));
//		addComponent(new Button("Pause audio", new ClickListener() {
//			@Override
//			public void buttonClick(ClickEvent event) {
//				song.pause();
//			}
//		}));
		
	}
	
	public void playSong(Button.ClickEvent event) {
		song.play();
	}
	
	public void pauseSong(Button.ClickEvent event) {
		song.pause();
	}	
	
	public void setFileName(String fileName) {
		FileResource s1 = new FileResource(new File(UPLOAD_FOLDER_AUDIO + fileName));
        song.setSources(s1);
	}

}
