package name.gpi.census.dao;

import java.util.Collection;

import name.gpi.census.domain.DataAverage;
import name.gpi.census.domain.ResultPage;

/**
 * Generic DAO for the app. There is one SQLite impl
 *
 */
public interface USCensusDAO {

	/** All table names in the DB */
	public Collection<String> getTableNames();
	
	/**
	 * Returns a list of SQL Column names for the given table. The table name **MUST** have been validated if user input, otherwise, we have a SQL Injection risk
	 */
	public Collection<String> getColumnNames(String tableName);
	
	/** Returns a result page for the average data. Column names MUST have been validated before-hand */
	public ResultPage<DataAverage> getAverageData(String tableName, String columnToGroup, String colToAvg, int offset, int pageSize);

	
}
