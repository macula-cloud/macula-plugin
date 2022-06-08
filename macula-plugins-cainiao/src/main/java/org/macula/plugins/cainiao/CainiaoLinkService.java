package org.macula.plugins.cainiao;

import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.PostConstruct;

import org.macula.plugins.cainiao.configure.CainiaoConfig;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CainiaoLinkService {

	private CainiaoConfig config;
	private RestTemplate restTemplate;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public CainiaoLinkService(CainiaoConfig config) {
		this.config = config;
	}

	@PostConstruct
	public void initialRestTemplate() {
		restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
		httpMessageConverters.stream().forEach(httpMessageConverter -> {
			if (httpMessageConverter instanceof StringHttpMessageConverter) {
				StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
				messageConverter.setDefaultCharset(Charset.forName(config.getCharset()));
			}
		});
	}

	/**
	 * 获取菜鸟设置
	 */
	public CainiaoConfig getConfig() {
		return this.config;
	}

	/**
	 * 查询四级地址
	 */
	public DivisionResponse getChinaDivision(DivisionsRequest request) {
		try {
			String messageType = "CNDZK_CHINA_DIVISION";
			String logisticsInterface = MAPPER.writeValueAsString(request);
			String response = queryLinkApi(messageType, logisticsInterface);
			return MAPPER.readValue(response, DivisionResponse.class);
		} catch (JsonProcessingException ex) {
			throw new CainiaoException(ex.getMessage(), ex);
		}
	}

	/**
	 * 四级地址新版本查询
	 */
	public DivisionVersionListResponse getDivisionVersionList(DivisionVersionListRequest request) {
		try {
			String messageType = "CNDZK_DIVISION_VERSION_LIST";
			String logisticsInterface = MAPPER.writeValueAsString(request);
			String response = queryLinkApi(messageType, logisticsInterface);
			return MAPPER.readValue(response, DivisionVersionListResponse.class);
		} catch (JsonProcessingException ex) {
			throw new CainiaoException(ex.getMessage(), ex);
		}
	}

	/**
	 * 地址质量分类
	 */
	public AddressClassifyResponse getAddressClassify(AddressClassifyRequest request) {
		try {
			String messageType = "CNDZK_ADDRESS_CLASSIFY";
			String logisticsInterface = MAPPER.writeValueAsString(request);
			String response = queryLinkApi(messageType, logisticsInterface);
			return MAPPER.readValue(response, AddressClassifyResponse.class);
		} catch (JsonProcessingException ex) {
			throw new CainiaoException(ex.getMessage(), ex);
		}
	}

	/**
	 * 四级地址升级履历查询
	 */
	public VersionChangeListResponse getVersionChangeList(VersionChangeListRequest request) {
		try {
			String messageType = "CNDZK_VERSION_CHANGE_LIST";
			String logisticsInterface = MAPPER.writeValueAsString(request);
			String response = queryLinkApi(messageType, logisticsInterface);
			return MAPPER.readValue(response, VersionChangeListResponse.class);
		} catch (JsonProcessingException ex) {
			throw new CainiaoException(ex.getMessage(), ex);
		}
	}

	/**
	 * 查询下级四级地址
	 */
	public SubDivisionsResponse getChinaSubDivisions(SubDivisionsRequest request) {
		try {
			String messageType = "CNDZK_CHINA_SUB_DIVISIONS";
			String logisticsInterface = MAPPER.writeValueAsString(request);
			String response = queryLinkApi(messageType, logisticsInterface);
			return MAPPER.readValue(response, SubDivisionsResponse.class);
		} catch (JsonProcessingException ex) {
			throw new CainiaoException(ex.getMessage(), ex);
		}
	}

	/**
	 * 四级地址纠正
	 */
	public DivisionParseResponse getDivisionParse(DivisionParseRequest request) {
		try {
			String messageType = "CNDZK_DIVISION_PARSE";
			String logisticsInterface = MAPPER.writeValueAsString(request);
			String response = queryLinkApi(messageType, logisticsInterface);
			return MAPPER.readValue(response, DivisionParseResponse.class);
		} catch (JsonProcessingException ex) {
			throw new CainiaoException(ex.getMessage(), ex);
		}
	}

	public String queryLinkApi(String messageType, String logisticsInterface) {
		if (log.isInfoEnabled()) {
			log.info("Request -> {} , {}", messageType, logisticsInterface);
		}
		ResponseEntity<String> response = restTemplate.exchange(config.createRequestEntity(messageType, logisticsInterface), String.class);
		HttpStatus status = response.getStatusCode();
		if (status == HttpStatus.OK || status == HttpStatus.CREATED || status == HttpStatus.ACCEPTED || status == HttpStatus.INTERNAL_SERVER_ERROR) {
			String body = response.getBody();
			if (log.isInfoEnabled()) {
				log.info("Response -> {}", response);
			}
			return body;
		} else {
			throw new RuntimeException(String.format("error.openapi.response.status: %s", status));
		}
	}

}
