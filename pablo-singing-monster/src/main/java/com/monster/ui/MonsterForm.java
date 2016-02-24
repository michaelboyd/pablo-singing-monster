package com.monster.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.image.utils.ImageSize;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class MonsterForm extends FormLayout {

    Button save = new Button("Save", this::save);
    Button cancel = new Button("Cancel", this::cancel);
    Button delete = new Button("Delete", this::delete);
    TextField name = new TextField("Name");
    TextArea description = new TextArea("Description");
    
	// Show uploaded file in this placeholder
	private final Embedded image = new Embedded("Uploaded Picture");
	private ImageUploader receiver = new ImageUploader();	
	// Create the upload with a caption and set receiver later
	private Upload upload = new Upload("Upload Picture", receiver);    

    Monster monster;
    byte[] fileData;
    
	@Autowired
	private MonsterRepository monsterRepo;    
	
	@Autowired
	public PictureRepository pictureRepo;	

    private BeanFieldGroup <Monster> formFieldBindings;

    public MonsterForm() {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        image.setVisible(false);
		upload.setButtonCaption("Start Upload");
		upload.addSucceededListener(receiver);        
        setVisible(false);
    }

    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);
        HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);
        actions.setSpacing(true);
		addComponents(actions, name, description, upload, image);
    }

    public void save(Button.ClickEvent event) {
        try {

        	formFieldBindings.commit();
            monsterRepo.save(monster);

			String msg = String.format("Saved '%s'.", monster.getName());
            Notification.show(msg,Type.TRAY_NOTIFICATION);
            refreshMonsterList();
            name.setValue("");
            description.setValue("");
            setVisible(false);
			image.setVisible(false);
			image.setSource(null);
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        }
    }

    public void cancel(Button.ClickEvent event) {
    	setVisible(false);
    	image.setSource(null);
    	image.setVisible(false);
        Notification.show("Cancelled", Type.TRAY_NOTIFICATION);
        refreshMonsterList();
    }
    
    public void delete(Button.ClickEvent event) {
    	monsterRepo.delete(monster);
        Notification.show("Deleted",Type.TRAY_NOTIFICATION);    	
        refreshMonsterList();  	
    }

    void edit(Monster monster) {
        this.monster = monster;
        if(monster != null) {
            // Bind the properties of the monster POJO to fields in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(monster, this);
            name.focus();
        }
        delete.setVisible(true);
        setVisible(monster != null);

        List <Picture> pictures = pictureRepo.findByMonster(monster);
        Picture picture = null;
        if(!pictures.isEmpty()) {
            picture = pictures.get(0);
        }
        if(monster != null) {
        	showOrHidePicture(picture);
        }
        
    }
    
    private void showOrHidePicture(Picture picture) {
		if (picture != null) {
			StreamResource.StreamSource imagesource = new MyImageSource(picture.getFile());
			image.setVisible(true);
			image.setSource(new StreamResource(imagesource, "myimage.png"));
		} else {
			image.setVisible(false);
			image.setSource(null);
		}		
    }
    
    void add(Monster monster) {
        this.monster = monster;
        if(monster != null) {
            // Bind the properties of the monster POJO to fields in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(monster, this);
            name.focus();
        }
        delete.setVisible(false);
        setVisible(monster != null);
        showOrHidePicture(null);
    }
    
    private void refreshMonsterList() {
        String filterValue = getUI().getFilter().getValue();
		
        if (filterValue == null || "".equals(filterValue)) {
			getUI().listMonsters(null);  
		} else {
			getUI().listMonsters(filterValue);
		}    	
    }

    @Override
    public MonsterUI getUI() {
        return (MonsterUI) super.getUI();
    }

	class ImageUploader implements Receiver, SucceededListener {

		public File file;

		public OutputStream receiveUpload(String filename, String mimeType) {
			FileOutputStream fos = null;
			try {
				file = new File("/tmp/uploads/" + filename);
				fos = new FileOutputStream(file);
			} catch (final java.io.FileNotFoundException e) {
				new Notification("Could not open file", e.getMessage(),
						Notification.Type.ERROR_MESSAGE)
						.show(Page.getCurrent());
				return null;
			}
			return fos; // Return the output stream to write to
		}

		public void uploadSucceeded(SucceededEvent event) {
			//this is the first chance to access the file since being uploaded
			Path path = Paths.get(file.getPath());
			Picture picture = null;
			try {
				byte[] fileData = Files.readAllBytes(path);
	            if(fileData != null && fileData.length > 0) {
	        		picture = new Picture();
	        		picture.setMonster(monster);
	        		picture.setImageSize(ImageSize.big);
	        		picture.setCreateDate(new Date());
	       			picture.setFile(fileData);
	        		pictureRepo.save(picture);            	
	            }				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//now that the image is saved use the picture file bytes to source the image component
			if(picture != null) {
				StreamResource.StreamSource imagesource = new MyImageSource (picture.getFile());
				image.setVisible(true);
				image.setSource(new StreamResource(imagesource, "myimage.png")); //image name is arbitrary as this image is dynamically created
			}
		}
	};	
	
	public class MyImageSource implements StreamResource.StreamSource {
		
		byte file[] = null;
		
		public MyImageSource(byte file[]) {
			this.file = file;
		}

		ByteArrayOutputStream imagebuffer = null;
		int reloads = 0;

		public InputStream getStream() {
			try {
				return new ByteArrayInputStream(file);
			} catch (Exception e) {
				return null;
			}
		}
	}	
}