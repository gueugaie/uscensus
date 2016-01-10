package name.gpi.census.rest.dto;

import java.util.List;

import name.gpi.census.domain.DataAverage;
import name.gpi.census.error.AppError;

/**
 * A DTO for the rest service, containing a result page
 *
 */
public class Result {

	protected AppError error;
	protected int totalResults; // 2^31 should be enough
	protected int startOffset;
	protected int pageSize;
	protected List<DataAverage> averages; // Should be a DTO too, be the model
											// is simple enough that we merge
											// DTO and domain

	public static Result error(AppError error) {
		return new Result(error);
	}

	public static Result success(int totalResults, int startOffset, int pageSize, List<DataAverage> averages) {
		return new Result(totalResults, startOffset, pageSize, averages);
	}

	private Result(AppError error) {
		super();
		this.error = error;
	}

	private Result(int totalResults, int startOffset, int pageSize, List<DataAverage> averages) {
		super();
		this.totalResults = totalResults;
		this.startOffset = startOffset;
		this.pageSize = pageSize;
		this.averages = averages;
	}

	public AppError getError() {
		return error;
	}

	public void setError(AppError error) {
		this.error = error;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<DataAverage> getAverages() {
		return averages;
	}

	public void setAverages(List<DataAverage> averages) {
		this.averages = averages;
	}

}
