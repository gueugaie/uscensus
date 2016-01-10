package name.gpi.census;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.gpi.census.dao.SQLiteUSCensusDAO;
import name.gpi.census.domain.DataAverage;
import name.gpi.census.domain.ResultPage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = UscensusApplication.class)
public class UscensusApplicationTests {

	@Autowired
	JdbcTemplate jdbc;
	@Autowired
	SQLiteUSCensusDAO dao;

	//
	// This test is a sample used to show that an in memory representation of
	// all results is possible,
	// fast to compute, and relatively light-weight.
	// Given the dataset size, one could base a DAO implementation on this
	// basics
	//
	// This also serves to check the dao implementation by comparing that for
	// each group of values, we get the same result

	@Test
	public void testDAO() {
		Map<String, List<DataAverage>> aggregatedDataRepo = calculateAggregates();

		for (String groupColumn : aggregatedDataRepo.keySet()) {
			System.out.println("Comparing aggregation by: " + groupColumn);
			ResultPage<DataAverage> daoResults = dao.getAverageData("census_learn_sql", groupColumn, "age", 0, Integer.MAX_VALUE);
			Assert.assertArrayEquals("Error validating DAO results for group by " + groupColumn, aggregatedDataRepo.get(groupColumn).toArray(), daoResults.getResults().toArray());
		}
	}

	protected Map<String, List<DataAverage>> calculateAggregates() {
		// Extract column names first (this could be done with the main
		// request).
		ResultSetExtractor<List<String>> columnNamesExtractor = resultSet -> {
			List<String> result = new ArrayList<>();
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				result.add(resultSet.getMetaData().getColumnName(i));
			}
			return result;
		};
		final List<String> cNames = jdbc.query("select * from census_learn_sql where age is not null limit 1", columnNamesExtractor);
		// Select all rows, do not cache result set in memory
		Map<String, Category> categoriesByName = new HashMap<>();
		PreparedStatementCreator psForward = (conn) -> conn.prepareStatement("select * from census_learn_sql where age is not null ", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		jdbc.query(psForward, (ResultSet rs) -> {
			do {
				// For each in the result set
				for (String column : cNames) {
					// Get or create the category named after the current column
				Category cat = categoriesByName.computeIfAbsent(column, col -> new Category(col));
				// Get or create the aggregate for this row set
				String colValue = rs.getString(column);
				// And add the age to the aggregate
				cat.getOrCreate(colValue).addData(rs.getInt("age"));
			}
		} while (rs.next());
	}	);
		// Convert to a representation compatible with the DAO
		Map<String, List<DataAverage>> aggregatedDataRepo = new HashMap<>();
		categoriesByName.forEach((catName, category) -> {
			// For each computed aggregate
				List<DataAverage> averages = category.aggregatesByValue.values().stream().map(aggregate ->
				// Convert local "Aggregate" class to domain's DataAverage
				// version
						new DataAverage(aggregate.aggValue, aggregate.occurences, Double.valueOf(Double.valueOf(aggregate.sumOfAges) / aggregate.occurences).floatValue()))
				// Sort the same as the DAO
						.sorted(Comparator.comparing(DataAverage::getAverageData).reversed().thenComparing(DataAverage::getCategory))
						// Collect to a list
						.collect(Collectors.toList());
				aggregatedDataRepo.put(catName, averages);
			});
		return aggregatedDataRepo;
	}

	public static class Category {
		public String name;
		public Map<String, Aggregate> aggregatesByValue = new HashMap<>();

		public Category(String name) {
			this.name = name;
		}

		public Aggregate getOrCreate(String aggValue) {
			return aggregatesByValue.computeIfAbsent(aggValue, agV -> new Aggregate(agV));
		}
	}

	public static class Aggregate {
		public String aggValue;
		public int occurences;
		public long sumOfAges;

		public Aggregate(String aggValue) {
			this.aggValue = aggValue;
			occurences = 0;
			sumOfAges = 0;
		}

		public void addData(int age) {
			occurences++;
			sumOfAges += age;
		}

		@Override
		public String toString() {
			return "Aggregate [aggValue=" + aggValue + ", occurences=" + occurences + ", sumOfAges=" + sumOfAges + "]";
		}
	}

}
