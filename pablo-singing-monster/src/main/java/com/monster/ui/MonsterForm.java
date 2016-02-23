package com.monster.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class MonsterForm extends FormLayout {

    Button save = new Button("Save", this::save);
    Button cancel = new Button("Cancel", this::cancel);
    Button delete = new Button("Delete", this::delete);
    TextField name = new TextField("Name");
    TextArea description = new TextArea("Description");

    Monster monster;
    
	@Autowired
	private MonsterRepository monsterRepo;    

    private BeanFieldGroup <Monster> formFieldBindings;

    public MonsterForm() {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        setVisible(false);
    }

    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);
        HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);
        actions.setSpacing(true);
		addComponents(actions, name, description);
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
            
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        }
    }

    public void cancel(Button.ClickEvent event) {
    	setVisible(false);
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
    
    protected Monster getMonster() {
    	return monster;
    }
    
}