package com.monster.audio.utils;

import java.io.File;

import com.monster.ui.FormConstants;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Audio;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;

public class AudioTest extends VerticalLayout implements FormConstants{
	
	//this is from http://massapi.com/source/github/49/25/49255846/uitest/src/com/vaadin/tests/components/media/AudioTest.java.html#41

	public AudioTest() {
		setup();
	}
	
	protected void setup() {

		// Public domain sounds from pdsounds.org 27.2.2013
		final Resource[] s1 = { new FileResource(new File(UPLOAD_FOLDER_AUDIO + "test.m4a")) };
		//final Resource[] s2 = {	new ClassResource(getClass(), "toyphone_dialling.mp3"),	new ClassResource(getClass(), "toyphone_dialling.ogg") };

		final Audio audio = new Audio();
		audio.setSources(s1);
		audio.setShowControls(true);
		audio.setHtmlContentAllowed(true);
		audio.setAltText("Can't <b>play</b> media");
		audio.setAutoplay(false);

		addComponent(audio);

		CheckBox checkBox = new CheckBox("Show controls", new MethodProperty<Boolean>(audio, "showControls"));
		addComponent(checkBox);
		checkBox = new CheckBox("HtmlContentAllowed", new MethodProperty<Boolean>(audio, "htmlContentAllowed"));
		addComponent(checkBox);
		checkBox = new CheckBox("muted", new MethodProperty<Boolean>(audio, "muted"));
		addComponent(checkBox);
		checkBox = new CheckBox("autoplay", new MethodProperty<Boolean>(audio, "autoplay"));
		addComponent(checkBox);
		
		Button b = new Button("Change", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				audio.setSources(s1);
			}
		});

		addComponent(b);
		setHeight("400px");
		setExpandRatio(b, 1.0f);
	}

}
