package org.macula.plugin.dataset.value;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.macula.plugin.dataset.value.scope.ValueScope;

/**
 * <p> <b>ValueEntry</b> 是值信息，提供Key可放入Key-Value型的缓存. </p>
 */
public final class ValueEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 标识.
	 */
	private final String key;
	/**
	 * 具体值.
	 */
	private Serializable value;
	/**
	 * 作用域.
	 */
	private final ValueScope scope;
	/**
	 * 创建时间.
	 */
	private final long creationTime;
	/**
	 * 最后使用时间.
	 */
	private long lastUsedTime;
	/**
	 * 使用次数.
	 */
	private int usageCount;

	/**
	 * 构造.
	 */
	public ValueEntry(String key, Serializable value, ValueScope scope) {
		this.key = key;
		this.value = value;
		this.scope = scope;
		this.creationTime = System.currentTimeMillis();
		this.lastUsedTime = creationTime;
		this.usageCount = 0;
	}

	/**
	 * 获取Key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 获取具体的值.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object> T getValue() {
		return (T) value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	/**
	 * 获取作用范围.
	 */
	public ValueScope getScope() {
		return scope;
	}

	/**
	 * 获取创建时间.
	 */
	public Date getCreationTime() {
		return new Date(this.creationTime);
	}

	/**
	 * 获取最后使用时间.
	 */
	public Date getLastUsedTime() {
		return new Date(this.lastUsedTime);
	}

	/**
	 * 获取使用次数.
	 */
	public int getUsageCount() {
		return usageCount;
	}

	/**
	 * 更新自身状态.
	 */
	public void updateState() {
		this.lastUsedTime = System.currentTimeMillis();
		this.usageCount++;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(key).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ValueEntry) {
			ValueEntry other = (ValueEntry) obj;
			return new EqualsBuilder().append(this.key, other.key).isEquals();
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
