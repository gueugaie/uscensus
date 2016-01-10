package name.gpi.census.rest;

import java.util.Set;

import name.gpi.census.srv.ConfigurationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigRestController {

	@Autowired
	protected ConfigurationService config;

	@RequestMapping("/config/tableNames")
	public Set<String> getTableNames() {
		return config.getAuthorizedTableNames();
	}
	

	@RequestMapping("/config/columnNames")
	public Set<String> getColumnNames(@RequestParam(name="tableName", required=true) String tableName) {
		return config.getAuthorizedColumnNames(tableName);
	}

}
