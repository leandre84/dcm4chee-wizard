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

package org.dcm4chee.wizard.tree;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.dcm4chee.wizard.model.ApplicationEntityModel;
import org.dcm4chee.wizard.model.AuditLoggerModel;
import org.dcm4chee.wizard.model.CoercionModel;
import org.dcm4chee.wizard.model.ConnectionModel;
import org.dcm4chee.wizard.model.DeviceModel;
import org.dcm4chee.wizard.model.TransferCapabilityModel;
import org.dcm4chee.wizard.model.hl7.HL7ApplicationModel;
import org.dcm4chee.wizard.model.proxy.ForwardOptionModel;
import org.dcm4chee.wizard.model.proxy.ForwardRuleModel;
import org.dcm4chee.wizard.model.proxy.RetryModel;
import org.dcm4chee.wizard.tree.ConfigTreeNode.TreeNodeType;

/**
 * @author Robert David <robert.david@agfa.com>
 */
public class CustomTreeColumn extends TreeColumn<ConfigTreeNode, String> {

    private static final long serialVersionUID = 1L;

    Map<TreeNodeType, String> nodeCssClasses;

    private static String folder_connections_cssClass = "folder_connections";
    private static String folder_application_entities_cssClass = "folder_application_entities";
    private static String folder_hl7_applications_cssClass = "folder_hl7_applications";
    private static String folder_audit_loggers_cssClass = "folder_audit_loggers";
    private static String folder_transfer_capabilities_cssClass = "folder_transfer_capabilities";
    private static String folder_transfer_capability_type_cssClass = "folder_transfer_capability_type";
    private static String folder_forward_rules_cssClass = "folder_forward_rules";
    private static String folder_forward_options_cssClass = "folder_forward_options";
    private static String folder_retries_cssClass = "folder_retries";
    private static String folder_coercions_cssClass = "folder_coercions";

    public CustomTreeColumn(IModel<String> displayModel) {
        super(displayModel);

        nodeCssClasses = new HashMap<TreeNodeType, String>();

        nodeCssClasses.put(TreeNodeType.DEVICE, DeviceModel.cssClass);
        nodeCssClasses.put(TreeNodeType.CONNECTION, ConnectionModel.cssClass);
        nodeCssClasses.put(TreeNodeType.HL7_APPLICATION, HL7ApplicationModel.cssClass);
        nodeCssClasses.put(TreeNodeType.APPLICATION_ENTITY, ApplicationEntityModel.cssClass);
        nodeCssClasses.put(TreeNodeType.AUDIT_LOGGER, AuditLoggerModel.cssClass);
        nodeCssClasses.put(TreeNodeType.TRANSFER_CAPABILITY, TransferCapabilityModel.cssClass);
        nodeCssClasses.put(TreeNodeType.FORWARD_RULE, ForwardRuleModel.cssClass);
        nodeCssClasses.put(TreeNodeType.FORWARD_OPTION, ForwardOptionModel.cssClass);
        nodeCssClasses.put(TreeNodeType.RETRY, RetryModel.cssClass);
        nodeCssClasses.put(TreeNodeType.COERCION, CoercionModel.cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_CONNECTIONS, CustomTreeColumn.folder_connections_cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_APPLICATION_ENTITIES,
                CustomTreeColumn.folder_application_entities_cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_HL7_APPLICATIONS, CustomTreeColumn.folder_hl7_applications_cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_AUDIT_LOGGERS, CustomTreeColumn.folder_audit_loggers_cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_TRANSFER_CAPABILITIES,
                CustomTreeColumn.folder_transfer_capabilities_cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_TRANSFER_CAPABILITY_TYPE,
                CustomTreeColumn.folder_transfer_capability_type_cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_FORWARD_RULES, CustomTreeColumn.folder_forward_rules_cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_FORWARD_OPTIONS, CustomTreeColumn.folder_forward_options_cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_RETRIES, CustomTreeColumn.folder_retries_cssClass);
        nodeCssClasses.put(TreeNodeType.CONTAINER_COERCIONS, CustomTreeColumn.folder_coercions_cssClass);
    }

    @Override
    public void populateItem(Item<ICellPopulator<ConfigTreeNode>> cellItem, String componentId,
            IModel<ConfigTreeNode> rowModel) {
        super.populateItem(cellItem, componentId, rowModel);

        cellItem.add(new AttributeAppender("class", Model.of(nodeCssClasses.get(rowModel.getObject().getNodeType())),
                " "));
        if (rowModel.getObject().getModel() != null)
            cellItem.add(new AttributeAppender("title", Model.of(rowModel.getObject().getModel().getDescription())));
    }
}
