package org.macula.plugins.cainiao;

import lombok.Data;

@Data
public class ParseDivisionResult {

	private String districtId;
	private String town;
	private String city;
	private String district;
	private String cityId;
	private String townId;
	private String prov;
	private String provId;

	public String getDivisionId() {
		if (townId != null) {
			return townId;
		}
		if (districtId != null) {
			return districtId;
		}
		if (cityId != null) {
			return cityId;
		}
		return provId;
	}
}
