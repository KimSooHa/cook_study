package com.study.cook.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE}) // 클래스 유형 및 다른 어노테이션에 적용할 수 있도록 지정
@Retention(RetentionPolicy.RUNTIME) // 이 어노테이션이 런타임에 사용 가능해야 함
@Documented
@Constraint(validatedBy = FieldMatchValidator.class)
public @interface FieldMatch {
    String message() default "Fields do not match"; // 필드가 일치하지 않을 때 표시할 기본 오류 메시지
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String first();
    String second();

    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List { // 어노테이션을 반복적으로 사용할 수 있도록 하는 컨테이너 역할
        FieldMatch[] value();
    }
}




