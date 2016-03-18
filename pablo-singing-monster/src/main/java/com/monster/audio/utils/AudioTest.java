package com.monster.audio.utils;

import java.io.File;

import com.monster.ui.FormConstants;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Audio;
import com.vaadin.ui.VerticalLayout;



public class AudioTest extends VerticalLayout implements FormConstants{
	
	public AudioTest() {
		setup();
	}
	
	protected void setup() {
		FileResource s1 = new FileResource(new File("test-files/Driving.mp3"));
        final Audio a = new Audio("audio");
        a.setSources(s1);
        addComponent(a);
	}

}
