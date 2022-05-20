package org.macula.plugin.cainiao;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class VersionChangeListResponse extends CainiaoResponse {

	private VersionChangeDto data;

	@Data
	public static class VersionChangeDto {
		private String publishTime;
		private String offset;
		private String publishVersion;
		private String description;
		private String publishStatus;
		private List<VersionChangeVo> changeList;
	}

	@Data
	public static class VersionChangeVo {

		private String townName;
		private String provName;
		private String changeType;
		private String cityId;
		private String townId;
		private String changeDetails;
		private String hasDetail;
		private String cityName;
		private String countyId;
		private String changeInfo;
		private String areaType;
		private String provId;
		private String countyName;
	}
}
