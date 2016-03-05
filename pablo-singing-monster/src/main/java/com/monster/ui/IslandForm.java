package com.monster.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.monster.domain.Island;
import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class IslandForm extends FormLayout implements FormConstants {
	
    private Button save = new Button("Save", this::save);
    private Button cancel = new Button("Cancel", this::cancel);
    private Button delete = new Button("Delete", this::delete);	
	private TextField name = new TextField("Name");
	Table monsterList = new Table("Monsters");
	
	 private Island island;
	
    private BeanFieldGroup <Island> formFieldBindings;	
    
    @Autowired
    private IslandRepository islandRepo;
    
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
    	addComponents(name, monsterList, actions);
    }    
	
    void edit(Island island) {
        this.island = island;
        if(island != null) {
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(island, this);
            name.focus();
        }
        delete.setVisible(true);
        loadMonsterList();
        setVisible(island != null);
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
        //showOrHidePicture(null);
        //upload.setVisible(false);
        clearMonsterList();
    }    
    
    private void refreshIslandList() {
		getUI().listIslands();  
    }
    
	private void loadMonsterList() {
        Object fields[] = {"name"};
        boolean order[] = {true};
		if(island != null) {
			monsterList.setContainerDataSource(new BeanItemContainer<Monster>(
					Monster.class, monsterRepo.findByIsland(island)));
	        monsterList.sort(fields, order);
	        monsterList.setVisibleColumns("name", "description");
		}
	} 
	
	private void clearMonsterList() {
		monsterList.clear();
	}
    
    @Override
    public MonsterUI getUI() {
        return (MonsterUI) super.getUI();
    }    

}