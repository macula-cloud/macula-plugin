package org.macula.plugin.dataset.repository;

import java.util.List;

import org.macula.plugin.dataset.domain.DataSet;
import org.macula.plugin.dataset.domain.DataSource;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p> <b>DataSetRepository</b> 是DataSet的存取接口. </p>
 * 
 */
public interface DataSetRepository extends JpaRepository<DataSet, Long> {

	DataSet findByCode(String code);

	List<DataSet> findByDataSource(DataSource ds);
}
