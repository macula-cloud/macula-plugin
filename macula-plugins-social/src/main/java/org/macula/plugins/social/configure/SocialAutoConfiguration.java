package org.macula.plugins.social.configure;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;

// @ImportAutoConfiguration({ WxMaConfiguration.class })
// @ComponentScan(basePackages = { "org.macula.cloud.social" })
@Slf4j
@Configuration
public class SocialAutoConfiguration {

	//	@ConditionalOnBean(ClientCredentialsResourceDetails.class)
	//	@Bean
	//	public WeChatService wechatService() {
	//		return new WeChatService();
	//	}

	@PostConstruct
	public void postConstruct() {
		log.debug("[Macula] |- Plugin [Social] Auto Configure.");
	}
}
