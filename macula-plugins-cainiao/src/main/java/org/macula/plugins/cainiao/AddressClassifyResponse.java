package org.macula.plugins.cainiao;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AddressClassifyResponse extends CainiaoResponse {

	private LinkClassificationResult data;

	@Data
	public static class LinkClassificationResult {
		private LinkQuality quality;
		private List<String> tags;
	}

	@Data
	public static class LinkQuality {
		private String code;
		private String cause;
	}
}
