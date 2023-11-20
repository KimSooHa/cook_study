package com.study.cook.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;

    /**
     * 초기화
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }


    /**
     * 유효성 검증
     * @param value object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return
     */
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {// 두 필드가 동일한 값을 가지는지 검증
            final Object firstObj = org.springframework.beans.BeanUtils.getPropertyDescriptor(value.getClass(), firstFieldName) // newPwd
                    .getReadMethod().invoke(value); // reflection
            final Object secondObj = org.springframework.beans.BeanUtils.getPropertyDescriptor(value.getClass(), secondFieldName) // newPwdConfirm
                    .getReadMethod().invoke(value);

            return firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj); // 둘다 null이거나 첫번쨰 값이 null이 아니면 두번째 값과 비교
        } catch (final Exception ignore) {
            // ignore
        }
        return true;
    }
}
