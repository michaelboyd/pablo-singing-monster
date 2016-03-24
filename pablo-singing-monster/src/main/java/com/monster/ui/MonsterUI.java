package com.monster.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.monster.domain.Island;
import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.utils.ImageSize;
import com.monster.utils.ImageSource;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Theme("valo")
public class MonsterUI extends UI {
	
	private final Table monsterTable = new Table("Monsters");
	private final Table islandTable = new Table("Islands");
	
	private final TextField monsterFilter = new TextField();
	private final TextField islandFilter = new TextField();
	
	private final Button addNewBtn = new Button("New Monster", FontAwesome.PLUS);
	private final Button addIslandButton = new Button("New Island", FontAwesome.PLUS);
	private final MonsterRepository monsterRepo;
	private final IslandRepository islandRepo;
	private final PictureRepository pictureRepo;
	private final MonsterForm monsterForm;
	private final IslandForm islandForm;
	
	@Autowired
	public MonsterUI(MonsterRepository monsterRepo, MonsterForm monsterForm,
			IslandForm islandForm, IslandRepository islandRepo, PictureRepository pictureRepo) {
		this.monsterRepo = monsterRepo;
		this.monsterForm = monsterForm;
		this.islandForm = islandForm;
		this.islandRepo = islandRepo;
		this.pictureRepo = pictureRepo;
	}

	@Override
	protected void init(VaadinRequest request) {
		configureComponents();
		buildLayout();
	}

	private void configureComponents() {
		addNewBtn.addClickListener(e -> monsterForm.add(new Monster("","", null)));
		addIslandButton.addClickListener(e -> islandForm.add(new Island("")));
		monsterFilter.setInputPrompt("Filter by Monster Name");
		monsterFilter.addTextChangeListener(e -> listMonsters(e.getText()));
		islandFilter.setInputPrompt("Filter by Island Name");
		islandFilter.addTextChangeListener(e -> listIslands(e.getText()));		
		
		//monsterTable.addValueChangeListener(e -> monsterForm.edit((Monster) monsterTable.getValue()));		
		monsterTable.addValueChangeListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	Monster m = (Monster) monsterRepo.findById((Long)monsterTable.getValue());
		    	monsterForm.edit(m);
		    }
		});		
		
		//islandTable.addValueChangeListener(e -> islandForm.edit((Island) islandTable.getValue()));
		islandTable.addValueChangeListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	Island i = (Island) islandRepo.findById((Long)islandTable.getValue());
		    	islandForm.edit(i);
		    }
		});		
        
        setErrorHandler(new DefaultErrorHandler() {
    	    @Override
    	    public void error(com.vaadin.server.ErrorEvent event) {
    	    	
//				for (Throwable t = event.getThrowable(); t != null; t = t.getCause())
//					if (t.getCause() == null) {// We're at final cause
//						//cause += t.getClass().getName() + "<br/>";
//					}
    	        // Do the default error handling (optional)
    	        //doDefault(event);
    	    }
    	});	
        

		listMonsters(null);
        listIslands(null);
	}
	
	private void buildLayout() {
		
		HorizontalLayout actions = new HorizontalLayout(monsterFilter, addNewBtn);
        actions.setWidth("100%");
        monsterFilter.setWidth("100%");
        actions.setExpandRatio(monsterFilter, 1);

        VerticalLayout left = new VerticalLayout(actions, monsterTable);
        left.setSizeFull();
        monsterTable.setSizeFull();
        left.setExpandRatio(monsterTable, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, monsterForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);
        
		HorizontalLayout islandActions = new HorizontalLayout(islandFilter, addIslandButton);
		islandActions.setWidth("100%");
		islandFilter.setWidth("100%");
		islandActions.setExpandRatio(islandFilter, 1);
		
        VerticalLayout islands = new VerticalLayout(islandActions, islandTable);
        islands.setSizeFull();
        islandTable.setSizeFull();
        islands.setExpandRatio(islandTable, 1);

        HorizontalLayout islandLayout = new HorizontalLayout(islands, islandForm);
        islandLayout.setSizeFull();
        islandLayout.setExpandRatio(islands, 1);
        
        TabSheet tabsheet = new TabSheet();        
        tabsheet.addTab(islandLayout).setCaption("Edit Islands");
        tabsheet.addTab(mainLayout).setCaption("Edit Monsters");
        setContent(tabsheet);
		
	}
	
	protected void listMonsters(String text) {
		monsterTable.addContainerProperty("Picture", Embedded.class, null);
		monsterTable.addContainerProperty("Name", Label.class, null);
		monsterTable.addContainerProperty("Description", String.class, null);		
		monsterTable.addContainerProperty("Island", String.class, null);

		List<Monster> monsters = null;
		if (StringUtils.isEmpty(text)) {
			monsters = monsterRepo.findAll(new Sort(Sort.Direction.ASC, "name"));
			addMonstersToMonsterTable(monsters);
		} else {
			monsters = monsterRepo.findByNameStartsWithIgnoreCase(text);
			addMonstersToMonsterTable(monsters);
		}
		monsterTable.setPageLength(10);
		monsterTable.setSelectable(true);
		monsterTable.setImmediate(true);
		monsterTable.setNullSelectionAllowed(true);
	}

	private void addMonstersToMonsterTable(List<Monster> monsters) {
		Label nameField;
		String island = null;
		Embedded image;
		Picture thumbnail;
		for (Monster monster : monsters) {
			nameField = new Label();
			image = new Embedded();
			thumbnail = pictureRepo.findByMonsterAndImageSize(monster, ImageSize.thumb);
			nameField.setValue(monster.getName());
			if(monster.getIsland() != null) 
				island = monster.getIsland().getName();
			if(thumbnail != null) {
				image.setSource(new StreamResource(new ImageSource(thumbnail.getFile()), thumbnail.getFileName()));	
			}
			monsterTable.addItem(new Object[] { image, nameField, monster.getDescription(), island }, monster.getId());
		}
	}
	
	protected void listIslands(String text) {
		islandTable.addContainerProperty("Picture", Embedded.class, null);
		islandTable.addContainerProperty("Name", Label.class, null);		
		
		List <Island> islands = null;
		if (StringUtils.isEmpty(text)) {
			islands = islandRepo.findAll(new Sort(Sort.Direction.ASC, "name"));
			addIslandsToIslandTable(islands);
		} else {
			islands = islandRepo.findByNameStartsWithIgnoreCase(text);
			addIslandsToIslandTable(islands);
		}
		islandTable.setPageLength(10);
		islandTable.setSelectable(true);
		islandTable.setImmediate(true);
		islandTable.setNullSelectionAllowed(true);
	}

	private void addIslandsToIslandTable(List<Island> islands) {
		Label nameField;
		Embedded image;
		Picture thumbnail;
		for (Island island : islands) {
			nameField = new Label();
			image = new Embedded();
			thumbnail = pictureRepo.findByIslandAndImageSize(island, ImageSize.thumb);
			nameField.setValue(island.getName());
			if(thumbnail != null) {
				image.setSource(new StreamResource(new ImageSource(thumbnail.getFile()), thumbnail.getFileName()));	
			}
			islandTable.addItem(new Object[] { image, nameField }, island.getId());
		}
	}
	
	
	protected TextField getMonsterFilter() {
		return monsterFilter;
	}
	
	protected TextField getIslandFilter() {
		return islandFilter;
	}
	
	
}