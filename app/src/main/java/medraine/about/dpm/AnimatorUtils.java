package medraine.about.dpm;

/**
 * Created by Raine on 09.03.2017.
 */
import android.animation.PropertyValuesHolder;

public class AnimatorUtils {

    private static final String ROTATION = "rotation";
    private static final String SCALE_X = "scaleX";
    private static final String SCALE_Y = "scaleY";
    private static final String TRANSLATION_X = "translationX";
    private static final String TRANSLATION_Y = "translationY";

    private AnimatorUtils() {
        //No instances.
    }

    public static PropertyValuesHolder rotation(float... values) {
        return PropertyValuesHolder.ofFloat(ROTATION, values);
    }

    public static PropertyValuesHolder translationX(float... values) {
        return PropertyValuesHolder.ofFloat(TRANSLATION_X, values);
    }

    public static PropertyValuesHolder translationY(float... values) {
        return PropertyValuesHolder.ofFloat(TRANSLATION_Y, values);
    }

    public static PropertyValuesHolder scaleX(float... values) {
        return PropertyValuesHolder.ofFloat(SCALE_X, values);
    }

    public static PropertyValuesHolder scaleY(float... values) {
        return PropertyValuesHolder.ofFloat(SCALE_Y, values);
    }
}