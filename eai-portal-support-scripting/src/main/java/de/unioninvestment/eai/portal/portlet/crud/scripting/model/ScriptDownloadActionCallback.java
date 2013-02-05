/**
 * 
 */
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;

import java.io.InputStream;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableAction.DownloadActionCallback;

/**
 * Script-Frontend for <code>{@link DownloadActionCallback}</code>.
 * 
 * @author Jan Malcomess (codecentric AG)
 */
public class ScriptDownloadActionCallback {
	/**
	 * The actual <code>DownloadActionCallback</code>.
	 */
	private final DownloadActionCallback downloadCallback;

	/**
	 * The total size of the progress bar.
	 */
	private Integer totalProgressSize;

	/**
	 * The current position of the progress bar.
	 */
	private int progressPos;

	/**
	 * @param The
	 *            actual <code>DownloadActionCallback</code>.
	 */
	public ScriptDownloadActionCallback(DownloadActionCallback downloadCallback) {
		this.downloadCallback = downloadCallback;
	}

	/**
	 * Create the downloadable content.
	 * 
	 * @param filename
	 *            Name of the created file.
	 * @param mimeType
	 *            MIME-Type of the created file.
	 * @param totalProgressSize
	 *            The total size of the progress bar.
	 * @param closure
	 *            Creates the file's content.
	 */
	public void create(String filename, String mimeType,
			Integer totalProgressSize, Closure<InputStream> closure) {
		this.totalProgressSize = totalProgressSize;
		this.progressPos = 0;
		this.downloadCallback.start(filename, mimeType);

		closure.setDelegate(this);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);

		InputStream stream = closure.call();

		this.downloadCallback.finish(stream);
	}

	/**
	 * Call to update a progress bar informing the user on the file creation
	 * progress. Advances the progress bar 1 unit.
	 */
	public void advanceProgress() {
		this.progressPos++;
		this.downloadCallback
				.updateProgess((float) (progressPos / totalProgressSize));
	}
}
