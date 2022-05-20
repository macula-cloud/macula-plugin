package org.macula.plugin.dataset.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.macula.plugin.core.utils.SecurityUtils;
import org.macula.plugin.dataset.util.DataSetUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p> <b>DataParamRestController</b> 是DataParam的数据调用服务. </p>
 */
@Controller
@RequestMapping("/**/macula-base/param")
public class DataParamRestController {

	@ResponseBody
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	public List<Map<String, Object>> list(@PathVariable("code") String code,
			@RequestParam(required = false) Map<String, Object> params) {
		Object userContext = SecurityUtils.getObject();
		return DataSetUtils.createMappedOptions(code, userContext);
	}

	@ResponseBody
	@RequestMapping(value = "/{code}/pageable", method = RequestMethod.GET)
	public Page<?> page(@PathVariable("code") String code, @RequestParam(required = false) Map<String, Object> params,
			Pageable pageable) {
		Object userContext = SecurityUtils.getObject();
		return DataSetUtils.createMappedOptions(code, userContext, pageable);
	}

	@ResponseBody
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public Map<String, List<Map<String, Object>>> list2(@RequestParam("code[]") String[] codes) {
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		if (codes != null) {
			for (String code : codes) {
				result.put(code, list(code, null));
			}
		}
		return result;
	}

}
