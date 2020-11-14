package rganel.demo.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ResourceArguments.class)
public @interface ResourceArgument {
    String key();

    String variable();
}