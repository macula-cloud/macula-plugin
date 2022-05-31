package org.macula.plugins.sap;

import org.hibersap.annotations.Bapi;

public abstract class AbstractBapi implements ExecuteBapi {

	@Override
	public boolean hasSuccess() {
		return true;
	}

	@Override
	public String getName() {
		Bapi bapi = this.getClass().getAnnotation(Bapi.class);
		return bapi == null ? "" : bapi.value();
	}

	@Override
	public String getErrorMessage() {
		return "";
	}

}
