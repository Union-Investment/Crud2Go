/**
 * 
 */
package de.unioninvestment.eai.portal.portlet.crud.export;

import java.io.InputStream;
import java.io.Serializable;

import com.vaadin.addon.tableexport.TableExport;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

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
public class DownloadExportTask extends AbstractTableExportTask implements
		ExportTask {

	/**
	 * The downloaded file's name.
	 */
	private final String filename;

	/**
	 * The downloaded file's MIME-Type.
	 */
	private final String mimeType;

	/**
	 * The downloaded file's content.
	 */
	private InputStream content;

	/**
	 * @param ui
	 *            the ui the corresponding table belongs to.
	 * @param vaadinTable
	 *            the table the corresponding download-action belongs to.
	 * @param tableModel
	 *            the table's model.
	 * @param automaticDownload
	 *            <code>true</code> if browser supports automatic download.
	 * @param filename
	 *            The downloaded file's name.
	 * @param mimeType
	 *            The downloaded file's MIME-Type.
	 */
	public DownloadExportTask(
			UI ui,
			Table vaadinTable,
			de.unioninvestment.eai.portal.portlet.crud.domain.model.Table tableModel,
			boolean automaticDownload, String filename, String mimeType) {
		super(ui, vaadinTable, tableModel, automaticDownload);
		this.filename = filename;
		this.mimeType = mimeType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TableExport createExport() {
		TableExport result = new TableExport(this.vaadinTable) {

			/**
			 * @see Serializable
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void convertTable() {
				try {
					while (DownloadExportTask.this.content == null) {
						checkForInterruption();

						Thread.sleep(100);
					}
				} catch (InterruptedException x) {
					throw new ExportInterruptionException();
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean sendConverted() {
				StreamResource resource = new StreamResource(
						new StreamSource() {
							/**
							 * @see Serializable
							 */
							private static final long serialVersionUID = 1L;

							@Override
							public InputStream getStream() {
								return DownloadExportTask.this.content;
							}
						}, DownloadExportTask.this.filename);
				resource.setMIMEType(getMimeType());
				if (isAutomaticDownload()) {
					Page.getCurrent().open(resource, exportWindow,
							!"_self".equals(exportWindow));
					return true;
				} else {
					DownloadExportTask.this.frontend.finished(resource);
					return true;
				}
			}
		};
		result.setExportWindow("_blank");
		result.setMimeType(mimeType);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String createFilename() {
		return this.filename;
	}

	/**
	 * 
	 * @param progress
	 */
	public void updateProgress(float progress) {
		if (this.frontend != null) {
			this.frontend.updateProgress(progress);
		}
	}

	/**
	 * @param The
	 *            downloaded file's content.
	 */
	public void setContent(InputStream content) {
		this.content = content;
	}

}
