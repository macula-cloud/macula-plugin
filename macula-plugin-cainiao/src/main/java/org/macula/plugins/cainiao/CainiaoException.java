package org.macula.plugin.cainiao;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

public class CainiaoException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public CainiaoException(String msg) {
		super(msg);
	}

	public CainiaoException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}
}
