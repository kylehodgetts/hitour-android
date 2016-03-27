package stranders.hitour.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import stranders.hitour.R;


/**
 * The activity that displays a quiz that can be accessed at the end of a tour.
 */
public class QuizActivity extends AppCompatActivity {

    /**
     * Initializes and populates {@link QuizActivity}
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
    }

}
