package org.example.userserver.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.common.constant.UserInfoType;
import org.example.common.exception.BusinessException;
import org.example.userserver.annotation.VerifyInfoCheck;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.example.common.util.CommonUtil.*;

@Aspect
@Component
@Slf4j
public class VerifyInfoCheckAspect {

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || " +
              "@within(org.springframework.stereotype.Controller)")
    public void userInfroCheckerPointcut() {
    }

    @Before("userInfroCheckerPointcut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg == null ||
                    isPrimitiveOrWrapperOrArray(arg.getClass()) ||
                    isFromSpringOrJDK(arg.getClass()) ||
                    isIterableOrMap(arg)) {
                continue;
            }
            Field[] fields = arg.getClass().getDeclaredFields();
            for(Field field : fields) {
                VerifyInfoCheck verifyInfoCheck = field.getAnnotation(VerifyInfoCheck.class);
                if(verifyInfoCheck == null) continue;
                UserInfoType type = verifyInfoCheck.type();
                field.setAccessible(true);
                Object val = null;
                try {
                    val = field.get(arg);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("反射获取直段内容失败---user-server/src/main/java/org/example/userserver/aspect/VerifyInfoCheckAspect.java");
                }
                if (!(val instanceof String content)) {
                    throw new RuntimeException("检查内容不为String---user-server/src/main/java/org/example/userserver/aspect/VerifyInfoCheckAspect.java");
                }

                // 在此处更新增加支持类型
                boolean isValid = switch (type) {
                    case EMAIL -> isValidEmail(content);
                    case PHONE -> isValidPhone(content);
                    default -> false;
                };
                if(!isValid) throw new BusinessException("邮箱号 或 手机号不合规");
            }
        }


    }
}
