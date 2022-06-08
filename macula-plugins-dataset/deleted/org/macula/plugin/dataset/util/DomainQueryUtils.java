package org.macula.plugin.dataset.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.macula.cloud.api.exception.MaculaArgumentException;
import org.macula.cloud.api.protocol.CommonCondition;
import org.macula.cloud.api.protocol.CriteriaType;
import org.macula.cloud.api.protocol.CriteriaVisitor;
import org.macula.cloud.api.protocol.DataType;
import org.macula.plugin.core.utils.StringUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * <p> <b>DomainQueryUtils</b> 是Jpa数据查询助手. </p>
 * 
 */
public final class DomainQueryUtils {

	private DomainQueryUtils() {
		//Noops
	}

	public static <T> Page<T> query(EntityManager em, Class<?> domainClass, List<CommonCondition> conditions,
			Pageable pageable) {
		return query(em, StringUtils.EMPTY, domainClass, conditions, pageable);
	}

	public static <T> Page<T> query(EntityManager em, String select, Class<?> domainClass,
			List<CommonCondition> conditions, Pageable pageable) {
		CriteriaVisitor collector = new CriteriaVisitor();
		// 校验Conditions中条件的合法性
		if (conditions != null) {
			for (CommonCondition commonCondition : conditions) {
				String name = commonCondition.getName();
				Assert.notNull(name, "macula.base.data.conditions.name.notNull");
				Field field = ReflectionUtils.findField(domainClass, name);
				if (field == null) {
					throw new MaculaArgumentException("macula.base.data.conditions.name.notExist", name);
				}
				DataType dataType = DataType.forType(field.getType());
				if (dataType == null) {
					throw new MaculaArgumentException("macula.base.data.conditions.type.notSupport", name);
				}
				commonCondition.setDataType(dataType);
				CriteriaType criteriaType = commonCondition.getCriteriaType();
				if (criteriaType == null || !criteriaType.support(field.getType())) {
					throw new MaculaArgumentException("macula.base.data.conditions.criteriaType.notSupport", name,
							criteriaType);
				}
				if (commonCondition.getValue() == null && !criteriaType.allowNull()) {
					throw new MaculaArgumentException("macula.base.data.conditions.value.notNull", name);
				}
				commonCondition.collect(collector);
			}
		}

		StringBuilder sb = new StringBuilder(" from ").append(domainClass.getName());
		if (!collector.getQueryParts().isEmpty()) {
			sb.append(" where ").append(StringUtils.join(collector.getQueryParts(), " and "));
		}

		Query query = em.createQuery(select + sb.toString());
		if (!collector.getQueryParams().isEmpty()) {
			for (Map.Entry<String, Object> entry : collector.getQueryParams().entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		if (pageable != null) {
			query.setFirstResult((int) pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
		}
		@SuppressWarnings("unchecked")
		List<T> content = query.getResultList();

		if (pageable != null) {
			query = em.createQuery("select count(*) " + sb.toString());
			if (!collector.getQueryParams().isEmpty()) {
				for (Map.Entry<String, Object> entry : collector.getQueryParams().entrySet()) {
					query.setParameter(entry.getKey(), entry.getValue());
				}
			}
			Number total = (Number) query.getSingleResult();

			return new PageImpl<T>(content, pageable, total.intValue());
		}

		return new PageImpl<T>(content, PageRequest.of(0, Integer.MAX_VALUE), content.size());
	}
}
