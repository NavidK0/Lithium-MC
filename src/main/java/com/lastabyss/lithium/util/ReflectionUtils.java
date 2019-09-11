package com.lastabyss.lithium.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.bukkit.Bukkit;
import org.spigotmc.SneakyThrow;

public class ReflectionUtils {
	public static String getVersion() {
		final String name = Bukkit.getServer().getClass().getPackage().getName();
		final String version = name.substring(name.lastIndexOf('.') + 1) + ".";
		return version;
	}

	public static Class<?> getNMSClass(String className) {
		final String fullName = "net.minecraft.server." + getVersion() + className;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(fullName);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}

	public static Class<?> getOBCClass(String className) {
		final String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(fullName);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}

	public static Object getHandle(Object obj) {
		try {
			return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			final Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
		for (final Method m : clazz.getMethods()) {
			if (m.getName().equals(name) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equal = true;
		if (l1.length != l2.length) return false;
		for (int i = 0; i < l1.length; i++) {
			if (l1[i] != l2[i]) {
				equal = false;
				break;
			}
		}
		return equal;
	}

	/**
	 * Sets final field to the provided value
	 *
	 * @param clazz
	 * @param fieldName
	 * @param newValue
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setStaticFinalField(Class<?> clazz, String fieldName, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setFinalField(clazz.getDeclaredField(fieldName), null, newValue);
	}

	/**
	 * Sets final field to the provided value
	 *
	 * @param field - the field which should be modified
	 * @param obj - the object whose field should be modified
	 * @param newValue - the new value for the field of obj being modified
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setFinalField(Field field, Object obj, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ReflectionUtils.setAccessible(Field.class.getDeclaredField("modifiers")).setInt(field, field.getModifiers() & ~Modifier.FINAL);
		ReflectionUtils.setAccessible(Field.class.getDeclaredField("root")).set(field, null);
		ReflectionUtils.setAccessible(Field.class.getDeclaredField("overrideFieldAccessor")).set(field, null);
		ReflectionUtils.setAccessible(field).set(obj, newValue);
	}

	/**
	 * Gets field reflectively
	 *
	 * @param clazz
	 * @param fieldName
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Class<?> clazz, String fieldName, Object obj) {
		try {
			return (T) ReflectionUtils.setAccessible(clazz.getDeclaredField(fieldName)).get(obj);
		} catch (Throwable t) {
			SneakyThrow.sneaky(t);
		}
		return null;
	}

	public static <T> T getConstructor(Class<?> clazz, Object obj, Class<?>... paramters) {
		try {
			return (T) ReflectionUtils.setAccessible(clazz.getDeclaredConstructor(paramters));
		} catch (Throwable t) {
			SneakyThrow.sneaky(t);
		}
		return null;
	}

	/**
	 * Sets accessibleobject accessible state an returns this object
	 *
	 * @param <T>
	 * @param object
	 * @return
	 */
	public static <T extends AccessibleObject> T setAccessible(T object) {
		object.setAccessible(true);
		return object;
	}

	/**
	 * Sets field reflectively
	 *
	 * @param clazz
	 * @param fieldName
	 * @param obj
	 * @param value
	 */
	public static void setFieldValue(Class<?> clazz, String fieldName, Object obj, Object value) {
		try {
			setAccessible(clazz.getDeclaredField(fieldName)).set(obj, value);
		} catch (Throwable t) {
			SneakyThrow.sneaky(t);
		}
	}
}