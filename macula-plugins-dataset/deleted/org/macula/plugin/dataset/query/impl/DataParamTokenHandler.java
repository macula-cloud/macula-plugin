package org.macula.plugin.dataset.query.impl;

import java.util.Map;

import org.macula.plugin.dataset.query.TokenHandler;

/**
 * <p>
 * <b>DataParamTokenHandler</b> 将SQL语句中是参数类型的字段自动获取对应的参数值，参数值的名称为参数字段名称+_Label<br/>
 * 如： select name, #P(dealer_type$$DATA_PARAM_CODE)# from dealer where id=1 <br/>    
 * 将返回 name, dealer_type和dealer_type_LABEL三个字段。其中参数DATA_PARAM_CODE必须是code,label的值对的SQL语句，或者是静态的枚举。
 * 而 select name, dealer_type as #P(dealerType$$DATA_PARAM_CODE)# from dealer where id=1 <br/>
 * 将返回 name, dealerType和dealerType_LABEL三个字段。
 * </p>
 */
public class DataParamTokenHandler implements TokenHandler {

	private final Map<String, Object> dataContext;

	public final static String DATA_PARAM_KEY_SUFFIX = "Label";
	private static String seprator = "\\$\\$";

	public DataParamTokenHandler(Object userContext, Map<String, Object> params) {
		this.dataContext = params;
	}

	@Override
	public String handleToken(String content) {
		String[] split = content.split(seprator);
		String column = split[0];
		if (split.length > 1) {
			// 将参数字段加上_label存入对应参数的CODE，给前面翻译这个参数用
			String label = (column + "_" + DATA_PARAM_KEY_SUFFIX).replace("\"", "").replace("'", "");
			dataContext.put(label.toUpperCase(), split[1]);
		}
		return column;
	}
}
