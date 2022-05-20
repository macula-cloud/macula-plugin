package org.macula.plugin.cainiao.configure;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("macula.cloud.cainiao")
public class CainiaoConfig {

	private String appKey;
	private String secretKey;
	private String providerId;
	private String url = "https://link.cainiao.com/gateway/link.do";
	private String version = "18Q4";
	private String publishVersion = "20190509001";
	private String charset = "utf-8";

	public RequestEntity<MultiValueMap<String, String>> createRequestEntity(String messageType, String logisticsInterface) {
		String messageId = String.valueOf(System.currentTimeMillis());
		MultiValueMap<String, String> postBody = new LinkedMultiValueMap<String, String>();
		postBody.add("logistics_interface", logisticsInterface);
		postBody.add("data_digest", getDataDigest(logisticsInterface, secretKey));
		postBody.add("partner_code", appKey);
		postBody.add("logistic_provider_id", providerId);
		postBody.add("msg_type", messageType);
		postBody.add("msg_id", messageId);
		return RequestEntity.post(URI.create(url)).body(postBody);
	}

	private String getDataDigest(String content, String secretKey) {
		try {
			String message = content.concat(secretKey);
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(message.getBytes(charset));
			return Base64.getEncoder().encodeToString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
