package org.macula.plugin.cainiao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DivisionParseResponse extends CainiaoResponse {

	@JsonProperty("ParseDivisionResult")
	private ParseDivisionResult parseDivisionResult;

}
