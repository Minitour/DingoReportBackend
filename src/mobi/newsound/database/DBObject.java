package mobi.newsound.database;

import com.google.gson.annotations.Expose;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created By Tony on 12/02/2018
 */
public class DBObject implements Mappable{

    /**
     * A hash map which holds a list of foreign keys.
     */
    final transient private Map<String,Object> foreignKeys = new HashMap<>();

    /**
     * Default Constructor
     */
    public DBObject(){ }

    /**
     * Constructor from map object.
     * @param map The map which contains the values.
     */
    public DBObject(Map<String,Object> map){
        Set<Field> fields = findFields(Expose.class);
        fields.forEach(field -> {
            try {
                field.setAccessible(true);
                field.set(this,map.get(field.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }catch (IllegalArgumentException e){
                //field is a foreign key!
                foreignKeys.put(field.getName(),map.get(field.getName()));
            }
        });
    }

    /**
     * Helper method which returns a list of fields that are annotated with a certain annotation.
     * @param ann
     * @return
     */
    private Set<Field> findFields(Class<? extends Annotation> ann) {
        Set<Field> set = new HashSet<>();
        Class<?> c = getClass();
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(ann)) {
                    set.add(field);
                }
            }
            c = c.getSuperclass();
        }
        return set;
    }

    /**
     * A method which returns a foreign key's value.
     * @param key The foreign key
     * @param <T> The type
     * @return The value if found else null.
     */
    public <T> T getForeignKey(String key){
        return (T) foreignKeys.get(key);
    }
}
