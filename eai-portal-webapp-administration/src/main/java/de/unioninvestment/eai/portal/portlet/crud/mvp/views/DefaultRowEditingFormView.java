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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.springframework.util.Assert;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.StreamResource;
import com.vaadin.server.UserError;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

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

	private CrudFieldFactory fieldFactory;

	/**
	 * Konstruktor.
	 * 
	 * @param withMargin
	 *            ob der Margin gesetzt werden soll. *
	 * @param useHorizontalLayout
	 *            when <code>true</code>, components are layed out horizontally.
	 *            (@since 1.45).
	 * @param width
	 *            The desired width of component (@since 1.45). Defaults to
	 *            "100%" when not specified.
	 * @param height
	 *            The desired height of component (@since 1.45). Defaults to
	 *            "100%" when not specified.
	 */
	public DefaultRowEditingFormView(boolean withMargin,
			boolean useHorizontalLayout, String width, String height) {

		super(withMargin, useHorizontalLayout, width, height);
	}

	@Override
	public void initialize(Presenter presenter, Table tableModel) {
		this.presenter = presenter;

		prepareFieldFactory(tableModel);
		buildViewComponents();
	}

	private void prepareFieldFactory(Table tableModel) {
		DefaultCrudFieldFactory fac = new DefaultCrudFieldFactory(null,
				tableModel);
		ValidationFieldFactoryWrapper validationWrapper = new ValidationFieldFactoryWrapper(
				tableModel.getContainer(), fac, tableModel.getColumns());
		this.fieldFactory = validationWrapper;
	}

	private void buildViewComponents() {
		fieldLayout = new FormLayout();
		addComponent(fieldLayout);

		CssLayout footerLayout = new CssLayout();
		footerLayout.setStyleName("actions");
		addComponent(footerLayout);

		saveButton = new Button("Speichern");
		saveButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.save();
			}
		});
		footerLayout.addComponent(saveButton);

		resetButton = new Button("Zurücksetzen");
		resetButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.resetFields();
			}
		});
		footerLayout.addComponent(resetButton);

		deleteButton = new Button("Löschen");
		deleteButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.delete();
			}
		});
		footerLayout.addComponent(deleteButton);

		previousRowButton = new Button("Vorheriger");
		previousRowButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.previousRow();
			}
		});
		footerLayout.addComponent(previousRowButton);

		nextRowButton = new Button("Nächster");
		nextRowButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.nextRow();
			}
		});
		footerLayout.addComponent(nextRowButton);

		Button cancelButton = new Button("Abbrechen");
		cancelButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.cancel();
			}
		});
		footerLayout.addComponent(cancelButton);
	}

	@Override
	public void displayRow(ContainerRow row, boolean editable, boolean deletable) {
		Assert.notNull(row, "Row cannot be null");
		Item item = row.getInternalRow();

		fieldLayout.removeAllComponents();
		try {
			binder = new FieldGroup(row.getFormItem());
			for (String fieldName : presenter.getVisibleFields()) {
				Field<?> field = fieldFactory.createField(item, fieldName);
				if (field != null) {
					binder.bind(field, fieldName);
					addFieldToLayout(field);
				}
			}
			presenter.addClobFields(item);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

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
	public void showError(String message) {
		fieldLayout.setComponentError(new UserError(message));
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

		StreamSource streamSource = containerBlob.getStreamSource();
		final FileMetadata metadata = tableColumn.getFileMetadata();
		StreamResource resource = new StreamResource(streamSource,
				metadata.getFileName());
		resource.setMIMEType(metadata.getMineType());
		Link link = buildDownloadLink(metadata, resource);

		blobField.addComponent(link);

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

		binder.bind(area, columnName);
		
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
