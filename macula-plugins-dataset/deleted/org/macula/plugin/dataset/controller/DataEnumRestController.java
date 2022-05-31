package org.macula.plugin.dataset.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.macula.cloud.api.context.CloudApplicationContext;
import org.macula.cloud.api.protocol.FieldOption;
import org.macula.plugin.dataset.domain.DataEnum;
import org.macula.plugin.dataset.service.DataEnumService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p> <b>DataEnumRestController</b> 枚举提供者. </p>
 */
@Controller
@RequestMapping("/**/macula-base/enum")
public class DataEnumRestController {

	@Autowired
	private DataEnumService dataEnumService;

	@ResponseBody
	@RequestMapping(value = "/{type}", method = RequestMethod.GET)
	public List<FieldOption> list(@PathVariable("type") String type) {
		Locale locale = CloudApplicationContext.getCurrentUserLocale();
		List<? extends DataEnum> enums = dataEnumService.findEnabledEnums(type, locale);
		List<FieldOption> options = new ArrayList<FieldOption>();
		for (DataEnum dataEnum : enums) {
			options.add(new FieldOption(dataEnum.getCode(), dataEnum.getName()));
		}
		return options;
	}

	@ResponseBody
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Map<String, List<FieldOption>> list2(@RequestParam("type") String[] types) {
		Map<String, List<FieldOption>> result = new HashMap<String, List<FieldOption>>();
		if (types != null) {
			for (String type : types) {
				result.put(type, list(type));
			}
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/{type}/{code}", method = RequestMethod.GET)
	public Map<String, String> code2value(@PathVariable("type") String type, @PathVariable("code") String code) {
		Locale locale = CloudApplicationContext.getCurrentUserLocale();
		String value = dataEnumService.getEnumCode2Value(type, code, locale);
		Map<String, String> result = new HashMap<String, String>();
		result.put(code, value);
		return result;
	}

}
