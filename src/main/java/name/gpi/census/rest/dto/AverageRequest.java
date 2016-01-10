package name.gpi.census.rest.dto;

/**
 * The components of a rest Request
 *
 */
public class AverageRequest {

	protected final int startOffset;
	protected final int pageSize;
	protected final String tableName;
	protected final String columnToGroupOn;
	protected final String columnToAvg;

	public AverageRequest(int startOffset, int pageSize, String tableName, String columnToGroupOn, String columnToAvg) {
		super();
		this.startOffset = startOffset;
		this.pageSize = pageSize;
		this.tableName = tableName;
		this.columnToGroupOn = columnToGroupOn;
		this.columnToAvg = columnToAvg;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getTableName() {
		return tableName;
	}

	public String getColumnToGroupOn() {
		return columnToGroupOn;
	}

	public String getColumnToAvg() {
		return columnToAvg;
	}

	@Override
	public String toString() {
		return "AverageRequest [startOffset=" + startOffset + ", pageSize="
				+ pageSize + ", tableName=" + tableName + ", columnToGroupOn="
				+ columnToGroupOn + ", columnToAvg=" + columnToAvg + "]";
	}

}
