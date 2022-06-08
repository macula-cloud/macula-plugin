package org.macula.plugin.dataset.domain;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.macula.cloud.api.protocol.DataType;
import org.macula.cloud.api.protocol.FieldControl;
import org.macula.engine.commons.domain.ApplicationAsset;
import org.macula.plugin.dataset.validation.Length2;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@MappedSuperclass
@ToString(callSuper = true)
public class DataArg extends ApplicationAsset {

	private static final long serialVersionUID = 1L;

	@Column(name = "ARG_LABEL", nullable = false, length = 50)
	@NotNull
	@Length2(min = 1, max = 50)
	private String label;
	@Column(name = "ARG_NAME", nullable = false, length = 50)
	@NotNull
	@Length2(min = 1, max = 50)
	private String name;
	@Column(name = "ARG_CLZ", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	@NotNull
	private DataType dataType;
	@Column(name = "ARG_CONTROL", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	@NotNull
	private FieldControl fieldControl;
	@Column(name = "ALLOW_NULL", nullable = false)
	private boolean allowNull;
	@Column(name = "DEFAULT_VALUE", length = 50)
	@Length2(min = 0, max = 50)
	private String defaultValue;

	@ManyToOne(targetEntity = DataSet.class, optional = false)
	private DataSet dataSet;

	@JsonIgnore
	@ManyToOne(targetEntity = DataParam.class, optional = true, fetch = FetchType.EAGER)
	private DataParam dataParam;

}
