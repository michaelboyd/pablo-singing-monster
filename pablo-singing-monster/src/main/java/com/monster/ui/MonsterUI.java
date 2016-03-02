package com.monster.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Theme("valo")
public class MonsterUI extends UI {
	
	private final Grid monsterList = new Grid();
	private final TextField filter = new TextField();
	private final Button addNewBtn = new Button("New Monster", FontAwesome.PLUS);

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
        
		listMonsters(null);
		
	}
	
	private void buildLayout() {
		
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        actions.setWidth("100%");
        filter.setWidth("100%");
        actions.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(actions, monsterList);
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

}