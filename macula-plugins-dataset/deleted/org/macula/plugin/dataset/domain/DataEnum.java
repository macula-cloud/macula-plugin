package org.macula.plugin.dataset.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.macula.engine.commons.domain.ApplicationAsset;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@MappedSuperclass
@ToString(callSuper = true)
public class DataEnum extends ApplicationAsset {

	private static final long serialVersionUID = 1L;

	@Column(name = "TYPE", nullable = false, length = 10)
	@NotNull
	private String type;
	@Column(name = "LOCALE", nullable = false, length = 255)
	@NotNull
	private String locale;
	@Column(name = "IS_ENABLED", nullable = false)
	private boolean enabled;
	@Column(name = "ORDERED", nullable = false)
	private int ordered;
	@Column(name = "COMMENTS", length = 255)
	private String comments;

}
