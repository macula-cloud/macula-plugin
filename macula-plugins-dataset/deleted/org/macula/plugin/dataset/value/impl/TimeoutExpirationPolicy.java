package org.macula.plugin.dataset.value.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.macula.plugin.dataset.value.ValueEntry;
import org.macula.plugin.dataset.value.ValueExpirationPolicy;
import org.macula.plugin.dataset.value.scope.ValueScope;

import org.springframework.stereotype.Component;

/**
 * <p> <b>TimeoutExpirationPolicy</b> 是通过超时时间来检测是否过期的策略. </p>
 * 
 */
@Component
public final class TimeoutExpirationPolicy implements ValueExpirationPolicy {

	/**
	 * 失效时间设定.
	 */
	private final Map<ValueScope, Long> expirdTimeConfig = new HashMap<ValueScope, Long>();

	public TimeoutExpirationPolicy() {
		this(new HashMap<String, String>() {
			private static final long serialVersionUID = -8637042195960795078L;
			{
				put(ValueScope.NONE.toString(), "0");
				put(ValueScope.SESSION.toString(), "3600");
				put(ValueScope.INSTANCE.toString(), "360000");
				put(ValueScope.APPLICATION.toString(), "-1");
			}
		});
	}

	public TimeoutExpirationPolicy(final long expired) {
		this(new HashMap<String, String>() {
			private static final long serialVersionUID = -8637042195960795078L;
			{
				for (ValueScope scope : ValueScope.values()) {
					put(scope.toString(), String.valueOf(expired));
				}
			}
		});
	}

	/**
	 * 缺省是否失效值.
	 */
	private boolean defaultExpired = true;

	/**
	 * 
	 * @param config
	 */
	public TimeoutExpirationPolicy(Map<String, String> config) {
		for (Map.Entry<String, String> entry : config.entrySet()) {
			ValueScope scope = ValueScope.valueOf(entry.getKey());
			Long time = TimeUnit.MILLISECONDS.convert(Integer.valueOf(entry.getValue()), TimeUnit.SECONDS);
			expirdTimeConfig.put(scope, time);
		}
	}

	@Override
	public boolean isExpired(ValueEntry valueEntry) {
		ValueScope scope = valueEntry.getScope();
		if (expirdTimeConfig.containsKey(scope)) {
			Long time = expirdTimeConfig.get(scope);
			return time > 0 && valueEntry.getCreationTime().before(new Date(System.currentTimeMillis() - time));
		}
		return defaultExpired;
	}

	/**
	 * 设置缺省情况下是否认为失效.
	 * 
	 * @param expired
	 */
	public void setDefaultExpired(boolean expired) {
		this.defaultExpired = expired;
	}
}
