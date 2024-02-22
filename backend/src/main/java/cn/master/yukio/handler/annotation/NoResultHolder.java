package cn.master.yukio.handler.annotation;

import java.lang.annotation.*;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoResultHolder {
}
