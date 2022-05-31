package org.macula.plugin.cainiao;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DivisionVersionListResponse extends CainiaoResponse {
	private DivisionVersionListData data;

	@Data
	public static class DivisionVersionListData {
		private String offset;
		private List<DivisionVersionVo> versionList;
	}

	@Data
	public static class PublishVersionChangeDto {
		private String publishTime;
		private String offset;
		private String publishVersion;
		private List<ChangeVo> changeList;
		private String description;
		private String publishStatus;
	}

	@Data
	public static class ChangeVo {
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
