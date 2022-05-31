package org.macula.cloud.social.service;

import java.util.Map;

import org.macula.cloud.social.command.WechatLoginCredential;
import org.macula.cloud.social.feign.OAuth2ClientFeign;
import org.macula.cloud.social.util.WxaUtils;
import org.macula.engine.commons.domain.User;
import org.macula.engine.commons.domain.UserSocial;
import org.macula.plugin.core.command.CreateSocialUserCommand;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;

@Slf4j
public class WeChatService {

	@Autowired
	private OAuth2ClientFeign oauth2ClientFeign;

	@Autowired
	private ClientCredentialsResourceDetails clientDetails;

	public String token(WechatLoginCredential credential) {
		String clientId = credential.getClientId();
		String code = credential.getCode();
		WxMaService service = WxaUtils.getWxMaService(clientId);

		try {
			WxMaJscode2SessionResult sessionKey = service.getUserService().getSessionInfo(code);
			String openId = sessionKey.getOpenid();
			Map<String, Object> oauth2Result = oauth2ClientFeign.getToken(clientDetails.getClientId(), clientDetails.getClientSecret(), "password",
					credential.getBindUsername(), openId);
			return oauth2Result.getOrDefault(clientDetails.getTokenName(), "").toString();
		} catch (WxErrorException e) {
			log.error("Get token  {} error: ", code, e);
			return null;
		}
	}

	public String bindAndLogin(WechatLoginCredential credential) {
		String clientId = credential.getClientId();
		String code = credential.getCode();
		WxMaService service = WxaUtils.getWxMaService(clientId);
		WxMaUserService us = service.getUserService();

		try {
			WxMaJscode2SessionResult session = us.getSessionInfo(code);

			WxMaUserInfo userInfo = us.getUserInfo(session.getSessionKey(), credential.getEncryptedData(), credential.getIv());

			String openId = session.getOpenid();

			User user = new User();
			BeanUtils.copyProperties(credential, user);
			BeanUtils.copyProperties(userInfo, user);
			user.setUsername(credential.getBindUsername());
			user.setPassword(openId);
			user.setSource(clientId);
			user.setAccount(user.getUsername());
			user.setEnabled(true);
			user.setSource("SOCIAL");

			UserSocial social = new UserSocial();
			BeanUtils.copyProperties(credential, social);
			BeanUtils.copyProperties(userInfo, social);
			social.setAppid(clientId);
			social.setClientId(clientDetails.getClientId());
			social.setOpenId(openId);
			social.setUnionId(session.getUnionid());

			CreateSocialUserCommand command = CreateSocialUserCommand.builder().user(user).social(social).build();
			String username = oauth2ClientFeign.createSocialUser(command);
			Map<String, Object> oauth2Result = oauth2ClientFeign.getToken(clientDetails.getClientId(), clientDetails.getClientSecret(), "password",
					username, user.getPassword());
			return oauth2Result.getOrDefault(clientDetails.getTokenName(), "").toString();
		} catch (WxErrorException e) {
			log.error("BindAndLogin {} error: ", code, e);
			return null;
		}
	}

	public void unbind(WechatLoginCredential credential) {
		// TODO Auto-generated method stub

	}

	public String getAccessToken(WechatLoginCredential credential) {
		// TODO Auto-generated method stub
		return null;
	}
}
