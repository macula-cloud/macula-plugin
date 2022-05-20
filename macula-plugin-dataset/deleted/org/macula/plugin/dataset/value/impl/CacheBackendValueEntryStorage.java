package org.macula.plugin.dataset.value.impl;

import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.value.ValueEntry;
import org.macula.plugin.dataset.value.ValueEntryStorage;
import org.macula.plugin.dataset.value.scope.ValueScope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * <p> <b>CacheBackendValueEntryStorage</b> 是使用CacheUtils和CacheUtils实现的策略，支持Application和Session级别的数据缓存. </p>
 */
@Component
public class CacheBackendValueEntryStorage implements ValueEntryStorage {

	@Autowired
	private CacheManager cacheManager;

	@Override
	public ValueEntry store(ValueEntry valueEntry) {
		if (valueEntry != null) {
			ValueScope scope = valueEntry.getScope();
			Cache cache = cacheManager.getCache(scope.getCacheName());
			if (cache != null) {
				cache.put(valueEntry.getKey(), valueEntry);
			}
		}
		return valueEntry;
	}

	@Override
	public ValueEntry retrieve(String key) {
		ValueEntry result = null;
		if (StringUtils.isNotBlank(key)) {
			for (ValueScope scope : ValueScope.values()) {
				result = retrieve(key, scope);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public ValueEntry retrieve(String key, ValueScope scope) {
		ValueEntry result = null;
		if (StringUtils.isNotBlank(key) && scope != null) {
			Cache cache = cacheManager.getCache(scope.getCacheName());
			if (cache != null) {

				ValueWrapper data = cache.get(key);
				if (data != null) {
					if (data.get() instanceof ValueEntry) {
						result = (ValueEntry) data.get();
						result.updateState();
						store(result);
					}
				}
			}
		}
		return result;
	}

	@Override
	public void cleanup(ValueScope... valueScope) {
		//nothing 
	}

	@Override
	public void remove(String... keys) {
		if (keys != null) {
			for (String key : keys) {
				for (ValueScope scope : ValueScope.values()) {
					remove(key, scope);
				}
			}
		}
	}

	@Override
	public void remove(ValueEntry... valueEntries) {
		if (valueEntries != null) {
			for (ValueEntry valueEntry : valueEntries) {
				if (valueEntry != null) {
					remove(valueEntry.getKey(), valueEntry.getScope());
				}
			}
		}
	}

	@Override
	public void remove(String key, ValueScope scope) {
		if (StringUtils.isNotBlank(key) && scope != null) {
			Cache cache = cacheManager.getCache(scope.getCacheName());
			if (cache != null) {
				cache.evict(key);
			}
		}
	}

}
