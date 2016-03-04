package com.monster.ui;

import com.monster.domain.Island;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

@SpringComponent
@UIScope
public class IslandForm extends FormLayout {
	
	private TextField name = new TextField("Name");
	
	private Island island;
	
    private BeanFieldGroup <Island> formFieldBindings;	
	
    void edit(Island island) {
        this.island = island;
        if(island != null) {
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(island, this);
            name.focus();
        }
//        delete.setVisible(true);
        setVisible(island != null);
//        Picture picture = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.big);
//        if(monster != null) {
//        	showOrHidePicture(picture);
//        	island.setValue(monster.getIsland());
//        }
    }

}
