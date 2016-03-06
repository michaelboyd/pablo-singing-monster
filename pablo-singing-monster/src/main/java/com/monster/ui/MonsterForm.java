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

import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.service.PictureService;
import com.monster.utils.ImageSize;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Select;
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

    private Button save = new Button("Save", this::save);
    private Button cancel = new Button("Cancel", this::cancel);
    private Button delete = new Button("Delete", this::delete);
    private Button deletePicture = new Button("Delete Picture", this::deletePicture);
    private TextField name = new TextField("Name");
    private TextArea description = new TextArea("Description");
	private final Embedded image = new Embedded("Uploaded Picture");
	private ImageUploader receiver = new ImageUploader();	
	private Upload upload = new Upload("Upload Picture", receiver);  
	private ComboBox island = new ComboBox("Islands");

    private Monster monster;
    byte[] fileData;
    
	@Autowired
	private MonsterRepository monsterRepo;    
	
	@Autowired
	public PictureRepository pictureRepo;
	
	@Autowired
	public PictureService pictureService;
	
    private BeanFieldGroup <Monster> formFieldBindings;

    @Autowired
    public MonsterForm(IslandRepository islandRepo) {
        configureComponents();
        initIslandList(islandRepo);
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
        description.setWidth("300px");
    }
    
    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);
        HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);
        HorizontalLayout pictureAction = new HorizontalLayout(deletePicture);
        actions.setSpacing(true);
        pictureAction.setSpacing(true);
		addComponents(name, description, island, upload, image, pictureAction, actions);
    }
    
    private void initIslandList(IslandRepository islandRepo) {
       	island.addItems(islandRepo.findAll());
        island.setNullSelectionAllowed(false);
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
    
    public void deletePicture(Button.ClickEvent event) {
    	List <Picture> pictures = pictureRepo.findByMonster(monster);
    	for(Picture picture : pictures) {
    		pictureRepo.delete(picture);
    	}
    	showOrHidePicture(null);
    }

    void edit(Monster monster) {
        this.monster = monster;
        if(monster != null) {
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(monster, this);
            name.focus();
        }
        delete.setVisible(true);
        setVisible(monster != null);
        Picture picture = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.big);
        if(monster != null) {
        	showOrHidePicture(picture);
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
	            	pictureService.savePicture(monster, fileData, event.getFilename());
	            }				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			picture = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.thumb);
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
			deletePicture.setVisible(true);
		} else {
			image.setVisible(false);
			image.setSource(null);
			upload.setVisible(true);
			deletePicture.setVisible(false);
		}		
    }	
}