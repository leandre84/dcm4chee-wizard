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
 * Java(TM), hosted at https://github.com/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2012
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
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

package org.dcm4chee.wizard.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.ResourceReference;
import org.dcm4chee.wizard.common.behavior.TooltipBehavior;
import org.dcm4chee.wizard.icons.ImageManager;
import org.dcm4chee.wizard.icons.behaviour.ImageSizeBehaviour;

/**
 * @author Robert David <robert.david@agfa.com>
 */
public class LinkPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private ModalWindow modalWindow;
    private AjaxLink<?> ajaxLink;

    public LinkPanel(String id, ModalWindow modalWindow) {
        super(id);

        this.modalWindow = modalWindow;

        add((ajaxLink = new AjaxLink<Object>("wickettree.link") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                LinkPanel.this.modalWindow.show(target);
            }
        }).add(new Image("wickettree.link.image", ImageManager.IMAGE_WIZARD_COMMON_EDIT).add(new ImageSizeBehaviour(
                "vertical-align: middle;"))).add(new TooltipBehavior("dicom.edit.")));
        add(new AttributeAppender("style", Model.of("width: 50px; text-align: center;")));
    }

    public LinkPanel(String id, AjaxLink<?> ajaxLink, ResourceReference linkIcon, ModalWindow confirmationWindow) {
        super(id);

        this.modalWindow = confirmationWindow;
        this.ajaxLink = ajaxLink;

        add(ajaxLink);
        ajaxLink.add(
                new Image("wickettree.link.image", linkIcon).add(new ImageSizeBehaviour("vertical-align: middle;")))
                .add(new TooltipBehavior("dicom.edit."));
        add(new AttributeAppender("style", Model.of("width: 50px; text-align: center;")));
    }

    public LinkPanel setImage(ResourceReference linkIcon, StringResourceModel resultMessage) {
        Image image = (Image) ajaxLink.get("wickettree.link.image");
        if (linkIcon != null) {
            image.setImageResourceReference(linkIcon);
            image.add(new ImageSizeBehaviour("vertical-align: middle;"));
        }
        if (resultMessage != null)
            image.add(new AttributeModifier("title", resultMessage));
        return this;
    }
}
