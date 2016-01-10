package name.gpi.census.domain;

import java.util.Objects;

/**
 * An average object taken from the DB
 *
 */
public class DataAverage {

	protected String category;
	protected int numberOfPeople;
	protected float averageData;

	public DataAverage(String category, int numberOfPeople, float averageData) {
		super();
		this.category = category;
		this.numberOfPeople = numberOfPeople;
		this.averageData = averageData;
	}

	@Override
	public String toString() {
		return "DataAverage [category=" + category + ", numberOfPeople=" + numberOfPeople + ", averageData=" + averageData + "]";
	}

	// HashCode & equals are used for unit tests
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(averageData);
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result + numberOfPeople;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataAverage other = (DataAverage) obj;
		return Objects.equals(category, other.category) && Objects.equals(numberOfPeople, other.numberOfPeople)
				&& Math.abs(averageData - other.averageData)< 0.01;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getNumberOfPeople() {
		return numberOfPeople;
	}

	public void setNumberOfPeople(int numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

	public double getAverageData() {
		return averageData;
	}

	public void setAverageData(float averageData) {
		this.averageData = averageData;
	}

}
