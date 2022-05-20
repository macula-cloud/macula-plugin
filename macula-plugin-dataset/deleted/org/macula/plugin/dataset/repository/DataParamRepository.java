package org.macula.plugin.dataset.repository;

import java.util.List;

import org.macula.plugin.dataset.domain.DataParam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * <p> <b>DataParamRepository</b> 是DataParam的存储接口. </p>
 */
public interface DataParamRepository extends JpaRepository<DataParam, Long> {

	@Query("from JpaDataParam a where a.code = :code")
	DataParam findByCode(@Param("code") String code);

	@Query("from JpaDataParam a where a.type = :type and a.enabled = true")
	List<DataParam> findByType(@Param("type") String type);
}
