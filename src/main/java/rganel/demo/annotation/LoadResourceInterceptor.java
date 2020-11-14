package rganel.demo.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class LoadResourceInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } finally {
            final LoadResource loadResourceAnnotation = methodInvocation.getMethod().getAnnotation(LoadResource.class);
            final Object[] arguments = methodInvocation.getArguments();

            if (arguments[1] instanceof HttpServletResponse) {
                final ResourceArgument[] resourceArguments = methodInvocation.getMethod().getAnnotationsByType(ResourceArgument.class);

                HttpServletResponse response = (HttpServletResponse) arguments[1];
                String resourceContents = new String(Files.readAllBytes(Paths.get(String.format("resources/%s", loadResourceAnnotation.path()))));

                for (ResourceArgument resourceArgument : resourceArguments) {
                    resourceContents = replaceArgument(methodInvocation, resourceContents, resourceArgument.key(), resourceArgument.variable());
                }

                response.getOutputStream().print(resourceContents);
            }
        }
    }

    private String replaceArgument(MethodInvocation methodInvocation, String resourceContents, String argumentKey, String variableName) throws NoSuchFieldException, IllegalAccessException {
        Object obj = methodInvocation.getThis();
        Class clazz = obj.getClass();
        Field field = clazz.getField(variableName);
        Class fieldType = field.getType();

        if (fieldType.getName().equals(String.class.getName())) {
            String value = (String) field.get(obj);

            return resourceContents.replace(String.format("{{%s}}", argumentKey), value);
        }

        return resourceContents;
    }
}
