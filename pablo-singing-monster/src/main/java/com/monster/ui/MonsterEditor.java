package com.monster.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.monster.domain.Monster;
import com.monster.domain.MonsterRepository;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * A simple example to introduce building forms. As your real application is
 * probably much more complicated than this example, you could re-use this form in
 * multiple places. This example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Virin
 * (https://vaadin.com/addon/viritin).
 */
@SpringComponent
@UIScope
public class MonsterEditor extends VerticalLayout {

	private final MonsterRepository repository;

	/**
	 * The currently edited customer
	 */
	private Monster monster;

	/* Fields to edit properties in Monster entity */
	TextField name = new TextField("Name");
	TextArea description = new TextArea("Description");

	/* Action buttons */
	Button save = new Button("Save", FontAwesome.SAVE);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", FontAwesome.TRASH_O);
	CssLayout actions = new CssLayout(save, cancel, delete);

	@Autowired
	public MonsterEditor(MonsterRepository repository) {
		this.repository = repository;

		addComponents(name, description, actions);

		// Configure and style components
		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> repository.save(monster));
		delete.addClickListener(e -> repository.delete(monster));
		cancel.addClickListener(e -> editMonster(monster));
		setVisible(false);
	}

	public interface ChangeHandler {

		void onChange();
	}
	
	public final void addMonster() {
		Monster m = new Monster("","");
		delete.setVisible(false);
		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		BeanFieldGroup.bindFieldsUnbuffered(monster, this);
		setVisible(true);
		// A hack to ensure the whole form is visible
		save.focus();
		// Select all text in firstName field automatically
		name.selectAll();		
	}

	public final void editMonster(Monster m) {
		final boolean persisted = m.getId() != 0;
		if (persisted) {
			// Find fresh entity for editing
			monster = repository.findOne(m.getId());
		}
		else {
			monster = m;
		}
		cancel.setVisible(persisted);
		BeanFieldGroup.bindFieldsUnbuffered(monster, this);
		setVisible(true);
		// A hack to ensure the whole form is visible
		save.focus();
		// Select all text in firstName field automatically
		name.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		save.addClickListener(e -> h.onChange());
		delete.addClickListener(e -> h.onChange());
	}

}