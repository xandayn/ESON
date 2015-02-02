package common.xandayn.eson;

import common.xandayn.eson.util.ArrayUtils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * A class that contains a JSON object and all of its children
 */
public class JSONEntry {

    //<editor-fold desc="JSONEntry static members">
    private static ScriptEngine _JS_ENGINE;

    static {
        _JS_ENGINE = new ScriptEngineManager().getEngineByName("nashorn");
    }

    public static JSONEntry parseJSON(String filePath) throws IOException, ScriptException {
        String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
        String evalString = String.format("JSON.parse('%s');", jsonData.replace("\n", "").replace("\r", "").replace("\t", ""));
        Object jsonObject = _JS_ENGINE.eval(evalString);
        return new JSONEntry(jsonObject);
    }
    //</editor-fold>

    private Object _jsonValue;

    private JSONEntry(Object jsonRoot) {
        this._jsonValue = jsonRoot;
    }


    //<editor-fold desc="JSONEntry Data Types">

    /**
     * A convenience function to access elements in a JSON Array
     * @param position The position to access
     * @return JSONEntry.getEntry for the key Integer.tostring(position)
     *
     * @see java.lang.Integer
     */
    public JSONEntry getArrayEntry(int position) {
        return getEntry(Integer.toString(position));
    }

    /**
     * @param key The key to check for inside the JSON tree
     * @return A JSONEntry corresponding to a given key, or an undefined JSONEntry
     * if it doesn't exist.
     */
    public JSONEntry getEntry(String key) {
        return _jsonValue == null ? new JSONEntry(null) : new JSONEntry(((ScriptObjectMirror) _jsonValue).get(key));
    }

    /**
     * Converts a JSONEntry into an array of JSONEntry subobjects
     * @return A JSONEntry array that contains all of elements in the requested JSON array.<br/>
     * Or null if the object is not a JSON array.
     */
    public JSONEntry[] asJSONEntryArray() {
        return asTArray(JSONEntry.class, false);
    }
    //</editor-fold>

    //<editor-fold desc="Check functions to determine the type of JSONEntry youre working with">

    public boolean isDefined() {
        return _jsonValue != null;
    }

    public boolean isBoolean() {
        try {
            asBoolean();
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean isNumber() {
        try {
            asNumber();
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean isString() {
        try {
            asString();
            return _jsonValue != null;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean isArray() {
        return _jsonValue != null && _jsonValue instanceof ScriptObjectMirror && ((ScriptObjectMirror)_jsonValue).isArray();
    }
    //</editor-fold>

    //<editor-fold desc="Primitive Data Types">
    private Number asNumber() {
        return (Number)_jsonValue;
    }

    public byte asByte() {
        return asNumber().byteValue();
    }

    public short asShort() {
        return asNumber().shortValue();
    }

    public int asInt() {
        return asNumber().intValue();
    }

    public long asLong() {
        return asNumber().longValue();
    }

    public float asFloat() {
        return asNumber().floatValue();
    }

    public double asDouble() {
        return asNumber().doubleValue();
    }

    public boolean asBoolean() {
        if(!isDefined()) throw new RuntimeException("Attempt to convert null to a boolean. Try calling isDefined() before checking type.");
        return (boolean)_jsonValue;
    }

    public char asChar() {
        try {
            return (char)asShort();
        } catch (ClassCastException e) {
            return asString().charAt(0);
        }
    }

    public String asString() {
        return (String)_jsonValue;
    }
    //</editor-fold>

    //<editor-fold desc="Primitive Data Array Types">
    private Number[] asNumberArray() {
        return asTArray(Number.class);
    }

    public byte[] asByteArray() {
        return (byte[]) ArrayUtils.copyToPrimitiveArray(asNumberArray(), Byte.class);
    }

    public short[] asShortArray() {
        return (short[])ArrayUtils.copyToPrimitiveArray(asNumberArray(), Short.class);
    }

    public int[] asIntArray() {
        return (int[])ArrayUtils.copyToPrimitiveArray(asNumberArray(), Integer.class);
    }

    public long[] asLongArray() {
        return (long[])ArrayUtils.copyToPrimitiveArray(asNumberArray(), Long.class);
    }

    public float[] asFloatArray() {
        return (float[])ArrayUtils.copyToPrimitiveArray(asNumberArray(), Float.class);
    }

    public double[] asDoubleArray() {
        return (double[])ArrayUtils.copyToPrimitiveArray(asNumberArray(), Double.class);
    }

    public boolean[] asBooleanArray() {
        return (boolean[])ArrayUtils.copyToPrimitiveArray(asTArray(Boolean.class), Boolean.class);
    }

    public char[] asCharArray() {
        return (char[])ArrayUtils.copyToPrimitiveArray(asTArray(Object.class), Character.class);
    }

    public String[] asStringArray() {
        return asTArray(String.class);
    }
    //</editor-fold>

    private <T> T[] asTArray(Class<T> type) {
        return asTArray(type, true);
    }

    @SuppressWarnings("unchecked")
    private <T> T[] asTArray(Class<T> type, boolean useJsonValue) {
        if(_jsonValue == null) return null;
        T[] array = (T[])Array.newInstance(type, 2048);
        JSONEntry current;
        int count = 0;
        //If the value at the current index is null, check the next index and make sure that isn't null, if it is defined we aren't done yet.
        while((current = getEntry(String.format("%d", count))).isDefined() || getEntry(String.format("%d", count + 1)).isDefined()) {
            array[count] = useJsonValue ? (T)current._jsonValue : (T)current;
            count++;
        }
        array = Arrays.copyOf(array, count);
        return array;
    }

}
