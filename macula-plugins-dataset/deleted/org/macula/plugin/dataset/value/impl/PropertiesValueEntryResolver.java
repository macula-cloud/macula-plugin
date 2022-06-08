package org.macula.plugin.dataset.value.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.value.ValueEntry;
import org.macula.plugin.dataset.value.ValueEntryResolver;
import org.macula.plugin.dataset.value.ValueEntryStorage;
import org.macula.plugin.dataset.value.scope.ValueScope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * <p> <b>PropertiesValueEntryResolver</b> 是根据配置的属性文件来获取上下文的数据信息 </p>
 */
@Slf4j
public abstract class PropertiesValueEntryResolver implements ValueEntryResolver, ApplicationContextAware {

	/**
	 * 属性文件位置列表.
	 */
	private final List<String> locations;

	/**
	 * 属性原始数据列表.
	 */
	private final Map<String, String> cachedProperties = new HashMap<String, String>();

	/**
	 * 引用Spring上下文.
	 */
	private ApplicationContext applicationContext;

	/**
	 * 数据缓存.
	 */
	@Autowired
	private ValueEntryStorage valueEntryStorage;

	private int order;

	/**
	 * 构造必须传入位置信息列表.
	 * 
	 * @param locations
	 */
	public PropertiesValueEntryResolver(List<String> locations) {
		this.locations = Collections.unmodifiableList(locations);
	}

	/**
	 * 初始化所有的属性文件
	 */
	@PostConstruct
	protected void initialProperties() {
		for (String location : locations) {
			try {
				Resource[] resources = applicationContext.getResources(location);
				if (resources != null) {
					for (int i = 0; i < resources.length; i++) {
						Resource resource = resources[i];
						if (log.isDebugEnabled()) {
							log.debug("load properties source from: {} ", resource.getURI());
						}
						Properties props = new Properties();
						props.load(resource.getInputStream());
						Set<Object> propKeys = props.keySet();
						for (Object propKey : propKeys) {
							String key = (String) propKey;
							cachedProperties.put(key, props.getProperty(key));
						}
					}
				}
			} catch (IOException e) {
				if (log.isInfoEnabled()) {
					log.info("load resource location: {} ,occured exception!", location, e);
				}
			}
		}
	}

	@Override
	public boolean support(String key) {
		return !StringUtils.isBlank(key) && cachedProperties.containsKey(key);
	}

	@Override
	public ValueEntry resolve(String attribute, Object userContext) {

		if (!support(attribute)) {
			return null;
		}

		ValueScope scope = ValueScope.obtainValueScope(attribute);
		String key = obtainCacheKey(attribute, userContext.getName(), scope);

		ValueEntry entry = valueEntryStorage.retrieve(key, scope);
		if (entry == null) {
			String originValue = cachedProperties.get(attribute);
			Serializable value = resolveInternal(originValue, userContext);

			if (log.isDebugEnabled()) {
				log.debug("Resolved properties: {} - {}", attribute, value);
			}
			entry = new ValueEntry(key, value, scope);
			valueEntryStorage.store(entry);

		}

		return entry;
	}

	/**
	 * 内部解析，由子类完成.
	 */
	protected abstract Serializable resolveInternal(String originValue, Object userContext);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * 生成Cache标识.
	 */
	protected String obtainCacheKey(String attribute, String username, ValueScope scope) {
		if (scope == ValueScope.SESSION) {
			return username + "^" + attribute;
		}
		return attribute;
	}

	@Override
	public int compareTo(ValueEntryResolver other) {
		return this.getOrder() - other.getOrder();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
