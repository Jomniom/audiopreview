package pl.cprojekt.cpaudiopreview;

import java.lang.reflect.Field;

//todo not use
public class CPSaver {

    public static void save(Class cl, Object object) {
        Field[] fields = cl.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(CPSaveState.class))
                continue;

            try {
                Class t = field.getType();
                //Log.i("X", "typ pola " + t.getName());
                field.get(object);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //Log.d("X", "Pole: " + field.getName());
        }
    }
}
