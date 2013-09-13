/**
 * 
 */
package de.unioninvestment.eai.portal.portlet.crud.export;

import java.io.InputStream;
import java.io.Serializable;

import org.codehaus.groovy.runtime.StackTraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportWithExportSettings;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Download;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Download.Status;

/**
 * Export-Task that is intended to be given its download-content asynchronously
 * - it is not capable of creating any content on its own (like i.e.
 * <code>ExcelExportTask</code>). The method
 * <code>TableExport#convertTable()</code> defined in
 * <code>{@link #createExport()}</code> will poll for content to become
 * available in <code>{@link #content}</code>.
 * 
 * @author Jan Malcomess
 * @since 1.46
 */
public class DownloadExportTask extends AbstractExportTask implements
		ExportTask {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DownloadExportTask.class);

	private de.unioninvestment.eai.portal.portlet.crud.domain.model.Table tableModel;
	private Download download;

	private InputStream content;

	private volatile boolean finished;
	private volatile boolean cancelled;
	private volatile boolean started = false;

	/**
	 * @param ui
	 *            the ui the corresponding table belongs to.
	 * @param tableModel
	 *            the table's model.
	 * @param automaticDownload
	 *            <code>true</code> if browser supports automatic download.
	 */
	public DownloadExportTask(
			UI ui,
			de.unioninvestment.eai.portal.portlet.crud.domain.model.Table tableModel,
			Download download, boolean automaticDownloadIsPossible) {
		super(ui, automaticDownloadIsPossible);
		this.tableModel = tableModel;
		this.download = download;
	}

	@Override
	public void run() {
		LOGGER.info("Started export thread for report '{}'",
				download.getFilename());
		started = true;
		try {
			doWithExportSettingsAndProperLocking(tableModel, new ExportWithExportSettings() {

				public void export() {
					LOGGER.info("Building Report");
					try {
						Status status = new Download.Status() {
							@Override
							public void updateProgress(float progress) {
								checkForInterruption();
								informFrontendAboutProgress(progress);
							}
						};
						content = download.build(status);

						finished = true;
						informFrontendAboutFinish();
						LOGGER.info("Finished Building Report '{}'",
								download.getFilename());

					} catch (ExportInterruptionException e) {
						LOGGER.info("Report generation was cancelled/interrupted");
						cancelled = true;
					}
				}
			});
			LOGGER.info("Finished export thread for report '{}'",
					download.getFilename());

		} catch (Exception e) {
			finished = true;
			LOGGER.error("Error during report generation", StackTraceUtils.deepSanitize(e));
			informFrontendAboutException(e);

		} finally {
			finished = true;
		}
	}

	@Override
	public void cancel() {
		cancelled = true;
		if (started) {
			waitForFinishing();
		}
	}

	protected void checkForInterruption() {
		if (cancelled || Thread.currentThread().isInterrupted()) {
			cancelled = true;
			throw new ExportInterruptionException();
		}
	}

	private void waitForFinishing() {
		while (true) {
			if (finished) {
				return;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void sendToClient(String exportWindow) {
		if (finished && !cancelled) {
			StreamResource resource = createResourceForContent();

			Page.getCurrent().open(resource, exportWindow,
					!"_self".equals(exportWindow));
		} else {
			throw new IllegalStateException(
					"Cannot send export to client when not finished");
		}
	}

	@Override
	protected StreamResource createResourceForContent() {
		StreamResource resource = new StreamResource(new StreamSource() {
			/**
			 * @see Serializable
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public InputStream getStream() {
				return content;
			}
		}, download.getFilename());
		resource.setMIMEType(download.getMimeType());
		return resource;
	}

}
