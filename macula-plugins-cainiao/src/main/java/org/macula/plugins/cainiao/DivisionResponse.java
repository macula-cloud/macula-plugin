package org.macula.plugins.cainiao;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DivisionResponse extends CainiaoResponse {

	private DivisionVO chinaDivisionVO;

}
