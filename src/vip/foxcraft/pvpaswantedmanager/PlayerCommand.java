package vip.foxcraft.pvpaswantedmanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlayerCommand {
	String cmd();
	String arg() default "";
	String des() default "这个家伙很懒没有写介绍";
}
