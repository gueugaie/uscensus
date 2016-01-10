package name.gpi.census.rest;

import name.gpi.census.rest.dto.AverageRequest;
import name.gpi.census.rest.dto.Result;
import name.gpi.census.srv.AverageService;
import name.gpi.census.srv.ConfigurationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CensusRestController {

	@Autowired protected AverageService averageService;
	@Autowired protected ConfigurationService config;
	
	@Value(value = "${defaultPageSize}")
	protected int defaultPageSize;
	
	@RequestMapping("/average")
	public Result average(
			@RequestParam(value = "table", required = true) String table,
			@RequestParam(value = "colToGroup", required = true) String colToGroup,
			@RequestParam(value = "colToAvg", required = true) String colToAvg,
			@RequestParam(value = "startOffset", required = false) Integer offset,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		// Validation
		if (pageSize == null) {
			pageSize = defaultPageSize;
		}
		if (offset == null) {
			offset = 0;
		}
		return averageService.computeResult(new AverageRequest(offset, pageSize, table, colToGroup, colToAvg));
	}

}
