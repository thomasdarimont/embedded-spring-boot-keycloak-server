package com.github.thomasdarimont.keycloak.graal;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.xnio.management.XnioProviderMXBean;
import org.xnio.management.XnioServerMXBean;
import org.xnio.management.XnioWorkerMXBean;
import sun.misc.Unsafe;

import java.io.Closeable;
import java.lang.reflect.Field;

public class GraalPatches {
}

@TargetClass(className = "org.xnio.Xnio")
final class Target_org_xnio_Xnio {

    @Substitute
    protected static Closeable register(XnioProviderMXBean providerMXBean) {
        return null;
    }

    @Substitute
    protected static Closeable register(XnioWorkerMXBean workerMXBean) {
        return null;
    }

    @Substitute
    protected static Closeable register(XnioServerMXBean serverMXBean) {
        return null;
    }
}

@TargetClass(className = "org.xnio.Xnio$MBeanCloseable")
final class Target_org_xnio_Xnio_MBeanCloseable {

    @Substitute
    public void close() {
        // NOOP
    }
}

@TargetClass(className = "org.jboss.marshalling.reflect.SerializableField")
final class Target_org_jboss_marshalling_reflect_SerializableField {

    @Alias
    static Unsafe unsafe;

    @Alias
    private Field field;

    @Substitute
    public void setBoolean(Object instance, boolean value) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != boolean.class) {
            throw new ClassCastException();
        }
        unsafe.putBoolean(instance, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Set the char value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @param value    the value to set
     * @throws ClassCastException       if {@code instance} or the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public void setChar(Object instance, char value) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != char.class) {
            throw new ClassCastException();
        }
        unsafe.putChar(instance, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Set the byte value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @param value    the value to set
     * @throws ClassCastException       if {@code instance} is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public void setByte(Object instance, byte value) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != byte.class) {
            throw new ClassCastException();
        }
        unsafe.putByte(instance, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Set the short value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @param value    the value to set
     * @throws ClassCastException       if {@code instance} or the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public void setShort(Object instance, short value) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != short.class) {
            throw new ClassCastException();
        }
        unsafe.putShort(instance, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Set the integer value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @param value    the value to set
     * @throws ClassCastException       if {@code instance} or the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public void setInt(Object instance, int value) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != int.class) {
            throw new ClassCastException();
        }
        unsafe.putInt(instance, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Set the long value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @param value    the value to set
     * @throws ClassCastException       if {@code instance} or the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public void setLong(Object instance, long value) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != long.class) {
            throw new ClassCastException();
        }
        unsafe.putLong(instance, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Set the float value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @param value    the value to set
     * @throws ClassCastException       if {@code instance} or the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public void setFloat(Object instance, float value) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != float.class) {
            throw new ClassCastException();
        }
        unsafe.putFloat(instance, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Set the double value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @param value    the value to set
     * @throws ClassCastException       if {@code instance} or the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public void setDouble(Object instance, double value) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != double.class) {
            throw new ClassCastException();
        }
        unsafe.putDouble(instance, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Set the object value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @param value    the value to set
     * @throws ClassCastException       if {@code instance} or the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public void setObject(Object instance, Object value) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        final Class<?> fieldType = field.getType();
        if (fieldType.isPrimitive()) {
            throw new ClassCastException();
        }
        fieldType.cast(value);
        unsafe.putObject(instance, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Get the boolean value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @return the value of the field
     * @throws ClassCastException       if the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public boolean getBoolean(Object instance) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != boolean.class) {
            throw new ClassCastException();
        }
        return unsafe.getBoolean(instance, unsafe.objectFieldOffset(field));
    }

    /**
     * Get the char value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @return the value of the field
     * @throws ClassCastException       if the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public char getChar(Object instance) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != char.class) {
            throw new ClassCastException();
        }
        return unsafe.getChar(instance, unsafe.objectFieldOffset(field));
    }

    /**
     * Get the byte value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @return the value of the field
     * @throws ClassCastException       if the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public byte getByte(Object instance) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != byte.class) {
            throw new ClassCastException();
        }
        return unsafe.getByte(instance, unsafe.objectFieldOffset(field));
    }

    /**
     * Get the short value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @return the value of the field
     * @throws ClassCastException       if the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public short getShort(Object instance) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != short.class) {
            throw new ClassCastException();
        }
        return unsafe.getShort(instance, unsafe.objectFieldOffset(field));
    }

    /**
     * Get the integer value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @return the value of the field
     * @throws ClassCastException       if the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public int getInt(Object instance) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != int.class) {
            throw new ClassCastException();
        }
        return unsafe.getInt(instance, unsafe.objectFieldOffset(field));
    }

    /**
     * Get the long value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @return the value of the field
     * @throws ClassCastException       if the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public long getLong(Object instance) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != long.class) {
            throw new ClassCastException();
        }
        return unsafe.getLong(instance, unsafe.objectFieldOffset(field));
    }

    /**
     * Get the float value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @return the value of the field
     * @throws ClassCastException       if the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public float getFloat(Object instance) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != float.class) {
            throw new ClassCastException();
        }
        return unsafe.getFloat(instance, unsafe.objectFieldOffset(field));
    }

    /**
     * Get the double value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @return the value of the field
     * @throws ClassCastException       if the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public double getDouble(Object instance) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType() != double.class) {
            throw new ClassCastException();
        }
        return unsafe.getDouble(instance, unsafe.objectFieldOffset(field));
    }

    /**
     * Get the object value of this field on the given object instance.
     *
     * @param instance the object instance (must not be {@code null}, must be of the correct type)
     * @return the value of the field
     * @throws ClassCastException       if the field is not of the correct type
     * @throws IllegalArgumentException if this instance has no reflection field set on it
     */
    @Substitute
    public Object getObject(Object instance) throws ClassCastException, IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance is null");
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        field.getDeclaringClass().cast(instance);
        if (field.getType().isPrimitive()) {
            throw new ClassCastException();
        }
        return unsafe.getObject(instance, unsafe.objectFieldOffset(field));
    }
}