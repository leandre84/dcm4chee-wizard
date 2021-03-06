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

package org.dcm4chee.wizard.panel;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.PageCreator;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.time.Duration;
import org.dcm4che3.conf.api.ConfigurationException;
import org.dcm4che3.conf.api.DicomConfiguration;
import org.dcm4che3.conf.prefs.PreferencesDicomConfiguration;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.DeviceExtension;
import org.dcm4che3.net.TransferCapability;
import org.dcm4che3.net.audit.AuditLogger;
import org.dcm4che3.net.hl7.HL7Application;
import org.dcm4che3.net.hl7.HL7DeviceExtension;
import org.dcm4chee.proxy.conf.ProxyAEExtension;
import org.dcm4chee.storage.conf.StorageConfiguration;
import org.dcm4chee.wizard.WizardApplication;
import org.dcm4chee.wizard.common.behavior.TooltipBehavior;
import org.dcm4chee.wizard.common.component.ExtendedForm;
import org.dcm4chee.wizard.common.component.ModalWindowRuntimeException;
import org.dcm4chee.wizard.common.component.secure.ConfirmationWindow;
import org.dcm4chee.wizard.common.component.secure.MessageWindow;
import org.dcm4chee.wizard.edit.*;
import org.dcm4chee.wizard.edit.proxy.CreateOrEditForwardOptionPage;
import org.dcm4chee.wizard.edit.proxy.CreateOrEditForwardRulePage;
import org.dcm4chee.wizard.edit.proxy.CreateOrEditRetryPage;
import org.dcm4chee.wizard.edit.xds.*;
import org.dcm4chee.wizard.icons.ImageManager;
import org.dcm4chee.wizard.icons.behaviour.ImageSizeBehaviour;
import org.dcm4chee.wizard.model.*;
import org.dcm4chee.wizard.model.hl7.HL7ApplicationModel;
import org.dcm4chee.wizard.model.proxy.ForwardOptionModel;
import org.dcm4chee.wizard.model.proxy.ForwardRuleModel;
import org.dcm4chee.wizard.model.proxy.RetryModel;
import org.dcm4chee.wizard.model.xds.*;
import org.dcm4chee.wizard.page.ApplyTransferCapabilityProfilePage;
import org.dcm4chee.wizard.page.AutoDetectTransferCapabilities;
import org.dcm4chee.wizard.page.DicomEchoPage;
import org.dcm4chee.wizard.tree.ConfigTableTree;
import org.dcm4chee.wizard.tree.ConfigTreeNode;
import org.dcm4chee.wizard.tree.ConfigTreeNode.TreeNodeType;
import org.dcm4chee.wizard.tree.ConfigTreeProvider;
import org.dcm4chee.wizard.tree.ConfigTreeProvider.ConfigurationType;
import org.dcm4chee.wizard.tree.CustomTreeColumn;
import org.dcm4chee.xds2.common.deactivatable.Deactivateable;
import org.dcm4chee.xds2.conf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

/**
 * @author Robert David <robert.david@agfa.com>
 * @author Michael Backhaus <michael.backhaus@agfa.com>
 */
public class BasicConfigurationPanel extends DicomConfigurationPanel {

    private static final long serialVersionUID = 1L;

    private static final String MODULE_NAME = "dicom";

    private static Logger log = LoggerFactory.getLogger(BasicConfigurationPanel.class);

    private ExtendedForm form;

    private ModalWindow editWindow;
    private ModalWindow echoWindow;
    private ConfirmationWindow<ConfigTreeNode> removeConfirmation;
    private MessageWindow refreshMessage;
    private MessageWindow reloadMessage;
    public WindowClosedCallback windowClosedCallback;

    List<IColumn<ConfigTreeNode, String>> deviceColumns = new ArrayList<IColumn<ConfigTreeNode, String>>();
    TableTree<ConfigTreeNode, String> configTree;

    public static final Map<TreeNodeType, Class<? extends DeviceExtension>> XDS_EXTENSIONS = new HashMap<>();
    public static final Map<TreeNodeType, String> XDS_REST_PATH = new HashMap<>();

    static {

        XDS_EXTENSIONS.put(TreeNodeType.XDSRegistry, XdsRegistry.class);
        XDS_EXTENSIONS.put(TreeNodeType.XDSRepository, XdsRepository.class);
        XDS_EXTENSIONS.put(TreeNodeType.XCAiInitiatingGateway, XCAiInitiatingGWCfg.class);
        XDS_EXTENSIONS.put(TreeNodeType.XCAiRespondingGateway, XCAiRespondingGWCfg.class);
        XDS_EXTENSIONS.put(TreeNodeType.XCAInitiatingGateway, XCAInitiatingGWCfg.class);
        XDS_EXTENSIONS.put(TreeNodeType.XCARespondingGateway, XCARespondingGWCfg.class);
        XDS_EXTENSIONS.put(TreeNodeType.XDSiSource, XDSiSourceCfg.class);

        XDS_REST_PATH.put(TreeNodeType.XDSRegistry, "xds-reg-rs/ctrl/");
        XDS_REST_PATH.put(TreeNodeType.XDSRepository, "xds-rep-rs/ctrl/");
        XDS_REST_PATH.put(TreeNodeType.XCAiInitiatingGateway, "xcai-rs/ctrl/");
        XDS_REST_PATH.put(TreeNodeType.XCAiRespondingGateway, "xcai-rs/ctrl/");
        XDS_REST_PATH.put(TreeNodeType.XCAInitiatingGateway, "xca-rs/ctrl/");
        XDS_REST_PATH.put(TreeNodeType.XCARespondingGateway, "xca-rs/ctrl/");
        XDS_REST_PATH.put(TreeNodeType.XDSiSource, "imgsrc-rs/ctrl/");

    }

    public BasicConfigurationPanel(final String id) {
        super(id);
        add(new Label("reload-message"));
        createChangeTimer();
        setWindowClosedCallback();
        addEchoWindow();
        addEditWindow();
        addRemoveConfirmation();
        addRefreshMessage();
        addForm();
        addCreateDevice();
        addExport();
        addDeviceColumns();
    }

    private void addDeviceColumns() {
        try {
            deviceColumns.add(new CustomTreeColumn(Model.of("Devices")));
            deviceColumns.add(getConfigurationTypeColumn());
            deviceColumns.add(getProtocolColumn());
            deviceColumns.add(getConnectionsColumn());
            deviceColumns.add(getStatusColumn());
            deviceColumns.add(getSendColumn());
            deviceColumns.add(getEmptyModelColumn());
            if (System.getProperty("org.dcm4chee.wizard.config.aeTitle") != null)
                deviceColumns.add(getEchoColum());
            deviceColumns.add(getEditColumn());
            deviceColumns.add(getProfileColumn());
            deviceColumns.add(getDeleteColumn());
            ConfigTreeProvider initCTP = ConfigTreeProvider.init(BasicConfigurationPanel.this);
            configTree = new ConfigTableTree("configTree", deviceColumns, initCTP, Integer.MAX_VALUE);
            renderTree();
        } catch (ConfigurationException ce) {
            log.error(this.getClass().toString() + ": " + "Error creating tree: " + ce.getMessage());
            if (log.isDebugEnabled())
                ce.printStackTrace();
            throw new RuntimeException(ce);
        }
    }

    private void addExport() {
        final Link<Object> export = getExportLink();
        export.add(new Image("exportImg", ImageManager.IMAGE_WIZARD_EXPORT).add(new ImageSizeBehaviour(
                "vertical-align: middle;")));
        export.add(new TooltipBehavior("dicom."));
        export.add(new Label("exportText", new ResourceModel("dicom.export.text")).add(new AttributeAppender("style",
                Model.of("vertical-align: middle"), " ")));
        form.add(export);

        if (!(((WizardApplication) getApplication()).getDicomConfigurationManager().getDicomConfiguration() instanceof PreferencesDicomConfiguration))
            export.setVisible(false);
    }

    private Link<Object> getExportLink() {
        return new Link<Object>("export") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {

                RequestCycle.get().replaceAllRequestHandlers(new IRequestHandler() {

                    @Override
                    public void detach(IRequestCycle requestCycle) {
                    }

                    @Override
                    public void respond(IRequestCycle requestCycle) {

                        OutputStream out = null;
                        try {
                            WebResponse response = (WebResponse) getRequestCycle().getResponse();
                            response.setContentType("application/zip");
                            response.setAttachmentHeader("configuration.zip");
                            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
                            out = zos;
                            ZipEntry entry = new ZipEntry("configuration.xml");
                            zos.putNextEntry(entry);
                            DicomConfiguration dicomConfiguration = ((WizardApplication) getApplication())
                                    .getDicomConfigurationManager().getDicomConfiguration();
                            ((PreferencesDicomConfiguration) dicomConfiguration).getDicomConfigurationRoot()
                                    .exportSubtree(zos);
                            zos.flush();
                            zos.closeEntry();
                        } catch (ZipException ze) {
                            log.warn("Problem creating zip file: " + ze);
                        } catch (ClientAbortException cae) {
                            log.warn("Client aborted zip file download: " + cae);
                        } catch (Exception e) {
                            log.error("An error occurred while attempting to stream zip file for download: ", e);
                        } finally {
                            try {
                                if (out != null)
                                    out.close();
                            } catch (Exception ignore) {
                            }
                        }
                    }
                });
            }
        };
    }

    private void addCreateDevice() {
        AjaxLink<Object> createDevice = getCreateDeviceLink();
        Component createDeviceImg = new Image("createDeviceImg", ImageManager.IMAGE_WIZARD_DEVICE_ADD);
        createDeviceImg.add(new ImageSizeBehaviour("vertical-align: middle;"));
        createDevice.add(createDeviceImg);
        createDevice.add(new TooltipBehavior("dicom."));
        Component createDeviceText = new Label("createDeviceText", new ResourceModel("dicom.createDevice.text"));
        createDeviceText.add(new AttributeAppender("style", Model.of("vertical-align: middle"), " "));
        createDevice.add(createDeviceText);
        form.add(createDevice);
    }

    private AjaxLink<Object> getCreateDeviceLink() {
        return new AjaxLink<Object>("createDevice") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                editWindow.setTitle("").setPageCreator(new ModalWindow.PageCreator() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Page createPage() {
                        return new CreateOrEditDevicePage(editWindow, null);
                    }
                }).show(target);
            }
        };
    }

    private void addForm() {
        add(form = new ExtendedForm("form"));
        form.setResourceIdPrefix("dicom.");
    }

    private void addRefreshMessage() {
        refreshMessage = getRefreshMessageWindow();
        refreshMessage.setInitialHeight(150);
        refreshMessage.setWindowClosedCallback(windowClosedCallback);
        add(refreshMessage);
    }

    private MessageWindow getRefreshMessageWindow() {
        return new MessageWindow("refresh-message",
                new StringResourceModel("dicom.confirmRefresh", this, null)
                        .wrapOnAssignment(BasicConfigurationPanel.this)) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onOk(AjaxRequestTarget target) {
                try {
                    log.warn("Reloading device list from configuration");
                    ((WizardApplication) getApplication()).getDicomConfigurationManager().getDicomConfiguration().sync();
                    ConfigTreeProvider.get().loadDeviceList();
                    for (Iterator<ConfigTreeNode> i = configTree.getModel().getObject().iterator(); i.hasNext(); ) {
                        ConfigTreeNode root = i.next().getRoot();
                        for (ConfigTreeNode deviceNode : ConfigTreeProvider.get().getNodeList())
                            if (deviceNode.equals(root) && (((DeviceModel) deviceNode.getModel()).getDevice() == null))
                                ConfigTreeProvider.get().loadDevice(deviceNode);
                    }
                    renderTree();
                } catch (ConfigurationException ce) {
                    log.error("Error reloading configuration after concurrent modification", ce);
                }
            }
        };
    }

    private void addRemoveConfirmation() {
        removeConfirmation = getNewRemoveConfirmation();
        removeConfirmation.setInitialHeight(200);
        removeConfirmation.setWindowClosedCallback(windowClosedCallback);
        add(removeConfirmation);
    }

    private ConfirmationWindow<ConfigTreeNode> getNewRemoveConfirmation() {
        return new ConfirmationWindow<ConfigTreeNode>("remove-confirmation") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onConfirmation(AjaxRequestTarget target, ConfigTreeNode node) {
                try {
                    if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.DEVICE)) {
                        ConfigTreeProvider.get().removeDevice(node);
                    } else {
                        ConfigTreeNode deviceNode = node.getRoot();
                        Device device = ((DeviceModel) deviceNode.getModel()).getDevice();
                        if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.CONNECTION)) {
                            device.removeConnection(((ConnectionModel) node.getModel()).getConnection());

                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.APPLICATION_ENTITY)) {
                            ApplicationEntity applicationEntity = ((ApplicationEntityModel) node.getModel())
                                    .getApplicationEntity();
                            device.removeApplicationEntity(applicationEntity);
                            ConfigTreeProvider.get().unregisterAETitle(applicationEntity.getAETitle());

                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.HL7_APPLICATION)) {
                            HL7Application hl7Application = ((HL7ApplicationModel) node.getModel()).getHL7Application();
                            device.getDeviceExtension(HL7DeviceExtension.class).removeHL7Application(hl7Application);
                            ConfigTreeProvider.get().unregisterHL7Application(hl7Application.getApplicationName());

                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.AUDIT_LOGGER)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(AuditLogger.class));

                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.TRANSFER_CAPABILITY)) {
                            TransferCapability transferCapability = ((TransferCapabilityModel) node.getModel())
                                    .getTransferCapability();
                            ((ApplicationEntityModel) node.getAncestor(3).getModel()).getApplicationEntity()
                                    .removeTransferCapabilityFor(transferCapability.getSopClass(),
                                            transferCapability.getRole());

                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.FORWARD_RULE)) {
                            ((ApplicationEntityModel) node.getAncestor(2).getModel()).getApplicationEntity()
                                    .getAEExtension(ProxyAEExtension.class).getForwardRules()
                                    .remove(((ForwardRuleModel) node.getModel()).getForwardRule());

                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.FORWARD_OPTION)) {
                            ((ApplicationEntityModel) node.getAncestor(2).getModel()).getApplicationEntity()
                                    .getAEExtension(ProxyAEExtension.class).getForwardOptions()
                                    .remove(((ForwardOptionModel) node.getModel()).getDestinationAETitle());

                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.RETRY)) {
                            ((ApplicationEntityModel) node.getAncestor(2).getModel()).getApplicationEntity()
                                    .getAEExtension(ProxyAEExtension.class).getRetries()
                                    .remove(((RetryModel) node.getModel()).getRetry());

                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.COERCION)) {
                            ((ApplicationEntityModel) node.getAncestor(2).getModel()).getApplicationEntity()
                                    .getAEExtension(ProxyAEExtension.class).getAttributeCoercions()
                                    .remove(((CoercionModel) node.getModel()).getCoercion());
                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.XCAiInitiatingGateway)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(XCAiInitiatingGWCfg.class));
                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.XCAInitiatingGateway)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(
                                            XCAInitiatingGWCfg.class));
                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.XCAiRespondingGateway)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(XCAiRespondingGWCfg.class));
                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.XCARespondingGateway)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(XCARespondingGWCfg.class));
                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.XDSRegistry)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(XdsRegistry.class));
                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.XDSSource)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(XdsSource.class));
                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.XDSiSource)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(XDSiSourceCfg.class));
                        } else if (node.getNodeType().equals(TreeNodeType.XDSStorage)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(StorageConfiguration.class));
                        } else if (node.getNodeType().equals(ConfigTreeNode.TreeNodeType.XDSRepository)) {
                            device.removeDeviceExtension(
                                    device.getDeviceExtension(XdsRepository.class));
                        } else {
                            log.error("Missing type of ConfigurationTreeNode");
                            return;
                        }
                        ConfigTreeProvider.get().mergeDevice(device);
                    }
                } catch (Exception e) {
                    log.error(this.getClass().toString() + ": " + "Error deleting configuration object: "
                            + e.getMessage());
                    log.debug("Exception", e);
                    throw new ModalWindowRuntimeException(e.getLocalizedMessage());
                }
            }
        };
    }

    private void addEditWindow() {
        editWindow = new ModalWindow("edit-window");
        editWindow.setInitialWidth(700).setInitialHeight(500);
        editWindow.setWindowClosedCallback(windowClosedCallback);
        add(editWindow);
    }

    private void addEchoWindow() {
        echoWindow = new ModalWindow("echo-window");
        echoWindow.setInitialWidth(700).setInitialHeight(500);
        echoWindow.setWindowClosedCallback(windowClosedCallback);
        add(echoWindow);
    }

    private void setWindowClosedCallback() {
        windowClosedCallback = getWindowClosedCallback();
    }

    private WindowClosedCallback getWindowClosedCallback() {
        return new ModalWindow.WindowClosedCallback() {

            private static final long serialVersionUID = 1L;

            public void onClose(AjaxRequestTarget target) {
                try {
                    boolean refresh = false;
                    for (ConfigTreeNode deviceNode : ConfigTreeProvider.get().getNodeList())
                        if (deviceNode.getModel() == null) {
                            ConfigTreeProvider.get().loadDevice(deviceNode);
                            refresh = true;
                        }
                    if (refresh || ConfigTreeProvider.get().resync())
                        renderTree();
                } catch (ConfigurationException ce) {
                    log.error(this.getClass().toString() + ": " + "Error refreshing tree: " + ce.getMessage());
                    log.debug("Exception", ce);
                    throw new RuntimeException(ce);
                }
                target.add(form);
            }
        };
    }

    private void createChangeTimer() {
        try {
            String checkForChangesInterval = ((WebApplication) this.getApplication())
                    .getInitParameter("CheckForChangesInterval");
            if (checkForChangesInterval != null && !checkForChangesInterval.equals(""))
                add(getChangeIntervalTimerBehaviour(checkForChangesInterval));
        } catch (Exception e) {
            log.error("Error creating timer for checking changes", e);
        }
    }

    private AbstractAjaxTimerBehavior getChangeIntervalTimerBehaviour(String checkForChangesInterval) {
        return new AbstractAjaxTimerBehavior(Duration.seconds(Integer.parseInt(checkForChangesInterval))) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                if (ConfigTreeProvider.get().getLastModificationTime()
                        .before(getDicomConfigurationManager().getLastModificationTime())) {
                    log.warn("Configuration needs to be reloaded because of concurrent modification");
                    refreshMessage.show(target);
                }
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript("Wicket.Window.unloadConfirmation = false"));
    }

    private AbstractColumn<ConfigTreeNode, String> getDeleteColumn() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("Delete")) {

            private static final long serialVersionUID = 1L;

            public void populateItem(Item<ICellPopulator<ConfigTreeNode>> cellItem, String componentId,
                                     final IModel<ConfigTreeNode> rowModel) {

                final TreeNodeType type = rowModel.getObject().getNodeType();
                if (type == null)
                    throw new RuntimeException("Error: Unknown node type, cannot create delete modal window");

                else if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_CONNECTIONS)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_APPLICATION_ENTITIES)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_HL7_APPLICATIONS)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_AUDIT_LOGGERS)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_TRANSFER_CAPABILITIES)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_TRANSFER_CAPABILITY_TYPE)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_FORWARD_RULES)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_FORWARD_OPTIONS)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_RETRIES)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_COERCIONS)
                        || type.equals(ConfigTreeNode.TreeNodeType.XCAiInitiatingGateway)
                        || type.equals(ConfigTreeNode.TreeNodeType.XCAInitiatingGateway)
                        || type.equals(ConfigTreeNode.TreeNodeType.XCAiRespondingGateway)
                        || type.equals(ConfigTreeNode.TreeNodeType.XCARespondingGateway)
                        || type.equals(ConfigTreeNode.TreeNodeType.XDSRegistry)
                        || type.equals(ConfigTreeNode.TreeNodeType.XDSSource)
                        || type.equals(ConfigTreeNode.TreeNodeType.XDSiSource)
                        || type.equals(TreeNodeType.XDSStorage)
                        || type.equals(ConfigTreeNode.TreeNodeType.XDSRepository)) {
                    cellItem.add(new Label(componentId));
                    return;
                }

                AjaxLink<Object> ajaxLink = getConfirmDeleteLink(rowModel);
                cellItem.add(
                        new LinkPanel(componentId, ajaxLink, ImageManager.IMAGE_WIZARD_COMMON_REMOVE,
                                removeConfirmation)).add(
                        new AttributeAppender("style", Model.of("width: 50px; text-align: center;")));
                if (type.equals(ConfigTreeNode.TreeNodeType.CONNECTION)) {
                    Connection connection = null;
                    try {
                        connection = ((ConnectionModel) rowModel.getObject().getModel()).getConnection();
                        for (ApplicationEntity ae : connection.getDevice().getApplicationEntities())
                            if (ae.getConnections().contains(connection))
                                ajaxLink.setEnabled(false).add(
                                        new AttributeModifier("title", new ResourceModel(
                                                "dicom.delete.connection.notAllowed")));

                        HL7DeviceExtension hl7DeviceExtension = connection.getDevice().getDeviceExtension(
                                HL7DeviceExtension.class);
                        if (hl7DeviceExtension != null) {
                            for (HL7Application hl7Application : hl7DeviceExtension.getHL7Applications())
                                if (hl7Application.getConnections().contains(connection))
                                    ajaxLink.setEnabled(false).add(
                                            new AttributeModifier("title", new ResourceModel(
                                                    "dicom.delete.connection.notAllowed")));
                        }

                        AuditLogger auditLogger = connection.getDevice().getDeviceExtension(AuditLogger.class);
                        if (auditLogger != null && auditLogger.getConnections().contains(connection))
                            ajaxLink.setEnabled(false).add(
                                    new AttributeModifier("title", new ResourceModel(
                                            "dicom.delete.connection.notAllowed")));

                    } catch (ConfigurationException ce) {
                        log.error(this.getClass().toString() + ": "
                                + "Error checking used connections of application entities: " + ce.getMessage());
                        log.debug("Exception", ce);
                        throw new RuntimeException(ce);
                    }
                }
            }
        };
    }

    private AbstractColumn<ConfigTreeNode, String> getProfileColumn() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("Profile")) {

            private static final long serialVersionUID = 1L;

            public void populateItem(Item<ICellPopulator<ConfigTreeNode>> cellItem, String componentId,
                                     final IModel<ConfigTreeNode> rowModel) {

                final TreeNodeType type = rowModel.getObject().getNodeType();
                if (type == null)
                    throw new RuntimeException("Error: Unknown node type, cannot create profile modal window");

                if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_TRANSFER_CAPABILITIES)) {

                    AjaxLink<Object> ajaxLink = new AjaxLink<Object>("wickettree.link") {

                        private static final long serialVersionUID = 1L;

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            editWindow.setTitle("Device " + ((DeviceModel) rowModel.getObject().getRoot().getModel()).getDeviceName())
                                    .setPageCreator(new ModalWindow.PageCreator() {

                                        private static final long serialVersionUID = 1L;

                                        @Override
                                        public Page createPage() {
                                            return new ApplyTransferCapabilityProfilePage(editWindow, null, rowModel.getObject()
                                                    .getParent());
                                        }
                                    });
                            editWindow.setWindowClosedCallback(windowClosedCallback).show(target);
                        }
                    };
                    cellItem.add(new LinkPanel(componentId, ajaxLink, ImageManager.IMAGE_WIZARD_COMMON_PROFILE, removeConfirmation)).add(
                            new AttributeAppender("style", Model.of("width: 50px; text-align: center;")));
                } else
                    //
                    // for proxy devices, that are under our control - add button to open dialog for launching auto-detection of transfer capabilities
                    //
                    if (    // if it is AE level
                            type.equals(ConfigTreeNode.TreeNodeType.APPLICATION_ENTITY) &&

                                    // if it is a Proxy device
                                    rowModel.getObject().getConfigurationType().equals(ConfigTreeProvider.ConfigurationType.Proxy) &&

                                    // if this device is 'connected'
                                    getDicomConfigurationManager().getConnectedDeviceUrls().get(rowModel.getObject().getRoot().getName()) != null
                            ) {

                        final String deviceName = rowModel.getObject().getRoot().getName();

                        AjaxLink<Object> ajaxLink = new AjaxLink<Object>("wickettree.link") {

                            private static final long serialVersionUID = 1L;

                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                editWindow.setTitle("Auto detect transfer capabilities for all related devices of " + deviceName)
                                        .setPageCreator(new ModalWindow.PageCreator() {

                                            private static final long serialVersionUID = 1L;

                                            @Override
                                            public Page createPage() {

                                                IModel<ConfigTreeNode> arowModel = rowModel;
                                                final String connectedDeviceUrl = getDicomConfigurationManager().getConnectedDeviceUrls().get(deviceName);


                                                ApplicationEntity ae = null;
                                                try {
                                                    ae = ((ApplicationEntityModel) rowModel.getObject().getModel()).getApplicationEntity();
                                                } catch (ConfigurationException e) {
                                                    throw new RuntimeException("Cannot get the AE", e);
                                                }

                                                return new AutoDetectTransferCapabilities(editWindow, connectedDeviceUrl, ae.getAETitle(), deviceName);
                                            }
                                        });
                                editWindow.setWindowClosedCallback(windowClosedCallback).show(target);
                            }
                        };
                        cellItem.add(new LinkPanel(componentId, ajaxLink, ImageManager.IMAGE_WIZARD_FOLDER_TRANSFER_CAPABILITY_TYPE, removeConfirmation)).add(
                                new AttributeAppender("style", Model.of("width: 50px; text-align: center;")));


                    } else {
                        cellItem.add(new Label(componentId));
                        return;
                    }

            }
        };
    }

    private AbstractColumn<ConfigTreeNode, String> getEditColumn() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("Edit")) {

            private static final long serialVersionUID = 1L;

            public void populateItem(final Item<ICellPopulator<ConfigTreeNode>> cellItem, final String componentId,
                                     final IModel<ConfigTreeNode> rowModel) {

                final TreeNodeType type = rowModel.getObject().getNodeType();
                if (type == null)
                    throw new RuntimeException("Error: Unknown node type, cannot create edit modal window");

                AjaxLink<Object> ajaxLink = new AjaxLink<Object>("wickettree.link") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        editWindow.setTitle("Device "
                                + ((DeviceModel) rowModel.getObject().getRoot().getModel()).getDeviceName());
                        setEditWindowPageCreator(rowModel, type);
                        editWindow.setWindowClosedCallback(windowClosedCallback).show(target);
                    }
                };

                ajaxLink.setVisible((!type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_APPLICATION_ENTITIES)
                        && !type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_HL7_APPLICATIONS) && !type
                        .equals(ConfigTreeNode.TreeNodeType.CONTAINER_AUDIT_LOGGERS))
                        || rowModel.getObject().getParent().getChildren().get(0).hasChildren());

                try {
                    if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_FORWARD_OPTIONS))
                        ajaxLink.setVisible(ConfigTreeProvider.get().getUniqueAETitles().length > 0);
                } catch (ConfigurationException ce) {
                    log.error("Error listing Registered AE Titles", ce);
                    if (log.isDebugEnabled())
                        ce.printStackTrace();
                }

                try {
                    if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_AUDIT_LOGGERS)
                            && ((DeviceModel) rowModel.getObject().getParent().getModel()).getDevice()
                            .getDeviceExtension(AuditLogger.class) != null)
                        ajaxLink.setVisible(false);
                } catch (ConfigurationException ce) {
                    log.error("Error accessing Audit Logger", ce);
                }

                ResourceReference image;
                if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_CONNECTIONS)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_APPLICATION_ENTITIES)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_HL7_APPLICATIONS)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_AUDIT_LOGGERS)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_TRANSFER_CAPABILITIES)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_FORWARD_RULES)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_FORWARD_OPTIONS)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_RETRIES)
                        || type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_COERCIONS))
                    image = ImageManager.IMAGE_WIZARD_COMMON_ADD;
                else
                    image = ImageManager.IMAGE_WIZARD_COMMON_EDIT;

                if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_TRANSFER_CAPABILITY_TYPE))
                    cellItem.add(new Label(componentId));
                else
                    cellItem.add(new LinkPanel(componentId, ajaxLink, image, removeConfirmation)).add(
                            new AttributeAppender("style", Model.of("width: 50px; text-align: center;")));
            }
        };
    }

    private AbstractColumn<ConfigTreeNode, String> getEchoColum() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("Echo")) {


            private static final long serialVersionUID = 1L;

            public void populateItem(final Item<ICellPopulator<ConfigTreeNode>> cellItem, final String componentId,
                                     final IModel<ConfigTreeNode> rowModel) {
                final TreeNodeType type = rowModel.getObject().getNodeType();
                if (type == null)
                    throw new RuntimeException("Error: Unknown node type, cannot create edit modal window");

                AjaxLink<Object> ajaxLink = new AjaxLink<Object>("wickettree.link") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        if (type.equals(ConfigTreeNode.TreeNodeType.APPLICATION_ENTITY)) {
                            echoWindow.setPageCreator(new ModalWindow.PageCreator() {

                                private static final long serialVersionUID = 1L;

                                @Override
                                public Page createPage() {
                                    try {
                                        return new DicomEchoPage(echoWindow, ((ApplicationEntityModel) rowModel
                                                .getObject().getModel()).getApplicationEntity());
                                    } catch (Exception e) {
                                        log.error(this.getClass().toString() + ": " + "Error creating DicomEchoPage: "
                                                + e.getMessage());
                                        log.debug("Exception", e);
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }
                        echoWindow.setWindowClosedCallback(windowClosedCallback).show(target);
                    }
                };
                if (type.equals(ConfigTreeNode.TreeNodeType.APPLICATION_ENTITY))
                    cellItem.add(
                            new LinkPanel(componentId, ajaxLink, ImageManager.IMAGE_WIZARD_ECHO, removeConfirmation))
                            .add(new AttributeAppender("style", Model.of("width: 50px; text-align: center;")));
                else
                    cellItem.add(new Label(componentId));
            }
        };
    }

    private AbstractColumn<ConfigTreeNode, String> getEmptyModelColumn() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("")) {

            private static final long serialVersionUID = 1L;

            public void populateItem(final Item<ICellPopulator<ConfigTreeNode>> cellItem, final String componentId,
                                     final IModel<ConfigTreeNode> rowModel) {

                final TreeNodeType type = rowModel.getObject().getNodeType();
                if (type == null)
                    throw new RuntimeException("Error: Unknown node type, cannot create edit modal window");

                if (!type.equals(ConfigTreeNode.TreeNodeType.DEVICE)
                        || !getDicomConfigurationManager().getConnectedDeviceUrls().containsKey(
                        rowModel.getObject().getName())
                        || !((WizardApplication) getApplication()).getDicomConfigurationManager().isReload(
                        rowModel.getObject().getName())) {
                    cellItem.add(new Label(componentId));
                    return;
                }

                AjaxLink<Object> reloadWarningLink = new AjaxLink<Object>("wickettree.link") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget arg0) {
                    }
                };

                reloadWarningLink
                        .setVisible(
                                ((WizardApplication) getApplication()).getDicomConfigurationManager().isReload(
                                        rowModel.getObject().getName())).setEnabled(false)
                        .add(new AttributeAppender("title", new ResourceModel("dicom.reload.warning.tooltip")));

                cellItem.add(
                        new LinkPanel(componentId, reloadWarningLink, ImageManager.IMAGE_WIZARD_RELOAD_WARNING, null))
                        .add(new AttributeAppender("style", Model.of("width: 50px; text-align: center;")));
            }
        };
    }

    private AbstractColumn<ConfigTreeNode, String> getSendColumn() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("Send")) {

            private static final long serialVersionUID = 1L;

            public void populateItem(final Item<ICellPopulator<ConfigTreeNode>> cellItem, final String componentId,
                                     final IModel<ConfigTreeNode> rowModel) {

                final TreeNodeType type = rowModel.getObject().getNodeType();
                if (type == null)
                    throw new RuntimeException("Error: Unknown node type, cannot create edit modal window");

                String connectedDeviceUrl = null;
                String restOperation = "restart";
                String deviceName = "";

                if (type.equals(TreeNodeType.DEVICE) && !rowModel.getObject().getConfigurationType().equals(ConfigurationType.XDS)) {
                    deviceName = rowModel.getObject().getName();
                    connectedDeviceUrl = getDicomConfigurationManager().getConnectedDeviceUrls().get(deviceName);
                } else if (XDS_EXTENSIONS.containsKey(type)) {
                    deviceName = rowModel.getObject().getRoot().getName();
                    connectedDeviceUrl = getDicomConfigurationManager().getConnectedDeviceUrls().get(deviceName);
                    restOperation = "reload";
                    if (connectedDeviceUrl != null)
                        connectedDeviceUrl += XDS_REST_PATH.get(type);
                }

                if (connectedDeviceUrl != null) {
                    IndicatingAjaxLink<Object> ajaxLink = new ReloadAjaxLink(connectedDeviceUrl, restOperation, deviceName);
                    String styleAttr = "width: 50px; text-align: center;";
                    if (getDicomConfigurationManager().isReload(deviceName, connectedDeviceUrl)) 
                        styleAttr += " background-color: red;";
                    cellItem.add(new LinkPanel(componentId, ajaxLink, ImageManager.IMAGE_WIZARD_RELOAD, reloadMessage))
                            .add(new AttributeAppender("style", Model.of(styleAttr)));
                    return;
                }

                // in case there is nothing to show
                cellItem.add(new Label(componentId));
            }
        };
    }


    private AbstractColumn<ConfigTreeNode, String> getStatusColumn() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("Status")) {

            private static final long serialVersionUID = 1L;

            public void populateItem(final Item<ICellPopulator<ConfigTreeNode>> cellItem, final String componentId,
                                     final IModel<ConfigTreeNode> rowModel) {

                final TreeNodeType type = rowModel.getObject().getNodeType();
                if (type == null)
                    throw new RuntimeException("Error: Unknown node type, cannot create edit modal window");


                String connectedDeviceUrl = null;
                DeviceExtension devExt = null;
                // for device-level status indicator
                if (type.equals(TreeNodeType.DEVICE) && !rowModel.getObject().getConfigurationType().equals(ConfigurationType.XDS)) {

                    connectedDeviceUrl = getDicomConfigurationManager().getConnectedDeviceUrls().get(
                            rowModel.getObject().getName());
                    // for extension-level status indicator
                } else if (XDS_EXTENSIONS.containsKey(type)) {

                    String deviceName = rowModel.getObject().getRoot().getName();
                    connectedDeviceUrl = getDicomConfigurationManager().getConnectedDeviceUrls().get(deviceName);
                    if (connectedDeviceUrl != null) {
                        connectedDeviceUrl += XDS_REST_PATH.get(type);

                        try {
                            devExt = ((WizardApplication) getApplication()).getDicomConfigurationManager().getDicomConfiguration().findDevice(deviceName).getDeviceExtension(XDS_EXTENSIONS.get(type));
                        } catch (ConfigurationException e) {
                            log.warn("Could not get a device extension", e);
                        }

                    }
                }


                if (connectedDeviceUrl != null) {


                    StatusAjaxLink ajaxLink = new StatusAjaxLink(connectedDeviceUrl, devExt);
                    StatusAjaxLink.StatusResponse statusResponse = ajaxLink.getStatusResponse().invoke(BasicConfigurationPanel.this);

                    cellItem.add(
                            new LinkPanel(componentId, ajaxLink, statusResponse.getImage(), null)
                                    .setImage(null, statusResponse.getResultMessage())
                                    .setOutputMarkupId(true)).add(
                            new AttributeAppender("style", Model.of("width: 50px; text-align: center;")));

                    return;
                }

                cellItem.add(new Label(componentId));
            }
        };
    }

    private AbstractColumn<ConfigTreeNode, String> getConnectionsColumn() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("Connections")) {

            private static final long serialVersionUID = 1L;

            public void populateItem(Item<ICellPopulator<ConfigTreeNode>> cellItem, String componentId,
                                     IModel<ConfigTreeNode> rowModel) {
                ConfigTreeNode configTreeNode = (ConfigTreeNode) rowModel.getObject();
                RepeatingView connectionsView = new RepeatingView(componentId);
                cellItem.add(connectionsView);
                try {
                    if (configTreeNode.getNodeType().equals(ConfigTreeNode.TreeNodeType.APPLICATION_ENTITY)) {
                        ApplicationEntity applicationEntity = ((ApplicationEntityModel) configTreeNode.getModel())
                                .getApplicationEntity();
                        if (applicationEntity != null)
                            for (Connection connection : applicationEntity.getConnections())
                                connectionsView.add(new ConnectionPanel(connectionsView.newChildId(),
                                        ImageManager.IMAGE_WIZARD_CONNECTION,
                                        Model.of(connection.getCommonName() == null ? connection.getHostname() + ":"
                                                + connection.getPort() : connection.getCommonName()), Model
                                        .of(connection.toString())));
                    }
                } catch (ConfigurationException ce) {
                    log.error(this.getClass().toString() + ": " + "Error listing connections for application entity: "
                            + ce.getMessage());
                    log.debug("Exception", ce);
                    throw new RuntimeException(ce);
                }
            }
        };
    }

    private AbstractColumn<ConfigTreeNode, String> getProtocolColumn() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("Protocol")) {

            private static final long serialVersionUID = 1L;

            public void populateItem(final Item<ICellPopulator<ConfigTreeNode>> cellItem, final String componentId,
                                     final IModel<ConfigTreeNode> rowModel) {

                ConfigTreeNode configTreeNode = (ConfigTreeNode) rowModel.getObject();
                String protocol = "";
                if (configTreeNode.getNodeType().equals(ConfigTreeNode.TreeNodeType.CONNECTION))
                    try {
                        protocol = ((ConnectionModel) configTreeNode.getModel()).getConnection().getProtocol()
                                .toString();
                    } catch (ConfigurationException ce) {
                        log.error(this.getClass().toString() + ": " + "Error fetching protocol for connection: "
                                + ce.getMessage());
                        log.debug("Exception", ce);
                    }
                cellItem.add(new Label(componentId, Model.of(protocol)));
            }
        };
    }

    private AbstractColumn<ConfigTreeNode, String> getConfigurationTypeColumn() {
        return new AbstractColumn<ConfigTreeNode, String>(Model.of("ConfigurationType")) {

            private static final long serialVersionUID = 1L;

            public void populateItem(final Item<ICellPopulator<ConfigTreeNode>> cellItem, final String componentId,
                                     final IModel<ConfigTreeNode> rowModel) {

                final ConfigurationType configurationType = rowModel.getObject().getConfigurationType();
                cellItem.add(new Label(componentId, Model.of(configurationType == null ? "" : configurationType
                        .toString())));
            }
        };
    }

    public void renderTree() throws ConfigurationException {
        IModel<Set<ConfigTreeNode>> currentState = configTree.getModel();
        configTree = new ConfigTableTree("configTree", deviceColumns, ConfigTreeProvider.get(), Integer.MAX_VALUE);
        configTree.setModel(currentState);
        form.addOrReplace(configTree);
    }

    private boolean getStatus(String statusServiceEndpoint) throws IOException, Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(statusServiceEndpoint
                + (statusServiceEndpoint.endsWith("/") ? "running" : "/running")).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200)
            throw new Exception("Expected response 200, but was " + connection.getResponseCode() + "."
                    + connection.getResponseMessage());
        else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine = reader.readLine();
            reader.close();
            return Boolean.parseBoolean(nextLine);
        }
    }

    public static String getModuleName() {
        return MODULE_NAME;
    }

    private PageCreator getModalWindowPageCreator(final IModel<ConfigTreeNode> rowModel, final TreeNodeType type) {
        return new ModalWindow.PageCreator() {

            private static final long serialVersionUID = 1L;

            @Override
            public Page createPage() {
                if (type.equals(ConfigTreeNode.TreeNodeType.DEVICE)) {
                    try {
                        ConfigTreeProvider.get().loadDevice(rowModel.getObject());
                        return new CreateOrEditDevicePage(editWindow, (DeviceModel) rowModel.getObject().getModel());
                    } catch (Exception e) {
                        log.error("Error loading device on edit", e);
                        return null;
                    }
                } else if (type.equals(ConfigTreeNode.TreeNodeType.CONNECTION)) {
                    return new CreateOrEditConnectionPage(editWindow,
                            (ConnectionModel) rowModel.getObject().getModel(), rowModel.getObject().getAncestor(2));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.APPLICATION_ENTITY)) {
                    return new CreateOrEditApplicationEntityPage(editWindow, (ApplicationEntityModel) rowModel
                            .getObject().getModel(), rowModel.getObject().getAncestor(2));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.HL7_APPLICATION)) {
                    return new CreateOrEditHL7ApplicationPage(editWindow, (HL7ApplicationModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(2));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.AUDIT_LOGGER)) {
                    return new CreateOrEditAuditLoggerPage(editWindow, (AuditLoggerModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(2));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.TRANSFER_CAPABILITY)) {
                    return new CreateOrEditTransferCapabilityPage(editWindow, (TransferCapabilityModel) rowModel
                            .getObject().getModel(), rowModel.getObject().getAncestor(3));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.FORWARD_RULE)) {
                    return new CreateOrEditForwardRulePage(editWindow, (ForwardRuleModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(2));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.FORWARD_OPTION)) {
                    return new CreateOrEditForwardOptionPage(editWindow, (ForwardOptionModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(2));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.RETRY)) {
                    return new CreateOrEditRetryPage(editWindow, (RetryModel) rowModel.getObject().getModel(), rowModel
                            .getObject().getAncestor(2));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.COERCION)) {
                    return new CreateOrEditCoercionPage(editWindow, (CoercionModel) rowModel.getObject().getModel(),
                            rowModel.getObject().getAncestor(2));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.XCAiInitiatingGateway)) {
                    return new XCAiInitiatingGatewayEditPage(editWindow, (XCAiInitiatingGatewayModel) rowModel
                            .getObject().getModel(), rowModel.getObject().getAncestor(1));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.XCAInitiatingGateway)) {
                    return new XCAInitiatingGatewayEditPage(editWindow, (XCAInitiatingGatewayModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(1));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.XCAiRespondingGateway)) {
                    return new XCAiRespondingGatewayEditPage(editWindow, (XCAiRespondingGatewayModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(1));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.XCARespondingGateway)) {
                    return new XCARespondingGatewayEditPage(editWindow, (XCARespondingGatewayModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(1));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.XDSRegistry)) {
                    return new XDSRegistryEditPage(editWindow, (XDSRegistryModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(1));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.XDSRepository)) {
                    return new XDSRepositoryEditPage(editWindow, (XDSRepositoryModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(1));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.XDSSource)) {
                    return new XDSSourceEditPage(editWindow, (XDSSourceModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(1));
                } else if (type.equals(ConfigTreeNode.TreeNodeType.XDSiSource)) {
                    return new XDSiSourceEditPage(editWindow, (XDSiSourceModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(1));
                } else if (type.equals(TreeNodeType.XDSStorage)) {
                    return new XDSStorageEditPage(editWindow, (XdsStorageModel) rowModel.getObject()
                            .getModel(), rowModel.getObject().getAncestor(1));
                } else
                    return null;
            }
        };
    }

    private void setEditWindowPageCreator(final IModel<ConfigTreeNode> rowModel, final TreeNodeType type) {
        if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_CONNECTIONS)) {
            editWindow.setPageCreator(new ModalWindow.PageCreator() {

                private static final long serialVersionUID = 1L;

                @Override
                public Page createPage() {
                    return new CreateOrEditConnectionPage(editWindow, null, rowModel.getObject().getParent());
                }
            });
        } else if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_APPLICATION_ENTITIES)) {
            editWindow.setPageCreator(new ModalWindow.PageCreator() {

                private static final long serialVersionUID = 1L;

                @Override
                public Page createPage() {
                    return new CreateOrEditApplicationEntityPage(editWindow, null, rowModel.getObject().getParent());
                }
            });
        } else if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_HL7_APPLICATIONS)) {
            editWindow.setPageCreator(new ModalWindow.PageCreator() {

                private static final long serialVersionUID = 1L;

                @Override
                public Page createPage() {
                    return new CreateOrEditHL7ApplicationPage(editWindow, null, rowModel.getObject().getParent());
                }
            });
        } else if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_AUDIT_LOGGERS)) {
            editWindow.setPageCreator(new ModalWindow.PageCreator() {

                private static final long serialVersionUID = 1L;

                @Override
                public Page createPage() {
                    return new CreateOrEditAuditLoggerPage(editWindow, null, rowModel.getObject().getParent());
                }
            });
        } else if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_TRANSFER_CAPABILITIES)) {
            editWindow.setPageCreator(new ModalWindow.PageCreator() {

                private static final long serialVersionUID = 1L;

                @Override
                public Page createPage() {
                    return new CreateOrEditTransferCapabilityPage(editWindow, null, rowModel.getObject().getParent());
                }
            });
        } else if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_FORWARD_RULES)) {
            editWindow.setPageCreator(new ModalWindow.PageCreator() {

                private static final long serialVersionUID = 1L;

                @Override
                public Page createPage() {
                    return new CreateOrEditForwardRulePage(editWindow, null, rowModel.getObject().getParent());
                }
            });
        } else if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_FORWARD_OPTIONS)) {
            editWindow.setPageCreator(new ModalWindow.PageCreator() {

                private static final long serialVersionUID = 1L;

                @Override
                public Page createPage() {
                    return new CreateOrEditForwardOptionPage(editWindow, null, rowModel.getObject().getParent());
                }
            });
        } else if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_RETRIES)) {
            editWindow.setPageCreator(new ModalWindow.PageCreator() {

                private static final long serialVersionUID = 1L;

                @Override
                public Page createPage() {
                    return new CreateOrEditRetryPage(editWindow, null, rowModel.getObject().getParent());
                }
            });
        } else if (type.equals(ConfigTreeNode.TreeNodeType.CONTAINER_COERCIONS)) {
            editWindow.setPageCreator(new ModalWindow.PageCreator() {

                private static final long serialVersionUID = 1L;

                @Override
                public Page createPage() {
                    return new CreateOrEditCoercionPage(editWindow, null, rowModel.getObject().getParent());
                }
            });
        } else {
            editWindow.setPageCreator(getModalWindowPageCreator(rowModel, type));
        }
    }

    private AjaxLink<Object> getConfirmDeleteLink(final IModel<ConfigTreeNode> rowModel) {
        return new AjaxLink<Object>("wickettree.link") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                removeConfirmation.confirm(target, new StringResourceModel("dicom.confirmDelete", this, null,
                        new Object[]{rowModel.getObject().getNodeType(), rowModel.getObject().getName()}), rowModel
                        .getObject());
            }
        };
    }

    public class StatusAjaxLink extends IndicatingAjaxLink<Object> {

        private static final long serialVersionUID = 1L;
        private final String connectedDeviceUrl;
        private DeviceExtension devExt;


        public StatusAjaxLink(String connectedDeviceUrl, DeviceExtension devExt) {
            super("wickettree.link");
            this.connectedDeviceUrl = connectedDeviceUrl;
            this.devExt = devExt;
        }

        public StatusResponse getStatusResponse() {
            return new StatusResponse();
        }

        @Override
        public void onClick(AjaxRequestTarget target) {

            if (connectedDeviceUrl == null) {
                log.warn("Service endpoint for status is not configured correctly");
                return;
            } else
                log.info("Attempting to retrieve status using service endpoint " + connectedDeviceUrl);

            StatusResponse statusResponse = new StatusResponse().invoke(StatusAjaxLink.this);
            ResourceReference image = statusResponse.getImage();
            StringResourceModel resultMessage = statusResponse.getResultMessage();
            ((LinkPanel) this.getParent()).setImage(image, resultMessage);
            target.add(getParent());
        }

        public class StatusResponse {
            private StringResourceModel resultMessage;
            private ResourceReference image;

            public StringResourceModel getResultMessage() {
                return resultMessage;
            }

            public ResourceReference getImage() {
                return image;
            }

            public StatusResponse invoke(Component component) {
                try {
                    boolean result = getStatus(connectedDeviceUrl);

                    // if extension is deactivated
                    if (devExt != null) {
                        if (((Deactivateable) devExt).isDeactivated()) {
                            image = ImageManager.IMAGE_WIZARD_RUNNING_DEACTIVATED;
                            resultMessage = new StringResourceModel("dicom.status.message.failed", component, null, new Object[]{"Extension is running but deactivated"});
                            return this;
                        }
                    }

                    resultMessage = new StringResourceModel(result ? "dicom.status.message.success"
                            : "dicom.status.message.warning", component, null);
                    image = result ? ImageManager.IMAGE_WIZARD_RUNNING : ImageManager.IMAGE_WIZARD_NOT_RUNNING;

                } catch (Exception e) {
                    log.error("Error retrieving status of connected device (@ {}): {}", connectedDeviceUrl, e.getMessage());
                    if (log.isDebugEnabled())
                        e.printStackTrace();
                    resultMessage = new StringResourceModel("dicom.status.message.failed", component, null,
                            new Object[]{e.getMessage()});
                    image = ImageManager.IMAGE_WIZARD_NOT_RUNNING;
                }
                return this;
            }
        }
    }

    private class ReloadAjaxLink extends IndicatingAjaxLink<Object> {

        private static final long serialVersionUID = 1L;
        private final String connectedDeviceUrl;
        private final String restOperation;
        private final String deviceName;

        public ReloadAjaxLink(String connectedDeviceUrl, String restOperation, String deviceName) {
            super("wickettree.link");
            this.connectedDeviceUrl = connectedDeviceUrl;
            this.restOperation = restOperation;
            this.deviceName = deviceName;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {

            if (connectedDeviceUrl == null) {
                log.warn("Service endpoint for reload is not configured correctly");
                return;
            } else {
                log.info("Attempting to reload configuration using service endpoint " + connectedDeviceUrl);
            }

            HttpURLConnection connection;
            StringResourceModel resultMessage;

            try {
                connection = (HttpURLConnection) new URL(connectedDeviceUrl
                        + (connectedDeviceUrl.endsWith("/") ? restOperation : "/" + restOperation)).openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                connection.disconnect();

                if (responseCode != 204) {
                    if (responseCode == 404) {
                        String msg = "The server has not found anything matching the Request-URI "
                                + connection.getURL().toString() + ", HTTP Status "
                                + connection.getResponseCode() + ": " + connection.getResponseMessage();
                        throw new Exception(msg);
                    } else
                        throw new Exception("</br>Expected response 204, but was "
                                + connection.getResponseCode() + ": " + connection.getResponseMessage());
                }

                ((WizardApplication) getApplication()).getDicomConfigurationManager().clearReload(deviceName, connectedDeviceUrl);
            } catch (Exception e) {
                log.error("Error reloading configuration of connected device: " + e.getMessage());
                if (log.isDebugEnabled())
                    e.printStackTrace();
                resultMessage = new StringResourceModel("dicom.reload.message.failed", this, null,
                        new Object[]{e.getMessage()});

                reloadMessage = new MessageWindow("reload-message", resultMessage) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onOk(AjaxRequestTarget target) {
                    }
                };

                BasicConfigurationPanel.this.addOrReplace(reloadMessage);
                reloadMessage.setWindowClosedCallback(windowClosedCallback).show(target);
            }
            target.add(form);
        }
    }
}
