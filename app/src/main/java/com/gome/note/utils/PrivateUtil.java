package com.gome.note.utils;



import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class PrivateUtil {

    public static Method getMethod(Class clazz, String methodName,
                                   final Class[] classes) throws Exception {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, classes);
            } catch (NoSuchMethodException ex) {
                if (clazz.getSuperclass() == null) {
                    return method;
                } else {
                    method = getMethod(clazz.getSuperclass(), methodName,
                            classes);
                }
            }
        }
        return method;
    }


    public static Object invoke(final Object obj, final String methodName,
                                final Class[] classes, final Object[] objects) {
        try {
            Method method = getMethod(obj.getClass(), methodName, classes);
            method.setAccessible(true);
            return method.invoke(obj, objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invoke2(final Object obj, final String methodName,
                                 final Class[] classes, final Object[] objects) {
        try {
            Method method = getMethod((Class) obj, methodName, classes);
            method.setAccessible(true);
            return method.invoke(obj, objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invoke(final Object obj, final String methodName,
                                final Class[] classes) {
        return invoke(obj, methodName, classes, new Object[]{});
    }

    public static Object invoke(final Object obj, final String methodName) {
        return invoke(obj, methodName, new Class[]{}, new Object[]{});
    }

    public static Field getField(Class clazz, String fieldName) throws Exception {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getField(fieldName);
            } catch (NoSuchFieldException ex) {
                if (clazz.getSuperclass() == null) {
                    return field;
                } else {
                    field = getField(clazz.getSuperclass(), fieldName);
                }
            }
        }
        return field;
    }

    public static Object invokeFieldValue(final Object object, final String fieldName) {
        try {
            Field field = getField(object.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void printAllFileds(Object obj) {
        if (obj != null) {
            try {
                Class cls = obj.getClass();
                Field[] field = cls.getDeclaredFields();
                for (Field f : field) {
                    f.setAccessible(true);
                }
            } catch (Exception e) {

            }
        }
    }

    public static void printAllMethods(Object obj) {
        if (obj != null) {
            try {
                Class cls = obj.getClass();
                Method[] method = cls.getDeclaredMethods();
                if (method != null) {
                    for (Method m : method) {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
