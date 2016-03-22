package com.monster.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.monster.bean.MonsterBean;
import com.monster.domain.Island;
import com.monster.domain.IslandRepository;
import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
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
	private final MonsterForm monsterForm;
	private final IslandForm islandForm;
	
	@Autowired
	public MonsterUI(MonsterRepository monsterRepo, MonsterForm monsterForm,
			IslandForm islandForm, IslandRepository islandRepo) {
		this.monsterRepo = monsterRepo;
		this.monsterForm = monsterForm;
		this.islandForm = islandForm;
		this.islandRepo = islandRepo;
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
		    	MonsterBean monsterBean = (MonsterBean) monsterTable.getValue();
		    	Monster m = (Monster) monsterRepo.findById(monsterBean.getId());
		    	monsterForm.edit(m);
		    }
		});		
		
		islandTable.addValueChangeListener(e -> islandForm.edit((Island) islandTable.getValue()));		
        
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
		
		monsterTable.addContainerProperty("Name", String.class, null);
		monsterTable.addContainerProperty("Description", String.class, null);		
		
		if (StringUtils.isEmpty(text)) {
			
			List <Monster> monsters = monsterRepo.findAll(new Sort(Sort.Direction.ASC, "name"));

			List <MonsterBean> monsterBeans = new ArrayList <MonsterBean> ();
			
			for(Monster monster : monsters) {
				monsterBeans.add(new MonsterBean(monster.getId(), monster.getName(), monster.getDescription(), null, null));
				// Create the table row.
//				TextField name = new TextField();
//				name.setValue(monster.getName());
//				TextArea description = new TextArea();
//				description.setValue(monster.getDescription());
			    monsterTable.addItem(new Object[] {monster.getName(), monster.getDescription()});
			}
			
			//monsterTable.setContainerDataSource(new BeanItemContainer<MonsterBean>( MonsterBean.class, monsterBeans));
			
			
			
		} else {
			monsterTable.setContainerDataSource(new BeanItemContainer<Monster>(
					Monster.class, monsterRepo.findByNameStartsWithIgnoreCase(text)));
		}
		
		//will need to go from a Monster to a MonsterBean here for the table
		
		//monsterTable.setVisibleColumns(new Object[] { "name", "description", "island" });
		//monsterTable.setColumnHeaders(new String[] { "Name", "Description", "Island" });
		
//		monsterTable.setVisibleColumns(new Object[] { "name", "description" });
//		monsterTable.setColumnHeaders(new String[] { "Name", "Description" });
		
//		monsterTable.addContainerProperty("Name", TextField.class, null);
//		monsterTable.addContainerProperty("Description", TextArea.class, null);
		
		monsterTable.setPageLength(15);
		monsterTable.setSelectable(true);
		monsterTable.setImmediate(true);
		monsterTable.setNullSelectionAllowed(true);
	}

	protected void listIslands(String text) {
		if (StringUtils.isEmpty(text)) {
			islandTable.setContainerDataSource(new BeanItemContainer<Island>(
					Island.class, islandRepo.findAll(new Sort(
							Sort.Direction.ASC, "name"))));
		} else {
			islandTable.setContainerDataSource(new BeanItemContainer<Island>(
					Island.class, islandRepo
							.findByNameStartsWithIgnoreCase(text)));
		}
		islandTable.setVisibleColumns(new Object[] { "name" });
		islandTable.setColumnHeaders(new String[] { "Name" });
		islandTable.setPageLength(10);
		islandTable.setSelectable(true);
		islandTable.setImmediate(true);
		islandTable.setNullSelectionAllowed(true);
	}

	protected TextField getMonsterFilter() {
		return monsterFilter;
	}
	
	protected TextField getIslandFilter() {
		return islandFilter;
	}
	
	
}