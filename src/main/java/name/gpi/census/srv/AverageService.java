package name.gpi.census.srv;

import name.gpi.census.dao.USCensusDAO;
import name.gpi.census.domain.DataAverage;
import name.gpi.census.domain.ResultPage;
import name.gpi.census.error.AppError;
import name.gpi.census.rest.dto.AverageRequest;
import name.gpi.census.rest.dto.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AverageService {

	@Autowired
	protected ConfigurationService config;
	@Autowired
	protected USCensusDAO dao;

	public Result computeResult(AverageRequest request) {
		// Validate query
		if (!config.isRequestValid(request.getTableName(), request.getColumnToGroupOn())) {
			// SQL Injection ? Form / rest manipulation ?
			return Result.error(AppError.TABLE_OR_COLUMN_NOT_FOUND);
		}
		int pageSize = request.getPageSize();
		int offset = request.getStartOffset();
		if (pageSize <= 0 || offset < 0) {
			return Result.error(AppError.INVALID_PAGING);
		}
		
		// Work
		ResultPage<DataAverage> page = dao.getAverageData(request.getTableName(), request.getColumnToGroupOn(), request.getColumnToAvg(), offset, pageSize);
		return Result.success(page.getTotalNumberOfResults(), page.getStartOffset(), page.getPageSize(), page.getResults());
	}

}
