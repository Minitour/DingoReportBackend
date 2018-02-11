package mobi.newsound.database;


import com.sun.tools.javac.util.Pair;

import java.util.Optional;

/**
 * Created by Antonio Zaitoun on 11/02/2018.
 */

public class Column<T> extends Pair<String,Object> {

    /**
     * Creates a new pair
     *
     * @param key   The key for this pair
     * @param value The value to use for this pair
     */
    public Column(String key, Object value) {
        super(key, value);
    }

    public Column(String key, Optional<T> value, Value<T> callback) {
        super(key, value.isPresent() ? callback.getValue(value.get()) : null);
    }

    public interface Value<T> {
        Object getValue(T data);
    }

    public boolean shouldIgnore(){
        return false;
    }

    public String getKey(){
        return fst;
    }

    public Object getValue(){
        return snd;
    }
}