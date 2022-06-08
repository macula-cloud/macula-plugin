package org.macula.plugin.dataset.domain;

/**
 * <p> <b>DataSourceCreator</b> 是创建DataSource的接口. </p>
 */
public interface DataSourceCreator<T> {

	T createTargetDataSource(DataSource ds);
}
