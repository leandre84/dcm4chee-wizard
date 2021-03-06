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

package org.dcm4chee.wizard.common.component;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Robert David <robert.david@agfa.com>
 */
public class InternalErrorPage extends MainWebPage {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(InternalErrorPage.class);

    private Throwable throwable;
    private Page page;

    public InternalErrorPage() {
        super();
    }

    public InternalErrorPage(final Throwable throwable, final Page page) {
        super();
        this.throwable = throwable;
        this.page = page;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        if (throwable != null) {
            if (throwable instanceof PageExpiredException)
                log.warn("Page expired", throwable);
            else
                log.error("Critical error", throwable);
        }

        ((MainWebPage) getPage()).getModuleSelectorPanel().setVisible(false);

        if (throwable instanceof PageExpiredException) {
            add(new Label("message-type", new ResourceModel("application.page_expired_error").wrapOnAssignment(this)
                    .getObject() + (this.throwable == null ? "" : throwable.getLocalizedMessage())));
            add(new Label("message-content", new ResourceModel("application.page_expired_error.message")
                    .wrapOnAssignment(this).getObject()).setEscapeModelStrings(false));
        } else {
            add(new Label("message-type", new ResourceModel("application.internal_error").wrapOnAssignment(this)
                    .getObject() + (this.throwable == null ? "" : throwable.getLocalizedMessage())));
            add(new Label("message-content", new ResourceModel("application.internal_error.throwable")
                    .wrapOnAssignment(this).getObject()
                    + (this.throwable == null ? "" : throwable.getLocalizedMessage())));
        }
        add(new Label("originating-page", new ResourceModel("application.internal_error.page").wrapOnAssignment(this)
                .getObject() + (this.page == null ? "" : Page.class.toString())));

        add(new AjaxFallbackLink<Object>("home") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {

                if (throwable != null
                        && (throwable instanceof ModalWindowRuntimeException || throwable instanceof PageExpiredException))
                    ModalWindow.closeCurrent(target);
                else
                    setResponsePage(getApplication().getHomePage());
            }
        }.add(new Label("homeLabel", new ResourceModel("application.home"))));
    }

    @Override
    protected String getBrowserTitle() {
        return super.getBrowserTitle() + ":" + this.getString("application.internal_error", null, "Internal Error");
    }
}
