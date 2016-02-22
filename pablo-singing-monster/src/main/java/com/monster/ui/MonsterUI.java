package com.monster.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Theme("valo")
public class MonsterUI extends UI {
	
	private final Grid monsterList = new Grid();
	private final TextField filter = new TextField();
	private final Button addNewBtn = new Button("New Monster", FontAwesome.PLUS);

	// Show uploaded file in this placeholder
	private final Embedded image = new Embedded("Uploaded Image");
	private ImageUploader receiver = new ImageUploader();	
	// Create the upload with a caption and set receiver later
	private Upload upload = new Upload("Upload Image Here", receiver);
	
	
	//discoverable beans
	private final MonsterRepository repo;
	private final MonsterForm monsterForm;
	
	@Autowired
	public MonsterUI(MonsterRepository repo, MonsterForm monsterForm) {
		this.repo = repo;
		this.monsterForm = monsterForm;
	}

	@Override
	protected void init(VaadinRequest request) {
		configureComponents();
		buildLayout();
	}

	private void configureComponents() {
		
		addNewBtn.addClickListener(e -> monsterForm.add(new Monster("","", null)));

		filter.setInputPrompt("Filter by Name");
		filter.addTextChangeListener(e -> listMonsters(e.getText()));

        monsterList.setContainerDataSource(new BeanItemContainer<>(Monster.class));
        monsterList.setColumnOrder("id", "name", "description");
        monsterList.removeColumn("island");        
        monsterList.setSelectionMode(Grid.SelectionMode.SINGLE);
        
        monsterList.addSelectionListener(e -> monsterForm.edit((Monster) monsterList.getSelectedRow()));
        
        image.setVisible(false);
        
		upload.setButtonCaption("Start Upload");
		upload.addSucceededListener(receiver);        
        
		listMonsters(null);
		
	}
	
	private void buildLayout() {
		
		// Put the components in a panel
		Panel imagePanel = new Panel("Cool Image Storage");
		Layout panelContent = new VerticalLayout();
		panelContent.addComponents(upload, image);
		imagePanel.setContent(panelContent);
		
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        actions.setWidth("100%");
        filter.setWidth("100%");
        actions.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(actions, monsterList, imagePanel);
        left.setSizeFull();
        monsterList.setSizeFull();
        left.setExpandRatio(monsterList, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, monsterForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);

        setContent(mainLayout);	
		
	}
	
	void listMonsters(String text) {
		if (StringUtils.isEmpty(text)) {
			monsterList.setContainerDataSource(
					new BeanItemContainer<Monster>(Monster.class, repo.findAll()));
		}
		else {
			monsterList.setContainerDataSource(new BeanItemContainer<Monster>(Monster.class,
					repo.findByNameStartsWithIgnoreCase(text)));
		}
	}

	TextField getFilter() {
		return filter;
	}
	
	class ImageUploader implements Receiver, SucceededListener {

		public File file;

		public OutputStream receiveUpload(String filename, String mimeType) {
			// Create upload stream
			FileOutputStream fos = null; // Stream to write to
			try {
				// Open the file for writing.
				file = new File("/tmp/uploads/" + filename);
				fos = new FileOutputStream(file);
			} catch (final java.io.FileNotFoundException e) {
				new Notification("Could not open file<br/>", e.getMessage(),
						Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
				return null;
			}
			return fos; // Return the output stream to write to
		}

		public void uploadSucceeded(SucceededEvent event) {
			// Show the uploaded file in the image viewer
			image.setVisible(true);
			image.setSource(new FileResource(file));
		}
	};	
}