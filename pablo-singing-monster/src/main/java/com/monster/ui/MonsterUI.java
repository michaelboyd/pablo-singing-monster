package com.monster.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.monster.audio.utils.AudioTest;
import com.monster.domain.Island;
import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Theme("valo")
public class MonsterUI extends UI {
	
	private final Table monsterTable = new Table("Monsters");
	private final Table islandTable = new Table("Islands");
	
	private final TextField filter = new TextField();
	private final Button addNewBtn = new Button("New Monster", FontAwesome.PLUS);
	private final Button addIslandButton = new Button("New Island", FontAwesome.PLUS);
	private final MonsterRepository repo;
	private final IslandRepository islandRepo;
	private final MonsterForm monsterForm;
	private final IslandForm islandForm;
	
	@Autowired
	public MonsterUI(MonsterRepository repo, MonsterForm monsterForm,
			IslandForm islandForm, IslandRepository islandRepo) {
		this.repo = repo;
		this.monsterForm = monsterForm;
		this.islandForm = islandForm;
		this.islandRepo = islandRepo;
	}

	@Override
	protected void init(VaadinRequest request) {
		configureComponents();
		buildLayout();
	}

	@SuppressWarnings("serial")
	private void configureComponents() {
		
		addNewBtn.addClickListener(e -> monsterForm.add(new Monster("","", null)));
		addIslandButton.addClickListener(e -> islandForm.add(new Island("")));
		
		filter.setInputPrompt("Filter by Name");
		filter.addTextChangeListener(e -> listMonsters(e.getText()));

		listMonsters(null);
        listIslands();
	}
	
	private void buildLayout() {
		
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        actions.setWidth("100%");
        filter.setWidth("100%");
        actions.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(actions, monsterTable);
        left.setSizeFull();
        monsterTable.setSizeFull();
        left.setExpandRatio(monsterTable, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, monsterForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);
        
		HorizontalLayout islandActions = new HorizontalLayout(addIslandButton);
		islandActions.setWidth("100%");
		islandActions.setComponentAlignment(addIslandButton, Alignment.MIDDLE_RIGHT);
		
        VerticalLayout islands = new VerticalLayout(islandActions, islandTable);
        islands.setSizeFull();
        islandTable.setSizeFull();
        islands.setExpandRatio(islandTable, 1);

        HorizontalLayout islandLayout = new HorizontalLayout(islands, islandForm);
        islandLayout.setSizeFull();
        islandLayout.setExpandRatio(islands, 1);
        
        AudioTest audioTest = new AudioTest();
        audioTest.setVisible(true);
        
        TabSheet tabsheet = new TabSheet();        
        tabsheet.addTab(islandLayout).setCaption("Edit Islands");
        tabsheet.addTab(mainLayout).setCaption("Edit Monsters");
        tabsheet.addTab(audioTest).setCaption("Audio Test");
        setContent(tabsheet);
		
	}
	
	protected void listMonsters(String text) {
		if (StringUtils.isEmpty(text)) {
			monsterTable.setContainerDataSource(
					new BeanItemContainer<Monster>(Monster.class, repo.findAll(new Sort(Sort.Direction.ASC, "name"))));
		}
		else {
			monsterTable.setContainerDataSource(new BeanItemContainer<Monster>(Monster.class,
					repo.findByNameStartsWithIgnoreCase(text)));
		}
        monsterTable.setVisibleColumns(new Object[] {"name", "description", "island"});
        monsterTable.setColumnHeaders(new String[] { "Name", "Description", "Island" });
        monsterTable.setPageLength(15);
        monsterTable.setSelectable(true);
        monsterTable.setImmediate(true);        
        monsterTable.setNullSelectionAllowed(true);
        monsterTable.addValueChangeListener(e -> monsterForm.edit((Monster) monsterTable.getValue()));		
	}
	
	protected void listIslands() {
		islandTable.setContainerDataSource(new BeanItemContainer<Island>(
				Island.class, islandRepo.findAll(new Sort(Sort.Direction.ASC, "name"))));
      islandTable.setVisibleColumns(new Object[] {"name"});
      islandTable.setColumnHeaders(new String[] { "Name" });
      islandTable.setPageLength(10);
      islandTable.setSelectable(true);
      islandTable.setImmediate(true);        
      islandTable.setNullSelectionAllowed(true);
      islandTable.addValueChangeListener(e -> islandForm.edit((Island) islandTable.getValue()));		
	}

	TextField getFilter() {
		return filter;
	}
	
}