package com.ldx.landingpage.utils;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * @author lidongxiu
 * getResources().getIdentifier("main_activity", "layout", getPackageName());
 */

public class XIResourceUtils {

    public static int getLayoutId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "layout", paramContext.getPackageName());
    }

    public static int getStringId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "string", paramContext.getPackageName());
    }

    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "drawable", paramContext.getPackageName());
    }

    public static int getStyleId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "style", paramContext.getPackageName());
    }

    public static int getStyleableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "styleable", paramContext.getPackageName());
    }

    public static int getId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "id", paramContext.getPackageName());
    }

    public static int getColorId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "color", paramContext.getPackageName());
    }

    public static int getOneAttrId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "attr", paramContext.getPackageName());
    }

    public static int[] getStyleableIntArray(Context context, String name) {
        try {
            Field[] fields = Class.forName(context.getPackageName() + ".R$styleable").getFields();//.与$ difference,$表示R的子类
            for (Field field : fields) {
                if (field.getName().equals(name)) {
                    int ret[] = (int[]) field.get(null);
                    return ret;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param context
     * @param styleableName
     * @param styleableFieldName
     * @return
     */
    public static int getStyleableFieldId(Context context, String styleableName, String styleableFieldName) {
        String className = context.getPackageName() + ".R";
        String type = "styleable";
        String name = styleableName + "_" + styleableFieldName;
        try {
            Class<?> cla = Class.forName(className);
            for (Class<?> childClass : cla.getClasses()) {
                String simpleName = childClass.getSimpleName();
                if (simpleName.equals(type)) {
                    for (Field field : childClass.getFields()) {
                        String fieldName = field.getName();
                        if (fieldName.equals(name)) {
                            return (int) field.get(null);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getStyleableIntArrayIndex(Context sContext, String name) {
        try {
            if (sContext == null)
                return 0;
            // use reflection to access the resource class
            Field field = Class.forName(sContext.getPackageName() + ".R$styleable").getDeclaredField(name);
            int ret = (Integer) field.get(null);
            return ret;
        } catch (Throwable t) {
        }
        return 0;
    }

    private static Object getResourceId(Context context, String name, String type) {

        String className = context.getPackageName() + ".R";

        try {

            Class<?> cls = Class.forName(className);

            for (Class<?> childClass : cls.getClasses()) {

                String simple = childClass.getSimpleName();

                if (simple.equals(type)) {

                    for (Field field : childClass.getFields()) {

                        String fieldName = field.getName();

                        if (fieldName.equals(name)) {

                            System.out.println(fieldName);

                            return field.get(null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * context.getResources().getIdentifier
     *
     * @param name
     * @return
     * @paramcontext
     */

    public static int getStyleable(Context context, String name) {

        return ((Integer) getResourceId(context, name, "styleable")).intValue();
    }

    /**
     *
     * @param name
     * @return
     * @paramcontext
     */

    public static int[] getStyleableArray(Context context, String name) {
        return (int[]) getResourceId(context, name, "styleable");
    }
}
