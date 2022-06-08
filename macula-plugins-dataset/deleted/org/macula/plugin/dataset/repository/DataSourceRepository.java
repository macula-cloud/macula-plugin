package org.macula.plugin.dataset.repository;

import org.macula.plugin.dataset.domain.DataSource;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p> <b>DataSourceRepository</b> is DataSource存储接口 </p>
 */
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {

	DataSource findByCode(String code);
}
