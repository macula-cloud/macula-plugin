package org.macula.plugins.cainiao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class VersionChangeListRequest {

	private String version;
	private String offset;
	private String cpCode;
	private String bizCode;
}
