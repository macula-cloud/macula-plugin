package org.macula.plugin.dataset.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.macula.engine.commons.domain.ApplicationAsset;

/**
 * <p> <b>JpaDataSet</b> 是数据仓库的Jpa实现. </p>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@MappedSuperclass
@ToString(callSuper = true)
public class DataSet extends ApplicationAsset {

	private static final long serialVersionUID = 1L;

	@Column(name = "EXP_TEXT", columnDefinition = "CLOB")
	private String expressionText;
	@Column(name = "HANDLER_CHAIN", columnDefinition = "CLOB")
	private String handlerChain;
	@ManyToOne(targetEntity = DataSource.class, optional = true)
	private DataSource dataSource;
	@Column(name = "IS_PAGABLE", nullable = false)
	private boolean pagable;
	@Column(name = "mid")
	private String mid;

	@OneToMany(targetEntity = DataArg.class, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dataSet")
	private List<DataArg> dataArgs;

	public static DataSet createDataSet(Long id) {
		if (id == null) {
			return null;
		}
		DataSet tmpDataSet = new DataSet();
		tmpDataSet.setId(id);
		return tmpDataSet;
	}

	public void addDataArg(DataArg dataArg) {
		if (dataArgs == null) {
			dataArgs = new ArrayList<DataArg>();
		}
		dataArg.setDataSet(this);
		dataArgs.add(dataArg);
	}

	public void updateDataArgs() {
		if (dataArgs != null) {
			List<DataArg> removed = new ArrayList<DataArg>();
			for (DataArg arg : dataArgs) {
				if (arg.isDeleted()) {
					removed.add(arg);
				} else {
					if (arg.getDataSet() == null) {
						arg.setDataSet(this);
					}
				}
			}
			for (DataArg arg : removed) {
				arg.setDataSet(null);
				dataArgs.remove(arg);
			}
		}
	}

}
