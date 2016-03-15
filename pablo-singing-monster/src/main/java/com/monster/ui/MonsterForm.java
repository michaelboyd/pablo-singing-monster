package com.monster.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.vaadin.dialogs.ConfirmDialog;

import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.service.PictureService;
import com.monster.utils.ImageSize;
import com.monster.utils.ImageSource;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class MonsterForm extends FormLayout implements FormConstants{

	//buttons
    private Button save = new Button("Save", this::save);
    private Button cancel = new Button("Cancel", this::cancel);
    private Button delete = new Button("Delete", this::delete);
    private Button deletePicture = new Button("Delete Picture", this::deletePicture);
    
    //input fields
    private TextField name = new TextField("Name");
    private TextArea description = new TextArea("Description");
    private ComboBox island = new ComboBox("Islands"); //must be called island to bind form elements to the persistable object
    
	private Upload upload;
	private Upload audioUpload;
	private final Embedded image = new Embedded("Uploaded Picture");
	private ImageUploader receiver = new ImageUploader();	
	private AudioUploader audioReceiver = new AudioUploader();
	
	
    private Monster monster;
    byte[] fileData;
    
	private MonsterRepository monsterRepo;
	private IslandRepository islandRepo;
	public PictureRepository pictureRepo;
	public PictureService pictureService;
    private BeanFieldGroup <Monster> formFieldBindings;
    
	@Autowired
	public MonsterForm(IslandRepository islandRepo,
			MonsterRepository monsterRepo, PictureRepository pictureRepo,
			PictureService pictureService) {
		this.islandRepo = islandRepo;
		this.monsterRepo = monsterRepo;
		this.pictureRepo = pictureRepo;
		this.pictureService = pictureService;
		configureComponents();
		buildLayout();
	}

    @SuppressWarnings("serial")
	private void configureComponents() {
        
    	save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        
        image.setVisible(false);
		image.addClickListener(new com.vaadin.event.MouseEvents.ClickListener() {
			public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
				UI.getCurrent().addWindow(new MySub());
			}
		});
        
		upload = new Upload("Upload Picture", receiver);
		upload.setButtonCaption("Start Upload");
		upload.addSucceededListener(receiver);    
		
		audioUpload = new Upload("Upload Audio", audioReceiver);
		audioUpload.setButtonCaption("Start Upload");
		audioUpload.addSucceededListener(audioReceiver);
		
		
		setVisible(false);
    	name.setWidth("300px");
        name.setRequired(true);
        name.setRequiredError("Name must not be empty");
        name.setImmediate(true);
        name.setValidationVisible(true);
        name.addValidator(new StringLengthValidator("Must not be empty", 1, 100, false));
        description.setWidth("300px");
        
        initIslandList();
    }
    
    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);
        HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);
        HorizontalLayout pictureAction = new HorizontalLayout(island, deletePicture);
        actions.setSpacing(true);
        pictureAction.setSpacing(true);
        addComponents(name, description, pictureAction, upload, image, audioUpload, actions);
    }
    
    private void initIslandList() {
		island.addItems(islandRepo
				.findAll(new Sort(Sort.Direction.ASC, "name")));
        island.setNullSelectionAllowed(false);
    }   

    public void save(Button.ClickEvent event) {
        try {
        	formFieldBindings.commit();
            monsterRepo.save(monster);
			String msg = String.format(SAVED_NOTIFICATION_LABEL + " '%s'.", monster.getName());
            Notification.show(msg,Type.TRAY_NOTIFICATION);
            refreshMonsterList();            
            name.setValue("");
            description.setValue("");
            setVisible(false);
			image.setVisible(false);
			image.setSource(null);
			getUI().listIslands(null);
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        }
    }

    public void cancel(Button.ClickEvent event) {
    	setVisible(false);
    	image.setSource(null);
    	image.setVisible(false);
        Notification.show(CANCELED_NOTIFICATION_LABEL, Type.TRAY_NOTIFICATION);
        refreshMonsterList();
    }
    
    @SuppressWarnings("serial")
	public void delete(Button.ClickEvent event) {
		ConfirmDialog.show(getUI(), "Delete the Monster?", new ConfirmDialog.Listener() {
			public void onClose(ConfirmDialog dialog) {
				if (dialog.isConfirmed()) {
			    	monsterRepo.delete(monster);
			        Notification.show(DELETED_NOTIFICATION_LABEL, Type.TRAY_NOTIFICATION);    	
			        refreshMonsterList();
				}
			}
		});    	
    }
    
	@SuppressWarnings("serial")
	public void deletePicture(Button.ClickEvent event) {
		ConfirmDialog.show(getUI(), "Delete the Monster's Picture?", new ConfirmDialog.Listener() {
			public void onClose(ConfirmDialog dialog) {
				if (dialog.isConfirmed()) {
					List<Picture> pictures = pictureRepo.findByMonster(monster);
					for (Picture picture : pictures) {
						pictureRepo.delete(picture);
					}
					showOrHidePicture(null);
				}
			}
		});
	}

    void edit(Monster monster) {
        this.monster = monster;
        delete.setVisible(true);
        setVisible(monster != null);
        if(monster != null) {
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(monster, this);
            Picture bigPicture = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.big);
            showOrHidePicture(bigPicture);
        	island.setValue(monster.getIsland());
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
        upload.setVisible(false);
    }
    
    private void refreshMonsterList() {
        String filterValue = getUI().getMonsterFilter().getValue();
		
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

		private static final long serialVersionUID = 8684994998768778621L;
		public File file;

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
	            	pictureService.savePicture(monster, fileData, event.getFilename());
	            }				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			picture = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.big);
			showOrHidePicture(picture);
		}
	}	
	
	class AudioUploader implements Receiver, SucceededListener {

		private static final long serialVersionUID = 8684994998768778621L;
		public File file;

		public OutputStream receiveUpload(String filename, String mimeType) {
			FileOutputStream fos = null;
			try {
				file = new File(UPLOAD_FOLDER_AUDIO + filename);
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
//			Path path = Paths.get(file.getPath());
//			Picture picture = null;
//			try {
//				byte[] fileData = Files.readAllBytes(path);
//	            if(fileData != null && fileData.length > 0) {
//	            	pictureService.savePicture(monster, fileData, event.getFilename());
//	            }				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			picture = pictureRepo.findByMonsterAndImageSizeAndMonsterNotNull(monster, ImageSize.big);
//			showOrHidePicture(picture);
		}
	}
	
	// Define a sub-window by inheritance
	class MySub extends Window {
		
	    public MySub() {
	        super(monster.getName()); // Set window caption
	        center();
	        setModal(true);
	        setClosable(true);
	        
	        Embedded image = new Embedded();	  
	        Picture picture = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.fullSize);
			StreamResource.StreamSource imagesource = new ImageSource(picture.getFile());
			image.setVisible(true);
			image.setSource(new StreamResource(imagesource, picture.getFileName()));	        
	        
	        VerticalLayout content = new VerticalLayout();
			content.addComponent(image);
			content.setMargin(true);			
	        setContent(content);			

	        // Trivial logic for closing the sub-window
	        Button ok = new Button("Close");
	        ok.addClickListener(new com.vaadin.ui.Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	                close(); // Close the sub-window
	            }
	        });
	        content.addComponent(ok);
	    }
	    
	}	
	

	
    private void showOrHidePicture(Picture picture) {
		if (picture != null) {
			StreamResource.StreamSource imagesource = new ImageSource(picture.getFile());
			image.setVisible(true);
			image.setSource(new StreamResource(imagesource, picture.getFileName()));
			upload.setVisible(false);
			deletePicture.setVisible(true);
		} else {
			image.setVisible(false);
			image.setSource(null);
			upload.setVisible(true);
			deletePicture.setVisible(false);
		}		
    }	
}