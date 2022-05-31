package org.macula.plugin.dataset.value.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.macula.plugin.dataset.value.ValueEntry;
import org.macula.plugin.dataset.value.ValueEntryStorage;
import org.macula.plugin.dataset.value.ValueExpirationPolicy;
import org.macula.plugin.dataset.value.scope.ValueScope;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p> <b>InMemoryValueEntryStorage</b> 是ValueCacheStoragy的内存实现. </p>
 */
public class InMemoryValueEntryStorage implements ValueEntryStorage {

	private final ConcurrentMap<String, ValueEntry> cache = new ConcurrentHashMap<String, ValueEntry>();

	@Autowired
	private ValueExpirationPolicy valueExpirationPolicy = null;

	@Override
	public ValueEntry store(ValueEntry valueEntry) {
		if (cache.containsKey(valueEntry.getKey())) {
			cache.remove(valueEntry.getKey());
		}
		return cache.put(valueEntry.getKey(), valueEntry);
	}

	@Override
	public ValueEntry retrieve(String key) {
		ValueEntry entry = cache.get(key);
		if (entry != null) {
			entry.updateState();
		}
		return entry;
	}

	@Override
	public ValueEntry retrieve(String key, ValueScope valueScope) {
		return retrieve(key);
	}

	@Override
	public void cleanup(ValueScope... valueScope) {
		List<ValueScope> scopes = Arrays.asList(valueScope);
		for (Map.Entry<String, ValueEntry> cacheEntry : cache.entrySet()) {
			ValueEntry entry = cacheEntry.getValue();
			if (scopes.contains(entry.getScope()) || valueExpirationPolicy.isExpired(entry)) {
				cache.remove(cacheEntry.getKey());
			}
		}
	}

	@Override
	public void remove(String... keys) {
		for (String key : keys) {
			ValueEntry entry = cache.get(key);
			if (valueExpirationPolicy.isExpired(entry)) {
				cache.remove(entry.getKey());
			}
		}
	}

	@Override
	public void remove(String key, ValueScope scope) {
		remove(key);
	}

	@Override
	public void remove(ValueEntry... valueEntries) {
		for (ValueEntry valueEntry : valueEntries) {
			cache.remove(valueEntry.getKey());
		}
	}

	/**
	 * @param valueExpirationPolicy
	 *            the valueExpirationPolicy to set
	 */
	public void setExpirationPolicy(ValueExpirationPolicy valueExpirationPolicy) {
		this.valueExpirationPolicy = valueExpirationPolicy;
	}

}
