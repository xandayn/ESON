package common.xandayn.eson.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

public final class ArrayUtils {
    private ArrayUtils(){}

    /**
     * A functional interface that defines a function used to cast an object from one type to another.
     * For example it may be used to case a {@link Number} object into an int.
     */
    @FunctionalInterface
    private static interface ClassCaster {
        Object cast(Object input) throws Exception;
    }

    private static class ClassHandler {
        Class primitive;
        ClassCaster caster;

        private ClassHandler(Class primitive, ClassCaster caster) {
            this.primitive = primitive;
            this.caster = caster;
        }

    }

    private static final HashMap<Class, ClassHandler> _PRIMITIVE_CLASSES = new HashMap<>();

    static {
        _PRIMITIVE_CLASSES.put(Byte.class, new ClassHandler(byte.class, input -> ((Number)input).byteValue()));
        _PRIMITIVE_CLASSES.put(Short.class, new ClassHandler(short.class, input -> ((Number)input).shortValue()));
        _PRIMITIVE_CLASSES.put(Integer.class, new ClassHandler(int.class, input -> ((Number)input).intValue()));
        _PRIMITIVE_CLASSES.put(Long.class, new ClassHandler(long.class, input -> ((Number)input).longValue()));
        _PRIMITIVE_CLASSES.put(Float.class, new ClassHandler(float.class, input -> ((Number)input).floatValue()));
        _PRIMITIVE_CLASSES.put(Double.class, new ClassHandler(double.class, input -> ((Number)input).doubleValue()));
        _PRIMITIVE_CLASSES.put(Boolean.class, new ClassHandler(boolean.class, input -> input));
        _PRIMITIVE_CLASSES.put(Character.class, new ClassHandler(char.class, input -> {
            if(input instanceof Number)
                return (char)((Number)input).shortValue();
            return ((String)input).charAt(0);
        }));
    }

    /**
     * A function that returns an Object containing a primitive array with data copied from
     * a given Object array, casted to primitives.
     * <br/><br/>
     * Check _PRIMITIVE_CLASSES to see which Classes are mapped to which primitives and how they are casted
     * @param objectArray The Object array containing a single type that has a corresponding primitive type you want to convert.
     * @param objectClass The Object class that corresponds with a primitive type that you wish to cast to, for example {@link Boolean}.class
     * @return An object that can be casted to a primitive array that corresponds with the given Object class
     */
    @SuppressWarnings("unchecked")
    public static Object copyToPrimitiveArray(Object[] objectArray, Class objectClass) {
        if(objectArray == null) return null;
        ClassHandler primHandler = _PRIMITIVE_CLASSES.get(objectClass);
        if(primHandler == null) return null;
        Object primitiveArray = Array.newInstance(primHandler.primitive, objectArray.length);
        for(int i = 0; i < objectArray.length; i++) {
            try {
                Array.set(primitiveArray, i, primHandler.caster.cast(objectArray[i]));
            } catch (Exception e) {
                System.err.println("Unable to convert the following array to a primitive array:");
                System.err.println(Arrays.toString(objectArray));
                return null;
            }
        }
        return primitiveArray;
    }

}
