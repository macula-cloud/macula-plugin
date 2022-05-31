package org.macula.plugin.cainiao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class DivisionParseRequest {

	private String address;
	private String version;

}
