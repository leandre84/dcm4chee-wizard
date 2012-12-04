/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa-Gevaert AG.
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See listed authors below.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4chee.wizard.war.configuration.common.custom;

import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidator;

/**
 * @author Robert David <robert.david@agfa.com>
 */
public class CustomComponentsPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, IModel> models;
	
	public CustomComponentsPanel(List<CustomComponent> customComponents, final Map<String,IModel> models) {
		super("customComponents");
	
		this.models = models;

    	add(new ListView<CustomComponent>("componentList", customComponents) {

			private static final long serialVersionUID = 1L;
    		
			@Override
			protected void populateItem(ListItem<CustomComponent> item) {
				CustomComponent customComponent = 
						(CustomComponent) item.getModelObject();
				
				// dicom.edit.device.title.label
				// dicom.edit.device.title.tooltip
				
				item.add(new Label("component.label", new ResourceModel(customComponent.getName() + ".label")));
				
//				customComponent.setName(customComponent.getType().toString());//getWicketId(customComponent));

if (customComponent.getType().equals(CustomComponent.Type.TextField))
				item.add(new TextFieldFragment(customComponent, models.get(customComponent.getName())));
    	else if (customComponent.getType().equals(CustomComponent.Type.CheckBox))
    		item.add(new CheckBoxFragment(customComponent, models.get(customComponent.getName())));
    	else if (customComponent.getType().equals(CustomComponent.Type.DropDown))
    		item.add(new DropDownFragment(customComponent, models.get(customComponent.getName())));
    	else
    		item.add(new EmptyFragment());
			}
			
    	});
	}
	
	public class EmptyFragment extends Fragment {

		private static final long serialVersionUID = 1L;

		public EmptyFragment() {
            super("component", "emptyFragment", CustomComponentsPanel.this);
        }
    }

	public abstract class FormComponentFragment extends Fragment {

		private static final long serialVersionUID = 1L;

		public FormComponentFragment(String id, String tagName, CustomComponentsPanel panel) {
			super(id, tagName, panel);
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		FormComponent<?> addModifiers(CustomComponent customComponent, FormComponent<?> formComponent) {
			formComponent
				.setRequired(customComponent.getRequired())
				.add(new AttributeModifier("title", new ResourceModel(customComponent.getName() + ".tooltip")))
				.setEnabled(customComponent.getEnabled());
System.out.println("Model is: " + formComponent.getModel().getObject() + " for " + formComponent.getId());

			String prefix = "org.dcm4chee.wizard.war.configuration.simple.validator.";
			String postfix = "Validator";
			if (customComponent.getValidator() != null) {
System.out.println("Trying to add validator: " + 
		prefix + customComponent.getValidator() + postfix);
//				try {
//					formComponent.add((IValidator) 
//							Class.forName(prefix + customComponent.getValidator() + postfix)
//							.newInstance());
//				} catch (Exception e) {
//					throw new RuntimeException("Error instanciating Validator for "
//							 + prefix + customComponent.getValidator() + postfix, e);
//				}
			}
			if (customComponent.getBehaviors() != null)
				for (Behavior behavior : customComponent.getBehaviors())
					formComponent.add(behavior);
			return formComponent;
		}
	}
	
	public class TextFieldFragment extends FormComponentFragment {

		private static final long serialVersionUID = 1L;

		public TextFieldFragment(CustomComponent customComponent, IModel<String> model) {
            super("component", "textFieldFragment", CustomComponentsPanel.this);
            add(addModifiers(customComponent, new TextField<String>("TextField", model)));
        }
    }
	
	public class CheckBoxFragment extends FormComponentFragment {

		private static final long serialVersionUID = 1L;

		public CheckBoxFragment(CustomComponent customComponent, IModel<Boolean> model) {
            super("component", "checkBoxFragment", CustomComponentsPanel.this);
            add(addModifiers(customComponent, new CheckBox("CheckBox", model)));
        }
    }
	
	public class DropDownFragment extends FormComponentFragment {

		private static final long serialVersionUID = 1L;

		public DropDownFragment(CustomComponent customComponent, IModel<String> model) {
            super("component", "dropDownFragment", CustomComponentsPanel.this);
            add(addModifiers(customComponent, 
            		new DropDownChoice<String>("DropDown", model, customComponent.getOptionList())));
        }
    }
}
