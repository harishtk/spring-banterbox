package space.banterbox.search.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    private List<String> enumValues = null;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return enumValues.contains(s.toUpperCase());
    }

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        enumValues = new ArrayList<String>();
        Class<? extends Enum<?>> enumClazz = constraintAnnotation.enumClazz();

        for (Enum<?> enumVal : enumClazz.getEnumConstants()) {
            enumValues.add(enumVal.name());
        }
    }
}
