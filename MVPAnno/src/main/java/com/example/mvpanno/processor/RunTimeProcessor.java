package com.example.mvpanno.processor;

import com.example.mvpanno.anno.GetMVPCom;
import com.example.mvpanno.anno.MVPAnno;
import com.example.mvpanno.anno.MVPAnnoType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RunTimeProcessor {

    private static HashMap<String, HashMap<MVPAnnoType, Object>> bussinessMap = new HashMap<>();

    private static void registerMVPComponent(Object clz) {
        if (clz == null || clz.getClass() == null) {
            System.out.println("registerMVPComponent failed");
            return;
        }

        MVPAnno anno = clz.getClass().getAnnotation(MVPAnno.class);
        if (anno != null) {
            String bussiness = anno.bussiness();
            MVPAnnoType type = anno.type();
            if (!bussinessMap.containsKey(bussiness)) {
                bussinessMap.put(bussiness, new HashMap<MVPAnnoType, Object>());
            }
            HashMap<MVPAnnoType, Object> map = bussinessMap.get(bussiness);
            map.put(type, clz);
            injectMVPComponent(clz);
        }
    }

    private static void injectMVPComponent(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields){
            GetMVPCom com = field.getAnnotation(GetMVPCom.class);
            if (com != null) {
                String bussiness = com.bussiness();
                MVPAnnoType type = com.type();
                if (bussinessMap.containsKey(bussiness)) {
                    Map<MVPAnnoType, Object> map = bussinessMap.get(bussiness);
                    if (map.containsKey(type)) {
                        Object ins = map.get(type);
                        try {
                            Constructor c = ins.getClass().getConstructor((Class<?>[]) null);
                            c.setAccessible(true);
                            c.newInstance((Object[]) null);
                            field.setAccessible(true);
                            field.set(object, ins);
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e0) {
                            e0.printStackTrace();
                            System.out.println(object.getClass().getName() + " component inject failed!"
                                    + "IllegalAccessException");
                        }
                    }
                }
                System.out.println(object.getClass().getName() + " component inject failed");
            }
        }
    }

    public static void init(Object clz) {
        try {
            injectCom(clz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("runtime processor did not find support class");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println("runtime processor did not find get method");
        }
    }

    @SuppressWarnings("unchecked")
    private static void injectCom(Object obj) throws ClassNotFoundException, NoSuchMethodException {
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return;
        }
        for (Field f : fields) {
            GetMVPCom com = f.getAnnotation(GetMVPCom.class);
            if (com != null) {
                MVPAnnoType type = com.type();
                String bussiness = com.bussiness();
                Class support = Class.forName("annotationlearn.Support$MVPAnno");
                Method getClass = support.getMethod("getClass", String.class, MVPAnnoType.class);
                String clzName = null;
                try {
                    clzName = (String) getClass.invoke(null, bussiness, type);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                try {
                    Class needInject = Class.forName(clzName);
                    Constructor c = needInject.getConstructor((Class<?>[]) null);
                    c.setAccessible(true);
                    Object ins = c.newInstance((Object[]) null);
                    f.setAccessible(true);
                    f.set(obj, ins);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e0) {
                    e0.printStackTrace();
                    System.out.println(clzName + " component inject failed!"
                            + "IllegalAccessException");
                }
            }
        }
    }
}
