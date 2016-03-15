package com.monster.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.server.StreamResource;

@SuppressWarnings("serial")
public class ImageSource implements StreamResource.StreamSource{
	
	private byte file[] = null;

	public ImageSource(byte file[]) {
		this.file = file;
	}

	public InputStream getStream() {
		try {
			return new ByteArrayInputStream(file);
		} catch (Exception e) {
			return null;
		}
	}	

}
