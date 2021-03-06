package com.monster.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import com.monster.domain.Island;
import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.service.PictureService;
import com.monster.utils.ImageSize;
import com.monster.utils.ImageSource;
import com.monster.utils.PictureUploader;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class IslandForm extends FormLayout implements FormConstants {
	
    private Button save = new Button("Save", this::save);
    private Button cancel = new Button("Cancel", this::cancel);
    private Button delete = new Button("Delete", this::delete);	
    private Button deletePicture = new Button("Delete Picture", this::deletePicture);
	private TextField name = new TextField("Name");
	Table monsterList = new Table("Monsters");
	private final Embedded image = new Embedded("Uploaded Picture");
	
	private PictureUploader receiver;	
	private Upload upload;  
	
	private Island island;
	
    private BeanFieldGroup <Island> formFieldBindings;	
    
    private IslandRepository islandRepo;
    private PictureService pictureService;
    private PictureRepository pictureRepo;
    private MonsterRepository monsterRepo;
    
	@Autowired
	public IslandForm(MonsterRepository monsterRepo,
			PictureRepository pictureRepo, PictureService pictureService,
			IslandRepository islandRepo) {
		this.monsterRepo = monsterRepo;
		this.islandRepo = islandRepo;
		this.pictureRepo = pictureRepo;
		this.pictureService = pictureService;
		configureComponents();
		buildLayout();

	}
    
    private void configureComponents() {
    	setVisible(false);
    	
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER); 
       
        image.setVisible(false);
        image.addClickListener(new com.vaadin.event.MouseEvents.ClickListener() {
		    public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
		        UI.getCurrent().addWindow(new PictureSubwindow(island, pictureRepo));
		    }
		});
        
        upload = new Upload("Upload Picture", null);
        receiver = new PictureUploader(island, image, upload, deletePicture, pictureRepo, pictureService);
        upload.setReceiver(receiver);
        upload.setButtonCaption("Start Upload");
		upload.addSucceededListener(receiver);        
        
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
        HorizontalLayout pictureAction = new HorizontalLayout(monsterList, deletePicture);
        actions.setSpacing(true);
        pictureAction.setSpacing(true);
    	addComponents(name, pictureAction, upload, image, actions);
    }    
	
    void edit(Island island) {
        this.island = island;
        this.receiver.setEntity(island);
        if(island != null) {
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(island, this);
            //name.focus();
        }
        delete.setVisible(true);
        loadMonsterList();
        monsterList.setVisible(monsterList.size() > 0);
        setVisible(island != null);
        if(island != null) {
        	Picture picture = pictureRepo.findByIslandAndImageSize(island, ImageSize.big);
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
		ConfirmDialog.show(getUI(), "Do you really want to Delete the Island: " + island.getName() + " ?", new ConfirmDialog.Listener() {
			public void onClose(ConfirmDialog dialog) {
				if (dialog.isConfirmed()) {
					deleteIslandPictures(island);
			    	islandRepo.delete(island);
			        Notification.show(DELETED_NOTIFICATION_LABEL,Type.TRAY_NOTIFICATION);    	
			        refreshIslandList(); 				
			    }
			}
		});    	
    } 
    
    private void deleteIslandPictures(Island island) {
		List <Picture> pictures = pictureRepo.findByIsland(island);
    	for(Picture picture : pictures) {
    		pictureRepo.delete(picture);
    	}    	
    }
    
    public void deletePicture(Button.ClickEvent event) {
		ConfirmDialog.show(getUI(), "Do you really want to Delete the Picture for Island: " + island.getName() + "?", new ConfirmDialog.Listener() {
			public void onClose(ConfirmDialog dialog) {
				if (dialog.isConfirmed()) {
					deleteIslandPictures(island);
					showOrHidePicture(null);
				}
			}
		});    	
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
        String filterValue = getUI().getIslandFilter().getValue();
		
        if (filterValue == null || "".equals(filterValue)) {
			getUI().listIslands(null);  
		} else {
			getUI().listIslands(filterValue);
		}  
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