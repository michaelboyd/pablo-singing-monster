package com.monster.ui;

import com.monster.domain.Island;
import com.monster.domain.Monster;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.utils.ImageSize;
import com.monster.utils.ImageSource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PictureSubwindow extends Window{

	private String caption;
	private Picture picture;
	
	public PictureSubwindow(Object entity, PictureRepository pictureRepo) {

		if(entity instanceof Monster) {
			Monster monster = (Monster) entity;
			picture = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.fullSize);
			caption = monster.getName();
		}
		else if(entity instanceof Island) {
			Island island = (Island) entity;
			picture = pictureRepo.findByIslandAndImageSize(island, ImageSize.fullSize);
			caption = island.getName();
		}
		
        setCaption(caption);
        center();
        setModal(true);
        setClosable(true);
        
        Embedded image = new Embedded();	  
		StreamResource.StreamSource imagesource = new ImageSource(picture.getFile());
		image.setVisible(true);
		image.setSource(new StreamResource(imagesource, picture.getFileName()));	        
        
        VerticalLayout content = new VerticalLayout();
		content.addComponent(image);
		content.setMargin(true);			
        setContent(content);			

        Button ok = new Button("Close");
        ok.addClickListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        content.addComponent(ok);		
	}
	
}