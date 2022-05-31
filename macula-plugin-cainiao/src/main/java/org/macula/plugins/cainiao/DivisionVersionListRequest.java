package org.macula.plugin.cainiao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class DivisionVersionListRequest {

	private String fromVer;
	private String offset;
	private String cpCode;
	private String bizCode;
}
