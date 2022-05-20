package org.macula.cloud.social.configure;

import org.macula.cloud.social.service.WeChatService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

@ImportAutoConfiguration({ WxMaConfiguration.class })
@ComponentScan(basePackages = { "org.macula.cloud.social" })
public class SocialConfiguration {

	@ConditionalOnBean(ClientCredentialsResourceDetails.class)
	@Bean
	public WeChatService wechatService() {
		return new WeChatService();
	}
}
