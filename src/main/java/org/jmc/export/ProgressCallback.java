package org.jmc.export;


/**
 * Simple callback to indicate progress of an operation.
 */
public interface ProgressCallback
{
	enum Status {
		EXPORTING_OBJ, EXPORTING_TEXTURES, UPLOADING, PROCESSING, FINISHED;
	}

	/**
	 * Sets the current level of progress.
	 * @param value Progress, in the interval [0,1]
	 */
	void setProgress(float value);

	/**
	 * Sets the current status of progress.
	 * @param status
	 */
	void setStatus(Status status);
}
