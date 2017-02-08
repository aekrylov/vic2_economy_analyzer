package eug.parser;

import eug.shared.GenericObject;

import java.util.function.Function;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/8/17 2:19 PM
 * <p>
 * Function that decides whether the object just read should be kept in structure
 */
public interface PostloadFilter extends Function<GenericObject, Boolean> {
}
