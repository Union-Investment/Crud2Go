/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

import static de.unioninvestment.eai.portal.support.vaadin.context.Context.getMessage;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.springframework.util.Assert;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerBlob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FileMetadata;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.CrudFieldFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.DefaultCrudFieldFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.ValidationFieldFactoryWrapper;

/**
 * Beschreibt die Erwartungen des Presenters an die View.
 * 
 * 
 * @author siva.selvarajah
 */
public class DefaultRowEditingFormView extends DefaultPanelContentView
		implements RowEditingFormView {

	private static final long serialVersionUID = 1L;

	private Presenter presenter;
	private Upload upload;

	private Button saveButton;
	private Button resetButton;
	private Button deleteButton;

	private Button nextRowButton;

	private Button previousRowButton;

	private FieldGroup binder;

	private FormLayout fieldLayout;

	private Label errorLabel;

	private CrudFieldFactory fieldFactory;

	private CssLayout footerLayout;

	private Button switchModeButton = null;

	/**
	 * Leerer Konstruktor.
	 */
	public DefaultRowEditingFormView() {
	}

	@Override
	public void initialize(Presenter presenter, Table tableModel) {
		this.presenter = presenter;

		prepareFieldFactory(tableModel);
		buildViewComponents(tableModel.isModeChangeable());
	}

	private void prepareFieldFactory(Table tableModel) {
		DefaultCrudFieldFactory fac = new DefaultCrudFieldFactory(null,
				tableModel);
		ValidationFieldFactoryWrapper validationWrapper = new ValidationFieldFactoryWrapper(
				tableModel.getContainer(), fac, tableModel.getColumns());
		this.fieldFactory = validationWrapper;
	}

	private void buildViewComponents(boolean addSwitchModeButton) {
		fieldLayout = new FormLayout();
		addComponent(fieldLayout);

		errorLabel = new Label();
		errorLabel.setStyleName("error");
		errorLabel.setVisible(false);
		addComponent(errorLabel);

		footerLayout = new CssLayout();
		footerLayout.setStyleName("actions");
		addComponent(footerLayout);

		if (addSwitchModeButton) {
			switchModeButton = new Button(getMessage("portlet.crud.button.editMode"));
			switchModeButton.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public void buttonClick(ClickEvent event) {
					presenter.changeMode();
				}
			});
			footerLayout.addComponent(switchModeButton);
		}

		saveButton = new Button(getMessage("portlet.crud.button.save"));
		saveButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.save();
			}
		});

		resetButton = new Button(getMessage("portlet.crud.button.reset"));
		resetButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.resetFields();
			}
		});

		deleteButton = new Button(getMessage("portlet.crud.button.remove"));
		deleteButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.delete();
			}
		});

		previousRowButton = new Button(getMessage("portlet.crud.button.previousRow"));
		previousRowButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.previousRow();
			}
		});

		nextRowButton = new Button(getMessage("portlet.crud.button.nextRow"));
		nextRowButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.nextRow();
			}
		});

		Button cancelButton = new Button(getMessage("portlet.crud.button.cancel"));
		cancelButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.cancel();
			}
		});
		footerLayout.addComponents(saveButton, saveButton, deleteButton,
				previousRowButton, nextRowButton, cancelButton);
	}

	@Override
	public void updateButtonsForViewMode() {
		if (switchModeButton != null) {
			switchModeButton.setCaption(getMessage("portlet.crud.button.editMode"));
		}
		saveButton.setVisible(false);
		resetButton.setVisible(false);
	}

	@Override
	public void updateButtonsForEditMode() {
		if (switchModeButton != null) {
			switchModeButton.setCaption(getMessage("portlet.crud.button.viewMode"));
		}
		saveButton.setVisible(true);
		resetButton.setVisible(true);
		
	}

	@Override
	public void displayRow(ContainerRow row, boolean editable, boolean deletable) {
		Assert.notNull(row, "Row cannot be null");
		Item item = row.getInternalRow();

		fieldLayout.removeAllComponents();

		binder = new FieldGroup(row.getFormItem());
		for (String fieldName : presenter.getVisibleFields()) {
			Field<?> field = fieldFactory.createField(item, fieldName);
			if (field != null) {
				if (field instanceof AbstractField) {
					((AbstractField<?>) field).setValidationVisible(true);
				}
				// see https://dev.vaadin.com/ticket/11753
				boolean initiallyReadonly = field.isReadOnly();
				if (initiallyReadonly) {
					field.setPropertyDataSource(binder.getItemDataSource()
							.getItemProperty(fieldName));
				} else {
					// only bind if not readonly, so that readonly fields are
					// not part of the commit()
					binder.bind(field, fieldName);
				}
				addFieldToLayout(field);
			}
		}
		presenter.addClobFields(item);

		saveButton.setEnabled(editable);
		resetButton.setEnabled(editable);
		deleteButton.setEnabled(deletable);
		previousRowButton.setEnabled(presenter.hasPreviousRow());
		nextRowButton.setEnabled(presenter.hasNextRow());
	}

	private void addFieldToLayout(Field<?> field) {
		if (field instanceof CheckBox) {
			HorizontalLayout checkboxLayout = new HorizontalLayout();
			checkboxLayout.setCaption(field.getCaption());
			field.setCaption(null);
			checkboxLayout.addComponent(field);

			fieldLayout.addComponent(checkboxLayout);
		} else {
			fieldLayout.addComponent(field);
		}
	}

	@Override
	public void commit() throws CommitException {
		binder.commit();
	}

	@Override
	public void discard() {
		binder.discard();
	}

	@Override
	public void showFormError(String message) {
		errorLabel.setVisible(true);
		errorLabel.setValue(message);
	}

	@Override
	public void hideFormError() {
		errorLabel.setVisible(false);
		errorLabel.setValue(null);
	}

	@Override
	public boolean isFieldModifed(String fieldName) {
		Field<?> field = binder.getField(fieldName);
		if (field != null && field.isModified()) {
			return true;
		}
		return false;
	}

	@Override
	public void addBlobField(TableColumn tableColumn,
			final ContainerBlob containerBlob, boolean readonly) {

		HorizontalLayout blobField = new HorizontalLayout();
		if (tableColumn.getTitle() != null) {
			blobField.setCaption(tableColumn.getTitle());
		} else {
			blobField.setCaption(tableColumn.getName());
		}

		blobField.setSpacing(true);

		final FileMetadata metadata = tableColumn.getFileMetadata();
		Assert.notNull(metadata, "Missing file metadata for field '"
				+ tableColumn.getName() + "'");
		if (!containerBlob.isEmpty()) {
			StreamSource streamSource = containerBlob.getStreamSource();
			StreamResource resource = new StreamResource(streamSource,
					metadata.getFileName());
			resource.setMIMEType(metadata.getMineType());
			Link link = buildDownloadLink(metadata, resource);
			blobField.addComponent(link);
		}

		if (!readonly) {
			Upload upload = buildUpload(containerBlob, metadata);
			blobField.addComponent(upload);
		}
		fieldLayout.addComponent(blobField);
	}

	private Upload buildUpload(final ContainerBlob containerBlob,
			final FileMetadata metadata) {
		upload = new Upload();
		if (metadata.getUploadCaption() != null) {
			upload.setButtonCaption(metadata.getUploadCaption());
		} else {
			upload.setButtonCaption("Upload");
		}
		upload.setImmediate(true);
		upload.setReceiver(new BlobUploadReceiver());

		upload.addFinishedListener(new Upload.FinishedListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void uploadFinished(FinishedEvent event) {
				BlobUploadReceiver receiver = (BlobUploadReceiver) upload
						.getReceiver();
				if (receiver.getBaos().size() <= 0
						|| receiver.getBaos().size() <= metadata
								.getMaxFileSize()) {
					containerBlob.setValue(receiver.getBaos().toByteArray());
				} else {
					Notification.show("Ein Datei darauf nicht größer als "
							+ metadata.getMaxFileSize() + " Bytes sein.",
							Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		return upload;
	}

	private Link buildDownloadLink(final FileMetadata metadata,
			StreamResource resource) {
		Link link = new Link();
		link.setTargetName("_blank");
		if (metadata.getDownloadCaption() != null) {
			link.setCaption(metadata.getDownloadCaption());
		} else {
			link.setCaption(metadata.getFileName());
		}
		link.setResource(resource);
		return link;
	}

	@Override
	public void addClobField(TableColumn tableColumn, boolean readOnly) {
		String columnName = tableColumn.getName();

		TextArea area = new TextArea();
		if (tableColumn.getTitle() != null) {
			area.setCaption(tableColumn.getTitle());
		} else {
			area.setCaption(columnName);
		}

		if (tableColumn.getWidth() != null) {
			area.setWidth(tableColumn.getWidth(), Unit.PIXELS);
		}
		if (tableColumn.getRows() != null) {
			area.setRows(tableColumn.getRows());
		}
		area.setNullRepresentation("");
		area.setReadOnly(readOnly || !tableColumn.getDefaultEditable());
		area.setWidth(100.0f, Unit.PERCENTAGE);

		// see https://dev.vaadin.com/ticket/11753
		boolean initiallyReadonly = area.isReadOnly();
		if (initiallyReadonly) {
			area.setPropertyDataSource(binder.getItemDataSource()
					.getItemProperty(columnName));
		} else {
			// only bind if not readonly, so that readonly fields are not part
			// of the commit()
			binder.bind(area, columnName);
		}

		addFieldToLayout(area);
	}

	public class BlobUploadReceiver implements Receiver {

		private static final long serialVersionUID = 1L;
		private ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

		@Override
		public OutputStream receiveUpload(String filename, String mimetype) {
			baos.reset();
			return baos;
		}

		public ByteArrayOutputStream getBaos() {
			return baos;
		}

	}

}
