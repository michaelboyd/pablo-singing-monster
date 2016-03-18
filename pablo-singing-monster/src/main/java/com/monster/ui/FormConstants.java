package com.monster.ui;

public interface FormConstants {

	static final String HOME_DIRECTORY = System.getProperty("user.home");
	static final String FILE_SEPARATOR = System.getProperty("file.separator");
	static final String UPLOAD_FOLDER = "uploads";
	public static final String CANCELED_NOTIFICATION_LABEL = "Canceled";
	public static final String DELETED_NOTIFICATION_LABEL = "Deleted";
	public static final String SAVED_NOTIFICATION_LABEL = "Saved";
	public static final String UPLOAD_FOLDER_IMAGE = HOME_DIRECTORY + FILE_SEPARATOR + UPLOAD_FOLDER + FILE_SEPARATOR + "image" + FILE_SEPARATOR;
	public static final String UPLOAD_FOLDER_AUDIO = HOME_DIRECTORY + FILE_SEPARATOR + UPLOAD_FOLDER + FILE_SEPARATOR + "audio" + FILE_SEPARATOR;;
	
}
