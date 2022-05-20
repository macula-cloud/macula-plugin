package org.macula.plugin.execlog.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ServiceInvokeProxy {

	String key() default "args[0]";

	String description() default "";

	String source() default "sourceClass.getSimpleName()";

	String sourceMethod() default "method.getName()";

	String sourceMessage() default "args";

	String target() default "";

	String targetMethod() default "";

	String targetMessage() default "result";

	String exceptionMessage() default "e";

	String success() default "result!=null && e==null";

	boolean alarm() default false;
}
