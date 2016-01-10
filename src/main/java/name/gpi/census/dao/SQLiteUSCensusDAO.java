package name.gpi.census.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import name.gpi.census.domain.DataAverage;
import name.gpi.census.domain.ResultPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class SQLiteUSCensusDAO implements USCensusDAO {

	/** The DB connection */
	@Autowired
	protected JdbcTemplate jdbc;

	public Collection<String> getTableNames() {
		return jdbc.query("SELECT * FROM sqlite_master where type='table' ", (row, rowIdx) -> row.getString(2));
	}

	@Override
	public Collection<String> getColumnNames(String tableName) {
		ResultSetExtractor<Collection<String>> columnNamesExtractor = resultSet -> {
			List<String> result = new ArrayList<>();
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				result.add(resultSet.getMetaData().getColumnName(i));
			}
			return result;
		};

		return jdbc.query("select * from " + tableName + " LIMIT 1", columnNamesExtractor);
	}

	@Override
	public ResultPage<DataAverage> getAverageData(String tableName, String columnToGroup, String colToAvg, int offset, int pageSize) {
		tableName = "\"" + tableName + "\"";
		columnToGroup = "\"" + columnToGroup + "\"";
		colToAvg = "\"" + colToAvg + "\"";

		// Count results
		String countStatement = "select count(distinct " + columnToGroup + ") from " + tableName;
		int totalResultCount = jdbc.queryForObject(countStatement, Integer.class);
		// Create groups
		String statement = "select " + columnToGroup + ", sum(1), avg(" + colToAvg + ") "
				+ " from " + tableName 
				+ " where " + colToAvg + " is not null "
				+ " group by " + columnToGroup + " order by avg(" + colToAvg + ") DESC, " + columnToGroup + " ASC "
				+ " LIMIT ? OFFSET ? ";
		List<DataAverage> averages = jdbc.query(statement, new Object[] { pageSize, offset }, (row, index) -> new DataAverage(row.getString(1), row.getInt(2), row.getFloat(3)));
		return new ResultPage<>(offset, pageSize, totalResultCount, averages);
	}

}
