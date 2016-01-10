package name.gpi.census.srv;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import name.gpi.census.dao.USCensusDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A configuration manager. Holds means to validate requests, column / database
 * names, and the like
 *
 */
@Service
public class ConfigurationService {

	@Autowired
	protected USCensusDAO dao;

	// Usefull public methods
	
	public Set<String> getAuthorizedTableNames() {
		return authorizedTableNames;
	}

	public Set<String> getAuthorizedColumnNames(String tableName) {
		return authorizedColumnNames.get(tableName);
	}

	/**
	 * Return true if there is a table by that name, which contains a column by
	 * the give name. This allows the validation of SQL queries against injection.
	 */
	public boolean isRequestValid(String tableName, String columnToGroupBy) {
		return authorizedTableNames.contains(tableName) 
				&& authorizedColumnNames.get(tableName).contains(columnToGroupBy);
	}
	
	//
	// Configuration elements
	//
	/** Immutable & thread safe set of the tables inside the SQLite database */
	protected Set<String> authorizedTableNames;
	/** Immutable & thread safe map of column names for each table */
	protected Map<String, Set<String>> authorizedColumnNames;

	//
	// App Init
	//
	@PostConstruct
	protected void init() {
		computeAuthroizedTableAndNames();
		System.out.println(dao.getAverageData("census_learn_sql", "education", "age", 0, 100).getResults());
	}
	
	protected void computeAuthroizedTableAndNames() {
		Set<String> tableNames = new HashSet<>(dao.getTableNames());
		Map<String, Set<String>> columnsByTable = createAuthorizedColumnNames(tableNames);
		validateAndAffectTableAndColumns(tableNames, columnsByTable);
		System.out.println("System configured, allowed tables description: ");
		authorizedColumnNames.forEach((table, columns) -> {
			System.out.println("\tTable " + table + " -> " + columns);
		});
	}

	/** Check that we have a table to work with */
	protected void validateAndAffectTableAndColumns(Set<String> tableNames, Map<String, Set<String>> columnsByTable) {
		// Fatal error check
		if (tableNames.isEmpty()) {
			throw new IllegalStateException("FATAL : No table found, program can not function.");
		}
		// Immutability affectation of variables
		authorizedColumnNames = Collections.unmodifiableMap(columnsByTable);
		authorizedTableNames = Collections.unmodifiableSet(tableNames);
	}

	protected Map<String, Set<String>> createAuthorizedColumnNames(Set<String> tableNames) {
		Map<String, Set<String>> result = new HashMap<>();
		tableNames.forEach(tableName -> {
			result.put(tableName, Collections.unmodifiableSet(new HashSet<>(dao.getColumnNames(tableName))));
		});
		return result;
	}

}
