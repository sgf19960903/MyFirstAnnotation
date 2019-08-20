package lib.tzw.annotation_api;

import android.app.Activity;
import android.util.Log;

public class IdInjector {
    private static final String TAG = "IdInjector";
    private static final String SUFFIX = "$$IdInject";

    public static void injectBind(Activity activity){
        System.out.println("injectBind.............");
        IdInject inject = (IdInject) findProxyClass(activity);
        inject.inject(activity,activity);
    }

    public static void injectBind(Activity activity,Object obj){
        IdInject inject = (IdInject) findProxyClass(activity);
        inject.inject(activity,obj);
    }

    public static Object findProxyClass(Activity activity) {
        Class aClazz = activity.getClass();
        try {
            System.out.println("类名："+aClazz.getName()+SUFFIX);
            Class clazz = Class.forName(aClazz.getName()+SUFFIX);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("NotFound "+aClazz.getName()+SUFFIX+"!");
    }



}
