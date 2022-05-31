package org.macula.cloud.social.util;

import org.macula.cloud.social.configure.WxMaConfiguration;

import cn.binarywang.wx.miniapp.api.WxMaService;

public class WxaUtils {

	public static WxMaService getWxMaService(String clientId) {
		return WxMaConfiguration.getMaService(clientId);
	}

}
