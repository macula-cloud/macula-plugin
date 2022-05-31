package org.macula.plugin.cainiao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class SubDivisionsRequest {
	private String divisionId;
}
