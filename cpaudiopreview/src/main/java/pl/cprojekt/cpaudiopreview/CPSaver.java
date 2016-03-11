package pl.cprojekt.cpaudiopreview;


import android.util.Log;

import java.lang.reflect.Field;

//todo
public class CPSaver {

    public static void save(Class cl, Object object) {

        Log.i("X", "nazwa klasy " + cl.getName());

        Field[] fields = cl.getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(CPSaveState.class))
                continue;

            try {
                Class t = field.getType();
                Log.i("X", "typ pola " + t.getName());
                field.get(object);
                //bundle.putSerializable(field.getName(), field.get(object));
                //bundle.put
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


            Log.d("X", "Pole: " + field.getName());
        }
    }

}
