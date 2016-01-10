package name.gpi.census.domain;

import java.util.List;

/** A paged result from a DAO */
public class ResultPage<T> {

	protected final int startOffset;
	protected final int pageSize;
	protected final int totalNumberOfResults;
	protected final List<T> results;

	public ResultPage(int startOffset, int pageSize, int totalNumberOfResults, List<T> results) {
		super();
		this.startOffset = startOffset;
		this.pageSize = pageSize;
		this.totalNumberOfResults = totalNumberOfResults;
		this.results = results;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getPageSize() {
		return pageSize;
	}

	public List<T> getResults() {
		return results;
	}

	public int getTotalNumberOfResults() {
		return totalNumberOfResults;
	}

}
