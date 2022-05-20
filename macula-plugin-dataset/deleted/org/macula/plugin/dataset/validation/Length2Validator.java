package org.macula.plugin.dataset.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * <p> <b>Length2Validator</b> is Check that a string's length is between min and max. </p>
 */
public class Length2Validator implements ConstraintValidator<Length2, String> {
	private int min;
	private int max;

	@Override
	public void initialize(Length2 parameters) {
		min = parameters.min();
		max = parameters.max();
		validateParameters();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
		if (value == null) {
			return true;
		}
		int length = getLength2(value);
		return length >= min && length <= max;
	}

	private void validateParameters() {
		if (min < 0) {
			throw new IllegalArgumentException("The min parameter cannot be negative.");
		}
		if (max < 0) {
			throw new IllegalArgumentException("The max parameter cannot be negative.");
		}
		if (max < min) {
			throw new IllegalArgumentException("The length cannot be negative.");
		}
	}

	private int getLength2(String value) {
		int size = 0, length = value.length();
		for (int i = 0; i < length; i++) {
			size += (value.charAt(i) > 127 ? 2 : 1);
		}
		return size;
	}

}
