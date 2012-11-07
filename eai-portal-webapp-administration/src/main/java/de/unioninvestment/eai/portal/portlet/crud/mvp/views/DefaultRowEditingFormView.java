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
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.unioninvestment.eai.portal.portlet.crud.CrudPortletApplication;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerBlob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FileMetadata;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;

/**
 * Beschreibt die Erwartungen des Presenters an die View.
 * 
 * 
 * @author siva.selvarajah
 */
public class DefaultRowEditingFormView extends DefaultPanelContentView
		implements RowEditingFormView {

	private static final long serialVersionUID = 1L;

	private Form form;
	private Presenter presenter;
	private Upload upload;

	/**
	 * Konstruktor.
	 * 
	 * @param withMargin
	 *            ob der Margin gesetzt werden soll
	 */
	public DefaultRowEditingFormView(boolean withMargin) {

		super(withMargin);

		form = new Form() {
			/**
			 * Label vor der Checkbox hinzufügen.
			 */
			protected void attachField(Object propertyId,
					com.vaadin.ui.Field field) {
				if (field instanceof CheckBox) {
					HorizontalLayout checkboxLayout = new HorizontalLayout();
					checkboxLayout.setCaption(field.getCaption());
					field.setCaption(null);
					checkboxLayout.addComponent(field);
					getLayout().addComponent(checkboxLayout);
				} else {
					super.attachField(propertyId, field);
				}
			}
		};
	}

	@Override
	public void initialize(Presenter presenter) {
		this.presenter = presenter;

		buildViewComponents();
	}

	private void buildViewComponents() {
		form.setWriteThrough(false);
		addComponent(form);

		final Button commitButton = new Button("Speichern");
		commitButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				DefaultRowEditingFormView.this.presenter.save();
			}
		});
		form.getFooter().addComponent(commitButton);

		Button resetButton = new Button("Zurücksetzen");
		resetButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				DefaultRowEditingFormView.this.presenter.resetFields();
			}
		});
		form.getFooter().addComponent(resetButton);

		Button deleteButton = new Button("Löschen");
		deleteButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				DefaultRowEditingFormView.this.presenter.delete();
			}
		});
		form.getFooter().addComponent(deleteButton);

		Button previousRowButton = new Button("Vorheriger");
		previousRowButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				DefaultRowEditingFormView.this.presenter.previousRow();
			}
		});
		form.getFooter().addComponent(previousRowButton);

		Button nextRowButton = new Button("Nächster");
		nextRowButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				DefaultRowEditingFormView.this.presenter.nextRow();
			}
		});
		form.getFooter().addComponent(nextRowButton);

		Button cancelButton = new Button("Abbrechen");
		cancelButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				DefaultRowEditingFormView.this.presenter.cancel();
			}
		});
		form.getFooter().addComponent(cancelButton);
		form.setErrorHandler(new ComponentErrorHandler() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean handleComponentError(ComponentErrorEvent event) {
				System.out
						.println("Error handler:" + this.getClass().getName());
				return true;
			}
		});
	}

	@Override
	public void displayRow(Item item) {
		Assert.notNull(item, "Row cannot be null");
		form.getLayout().removeAllComponents();
		form.setItemDataSource(item, presenter.getVisibleFields());
		presenter.addClobFields(item);
	}

	@Override
	public void setFormFieldFactory(FormFieldFactory formFieldFactory) {
		form.setFormFieldFactory(formFieldFactory);
	}

	public Form getForm() {
		return form;
	}

	@Override
	public boolean isFieldModifed(String fieldName) {
		com.vaadin.ui.Field field = form.getField(fieldName);
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
				metadata.getFileName(),
				CrudPortletApplication.getCurrentApplication());
		resource.setMIMEType(metadata.getMineType());
		Link link = buildDownloadLink(metadata, resource);

		blobField.addComponent(link);

		if (!readonly) {
			Upload upload = buildUpload(containerBlob, metadata);
			blobField.addComponent(upload);
		}
		form.getLayout().addComponent(blobField);
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

		upload.addListener(new Upload.FinishedListener() {

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
					getApplication().getMainWindow().showNotification(
							"Ein Datei darauf nicht größer als "
									+ metadata.getMaxFileSize()
									+ " Bytes sein.",
							Notification.TYPE_ERROR_MESSAGE);
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
	public void addClobField(TableColumn tableColumn, Property property) {
		TextArea area = new TextArea();
		if (tableColumn.getTitle() != null) {
			area.setCaption(tableColumn.getTitle());
		} else {
			area.setCaption(tableColumn.getName());
		}

		if (tableColumn.getWidth() != null) {
			area.setWidth(tableColumn.getWidth(), Component.UNITS_PIXELS);
		}
		if (tableColumn.getRows() != null) {
			area.setRows(tableColumn.getRows());
		}
		area.setNullRepresentation("");
		area.setReadOnly(property.isReadOnly()
				|| !tableColumn.getDefaultEditable());
		area.setPropertyDataSource(property);
		area.setWidth(100.0f, Window.UNITS_PERCENTAGE);
		form.addField(tableColumn.getName(), area);
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