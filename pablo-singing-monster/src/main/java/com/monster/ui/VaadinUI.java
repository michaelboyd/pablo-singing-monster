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
public class VaadinUI extends UI {
	
	private final Grid grid;
	private final TextField filter;
	private final Button addNewBtn;

	private MonsterRepository repo;
	private MonsterEditor editor;
	
	@Autowired
	public VaadinUI(MonsterRepository repo, MonsterEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid();
		this.filter = new TextField();
		this.addNewBtn = new Button("New monster", FontAwesome.PLUS);
	}

	@Override
	protected void init(VaadinRequest request) {
		//setContent(new Button("Click me", e->Notification.show("Hello Spring+Vaadin user!")));
		//setContent(grid);
	    //listMonsters();
		
		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
		setContent(mainLayout);

		// Configure layouts and components
		actions.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "name", "description");

		filter.setInputPrompt("Filter by name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.addTextChangeListener(e -> listMonsters(e.getText()));

		// Connect selected Customer to editor or hide if none is selected
		grid.addSelectionListener(e -> {
			if (e.getSelected().isEmpty()) {
				editor.setVisible(false);
			}
			else {
				editor.editMonster((Monster) e.getSelected().iterator().next());
			}
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> editor.editMonster(new Monster("", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listMonsters(filter.getValue());
		});

		// Initialize listing
		listMonsters(null);
		
		
	}
	
	private void listMonsters(String text) {
		if (StringUtils.isEmpty(text)) {
			grid.setContainerDataSource(
					new BeanItemContainer<Monster>(Monster.class, repo.findAll()));
		}
		else {
			grid.setContainerDataSource(new BeanItemContainer<Monster>(Monster.class,
					repo.findByNameStartsWithIgnoreCase(text)));
		}
	}	

}
