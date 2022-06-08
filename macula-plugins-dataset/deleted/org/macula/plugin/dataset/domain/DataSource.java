package org.macula.plugin.dataset.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.macula.engine.commons.domain.ApplicationAsset;
import org.macula.plugin.dataset.validation.Length2;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@MappedSuperclass
@ToString(callSuper = true)
public class DataSource extends ApplicationAsset {

	private static final long serialVersionUID = 1L;

	/** JDBC驱动 */
	@Column(name = "TYPE", length = 10)
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType = DataSourceType.DATABASE;

	/** JDBC驱动 */
	@Column(name = "DRIVER", length = 255)
	@Length2(min = 0, max = 255)
	private String driver;
	/** JDBC URL地址 */
	@Column(name = "URI", length = 255, nullable = false)
	@NotNull
	@Length2(min = 1, max = 255)
	private String url;
	/** 数据库用户名 */
	@Column(name = "USER_NAME", length = 50)
	@Length2(min = 0, max = 50)
	private String username;
	/** 数据库密码 */
	@Column(name = "PASSWORD", length = 50)
	@Length2(min = 0, max = 50)
	private String password;
	/** 是否使用jndi */
	@Column(name = "IS_JNDI", nullable = false)
	private boolean jndi;
	/** 最大连接数 */
	@Column(name = "MAX_SIZE", length = 10, nullable = false)
	private int maxSize;
	/** 最大空闲连接数 */
	@Column(name = "MAX_IDLE", length = 10, nullable = false)
	private int maxIdle;
	/** 最大活动时间 */
	@Column(name = "MAX_ACTIVE", length = 10, nullable = false)
	private int maxActive;
	/** 最大等待时间 */
	@Column(name = "MAX_WAIT", length = 10, nullable = false)
	private int maxWait;
	/** 验证语句 */
	@Column(name = "VALIDATION_QUERY", length = 255)
	@Length2(min = 0, max = 255)
	private String validationQuery;

	@OneToMany(targetEntity = DataSet.class, fetch = FetchType.LAZY, mappedBy = "dataSource")
	private List<DataSet> datasets;

	public static DataSource createDataSource(Long id) {
		if (id == null) {
			return null;
		}
		DataSource tmpDs = new DataSource();
		tmpDs.setId(id);
		return tmpDs;
	}

}
