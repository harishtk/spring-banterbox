package space.banterbox.feature.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation that checks if a String contains only lowercase letters.
 * This validator is used in conjunction with the {@link Lowercase} constraint annotation.
 * Null values are considered valid.
 */

public class LowercaseValidator implements ConstraintValidator<Lowercase, String> {
    /**
     * Validates if the given string contains only lowercase letters.
     *
     * @param value   the string to validate
     * @param context the constraint validator context
     * @return true if the string is null or contains only lowercase letters, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.equals(value.toLowerCase());
    }
}
