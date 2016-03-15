package com.monster.ui;

import com.monster.domain.Picture;
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
	
	public PictureSubwindow(Picture picture) {
		this.picture = picture;
		
		if(picture.getMonster() != null) {
			caption = picture.getMonster().getName();
		}
		else if(picture.getIsland() != null) {
			caption = picture.getIsland().getName();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((caption == null) ? 0 : caption.hashCode());
		result = prime * result + ((picture == null) ? 0 : picture.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PictureSubwindow other = (PictureSubwindow) obj;
		if (caption == null) {
			if (other.caption != null)
				return false;
		} else if (!caption.equals(other.caption))
			return false;
		if (picture == null) {
			if (other.picture != null)
				return false;
		} else if (!picture.equals(other.picture))
			return false;
		return true;
	}
	
	
}