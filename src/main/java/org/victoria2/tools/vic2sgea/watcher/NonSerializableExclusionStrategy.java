package org.victoria2.tools.vic2sgea.watcher;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import org.victoria2.tools.vic2sgea.entities.NonSerializable;

/**
 * Created by anth on 12.02.2017.
 * <p>
 * Exclusion strategy for {@link com.google.gson.GsonBuilder}
 * that excludes field marked with {@link NonSerializable} annotation
 */
public class NonSerializableExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(NonSerializable.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
