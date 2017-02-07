package eug.parser;

import eug.shared.GenericObject;

import java.util.function.BiFunction;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/7/17 11:19 PM
 * <p>
 * BiFunction that consumes parent object and name read from tokenizer and decides whether that object must be read or not
 */
public interface NameFilter extends BiFunction<GenericObject, String, Boolean> {
}
