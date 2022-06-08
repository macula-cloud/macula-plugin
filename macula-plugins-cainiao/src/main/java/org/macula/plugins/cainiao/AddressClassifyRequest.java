package org.macula.plugins.cainiao;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AddressClassifyRequest {

	private LinkQueryAddress queryAddress;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor(staticName = "of")
	public static class LinkQueryAddress {

		private String address;
		private String countryCode;
		private Map<String, String> hint;

	}
}
