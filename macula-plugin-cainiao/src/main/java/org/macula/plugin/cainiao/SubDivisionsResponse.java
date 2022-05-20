package org.macula.plugin.cainiao;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SubDivisionsResponse extends CainiaoResponse {

	private List<DivisionVO> divisionsList;

}
