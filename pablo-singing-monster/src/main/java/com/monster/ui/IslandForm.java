package com.monster.ui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.monster.domain.Island;
import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.service.PictureService;
import com.monster.utils.ImageSize;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class IslandForm extends FormLayout implements FormConstants {
	
    private Button save = new Button("Save", this::save);
    private Button cancel = new Button("Cancel", this::cancel);
    private Button delete = new Button("Delete", this::delete);	
	private TextField name = new TextField("Name");
	Table monsterList = new Table("Monsters");
	private final Embedded image = new Embedded("Uploaded Picture");
	private ImageUploader receiver = new ImageUploader();	
	private Upload upload = new Upload("Upload Picture", receiver);  
	
	
	 private Island island;
	
    private BeanFieldGroup <Island> formFieldBindings;	
    
    @Autowired
    private IslandRepository islandRepo;
    
    @Autowired
    private PictureService pictureService;
    
    @Autowired
    private PictureRepository pictureRepo;
    
    private MonsterRepository monsterRepo;
    
    @Autowired
    public IslandForm(MonsterRepository monsterRepo) {
    	this.monsterRepo = monsterRepo;
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

    	name.setWidth("300px");
        name.setRequired(true);
        name.setRequiredError("Name must not be empty");
        name.setImmediate(true);
        name.setValidationVisible(true);
        name.addValidator(new StringLengthValidator("Must not be empty", 1, 100, false));
        
        monsterList.setHeight("400px");
        monsterList.setWidth("300px");        
        
        loadMonsterList();
    } 
    
    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);
        HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);
        actions.setSpacing(true);
    	addComponents(name, monsterList, upload, image, actions);
    }    
	
    void edit(Island island) {
        this.island = island;
        if(island != null) {
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(island, this);
            name.focus();
        }
        delete.setVisible(true);
        loadMonsterList();
        monsterList.setVisible(true);
        setVisible(island != null);
        if(island != null) {
        	Picture picture = pictureRepo.findByIslandAndImageSizeAndIslandNotNull(island, ImageSize.big);
        	showOrHidePicture(picture);        	
        }        
    }
    
    public void save(Button.ClickEvent event) {
        try {
        	formFieldBindings.commit();
            islandRepo.save(island);
			String msg = String.format(SAVED_NOTIFICATION_LABEL + " '%s'.", island.getName());
            Notification.show(msg,Type.TRAY_NOTIFICATION);
            refreshIslandList();
            name.setValue("");
            setVisible(false);
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        }
    }

    public void cancel(Button.ClickEvent event) {
    	setVisible(false);
        Notification.show(CANCELED_NOTIFICATION_LABEL, Type.TRAY_NOTIFICATION);
        refreshIslandList();
    }
    
    public void delete(Button.ClickEvent event) {
    	islandRepo.delete(island);
        Notification.show(DELETED_NOTIFICATION_LABEL,Type.TRAY_NOTIFICATION);    	
        refreshIslandList();  	
    } 
    
    void add(Island island) {
        this.island = island;
        if(island != null) {
            // Bind the properties of the monster POJO to fields in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(island, this);
            name.focus();
        }
        delete.setVisible(false);
        setVisible(island != null);
        monsterList.setContainerDataSource(null);
        monsterList.setVisible(false);
        showOrHidePicture(null);
        upload.setVisible(false);        
    }    
    
    private void refreshIslandList() {
		getUI().listIslands();  
    }
    
	private void loadMonsterList() {
		if(island != null) {
			monsterList.setContainerDataSource(new BeanItemContainer<Monster>(
					Monster.class, monsterRepo.findByIslandOrderByNameAsc(island)));
	        monsterList.setVisibleColumns("name", "description");
		}
	} 
	
    @Override
    public MonsterUI getUI() {
        return (MonsterUI) super.getUI();
    } 
    
	class ImageUploader implements Receiver, SucceededListener {

		private static final long serialVersionUID = 8684994998768778621L;
		public File file;

		public OutputStream receiveUpload(String filename, String mimeType) {
			FileOutputStream fos = null;
			try {
				file = new File("tmp/uploads/" + filename);
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
	            	pictureService.savePicture(island, fileData, event.getFilename());
	            }				
			} catch (IOException e) {
				e.printStackTrace();
			}
			picture = pictureRepo.findByIslandAndImageSizeAndIslandNotNull(island, ImageSize.thumb);
			showOrHidePicture(picture);
		}
	}  
	
	public class MyImageSource implements StreamResource.StreamSource {

		private byte file[] = null;

		public MyImageSource(byte file[]) {
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
	
    private void showOrHidePicture(Picture picture) {
		if (picture != null) {
			StreamResource.StreamSource imagesource = new MyImageSource(picture.getFile());
			image.setVisible(true);
			image.setSource(new StreamResource(imagesource, picture.getFileName()));
			upload.setVisible(false);
			//deletePicture.setVisible(true);
		} else {
			image.setVisible(false);
			image.setSource(null);
			upload.setVisible(true);
			//deletePicture.setVisible(false);
		}		
    }	

}