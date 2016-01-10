package name.gpi.census.error;

/**
 * A few errors checked by the app
 *
 */
public enum AppError {

	// Local request errors
	/** The requested table name / column does not exist in the db */
	TABLE_OR_COLUMN_NOT_FOUND,
	/** Invalid offset or page size*/
	INVALID_PAGING;
	
	;
	
}
