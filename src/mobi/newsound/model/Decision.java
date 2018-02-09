package mobi.newsound.model;

/**
 * Created by Antonio Zaitoun on 20/12/2017.
 */
public enum Decision {
    GUILTY(0),
    INNOCENT(1),
    UNDETERMINED(2);

    int value;
    Decision(int val){
        this.value = val;
    }

    public static Decision valueOf(int val){
        switch (val){
            case 0:
                return GUILTY;
            case 1:
                return INNOCENT;
            case 2:
                return UNDETERMINED;
        }
        return UNDETERMINED;
    }
}
