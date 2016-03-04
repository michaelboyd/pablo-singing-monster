package com.monster.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.monster.domain.Island;
import com.monster.domain.IslandRepository;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.TextField;

@SpringComponent
@UIScope
public class IslandForm extends FormLayout implements FormConstants {
	
    private Button save = new Button("Save", this::save);
    private Button cancel = new Button("Cancel", this::cancel);
    private Button delete = new Button("Delete", this::delete);	
	private TextField name = new TextField("Name");
	
	 private Island island;
	
    private BeanFieldGroup <Island> formFieldBindings;	
    
    @Autowired
    private IslandRepository islandRepo;
    
    public IslandForm() {
    	configureComponents();
    	buildLayout();
    }
    
    private void configureComponents() {
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);    	
    	setVisible(false);
    	name.setWidth("300px");
    } 
    
    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);
        HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);
        actions.setSpacing(true);
    	addComponents(name, actions);
    }    
	
    void edit(Island island) {
        this.island = island;
        if(island != null) {
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(island, this);
            name.focus();
        }
        delete.setVisible(true);
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
    
    private void refreshIslandList() {
		getUI().listIslands();  
    }  
    
    @Override
    public MonsterUI getUI() {
        return (MonsterUI) super.getUI();
    }    

}