package org.macula.plugin.cainiao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DivisionVO {
	/** 地址编码 */
	private String divisionId;
	/**  行政区号编码（供参考） */
	private String divisionCode;
	private int divisionLevel;
	private String pinyin;
	private String divisionName;
	private String divisionTname;
	private String divisionAbbName;
	@JsonProperty(value = "isdeleted")
	private boolean deleted;
	private String version;
	/** 父地址编码，关联 divisionId */
	private String parentId;
}
