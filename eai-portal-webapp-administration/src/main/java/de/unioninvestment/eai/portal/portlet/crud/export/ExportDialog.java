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
package de.unioninvestment.eai.portal.portlet.crud.export;

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.LiferayTheme;

/**
 * Modaler Dialog, der beim Export einen Fortschrittsbalken anzeigt und die
 * Möglichkeit zum Abbruch bietet.
 * 
 * Im IE7/IE8 verhindert eine Sicherheitsabfrage den automatischen Download nach
 * Beendigung, daher wird hier ein separater Button angeboten und der Dialog
 * muss händisch geschlossen werden.
 * 
 * @author carsten.mjartan
 */
public class ExportDialog extends Window implements ExportFrontend {
	private static final long serialVersionUID = 1L;

	private final ExportTask exportTask;

	private ProgressBar indicator;

	private final Table table;

	private boolean automaticDownload = true;

	private Link downloadLink;

	/**
	 * Initialisierung des Dialogs inkl. Widgets
	 * 
	 * @param table
	 *            die Tabelle, die nach Schließen des Dialogs zu reaktivieren
	 *            ist.
	 * @param exportTask
	 *            der Hintergrund-Task
	 */
	public ExportDialog(Table table, ExportTask exportTask,
			boolean automaticDownload) {
		this.table = table;
		this.exportTask = exportTask;
		this.automaticDownload = automaticDownload;

		init();

		exportTask.setFrontend(this);
	}

	@SuppressWarnings("serial")
	private void init() {
		setCaption(getMessage("portlet.crud.dialog.export.title"));
		setModal(true);
		setResizable(false);
		setWidth("400px");
		addCloseListener(new CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				handleWindowCloseEvent();
			}
		});

		indicator = new ProgressBar(0.0f);
		indicator.setWidth("100%");
		UI.getCurrent().setPollInterval(1000);

		if (automaticDownload) {
			VerticalLayout layout = new VerticalLayout();
			layout.setMargin(true);
			layout.addComponent(indicator);

			setContent(layout);
		} else {
			HorizontalLayout layout = new HorizontalLayout();
			layout.setWidth("100%");
			layout.setMargin(true);
			layout.setSpacing(true);

			downloadLink = new Link();
			downloadLink.setStyleName(LiferayTheme.BUTTON_LINK);
			downloadLink
					.setCaption(getMessage("portlet.crud.dialog.export.download"));
			downloadLink.setEnabled(false);
			
			layout.addComponents(indicator, downloadLink);
			layout.setComponentAlignment(indicator, Alignment.MIDDLE_CENTER);
			layout.setComponentAlignment(downloadLink, Alignment.MIDDLE_CENTER);
			layout.setExpandRatio(indicator, 1f);
			
			setContent(layout);
		}

	}

	protected void handleWindowCloseEvent() {
		UI.getCurrent().setPollInterval(-1);
		if (!exportTask.isFinished()) {
			exportTask.cancel();
		}
		table.setEnabled(true);		
	}

	@Override
	public void updateProgress(float progress) {
		indicator.setValue(progress);
	}

	@Override
	public void finished() {
		startDownloadAndCloseDialog("_blank");
	}

	@Override
	public void finished(StreamResource resource) {
		downloadLink.setResource(resource);
		downloadLink.setEnabled(true);
	}

	private void startDownloadAndCloseDialog(String targetWindow) {
		ExportDialog.this.exportTask.sendToClient(targetWindow);
		close();
		// table.getApplication().getMainWindow().requestRepaint();
	}

	@Override
	public void handleException(final Exception e) {
		displayErrorMessage(e);
		close();
	}

	private void displayErrorMessage(final Exception e) {
		Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
	}

	/**
	 * @return indicator for testing
	 */
	ProgressBar getIndicator() {
		return indicator;
	}

}
