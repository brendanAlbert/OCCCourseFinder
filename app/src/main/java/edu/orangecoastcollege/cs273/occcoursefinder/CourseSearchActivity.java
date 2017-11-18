package edu.orangecoastcollege.cs273.occcoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

/**
 * OCCCourseFinder is a useful app that teaches how to take data from a csv file
 * and use it to populate an SQLite database.
 *
 * This app has no front end, all of the action occurs in the logcat.
 *
 * This class, CourseSearchActivity, has one method, onCreate.
 * In onCreate we set the content view.
 * Delete any previous database, import the data from the three csv files using their
 * respective methods in our DBHelper object, db.
 *
 * Then we populate three Lists, of Courses, Instructors and Offerings respectively.
 * Each list is then printed to the logcat.
 */
public class CourseSearchActivity extends AppCompatActivity {

    private DBHelper db;
    private static final String TAG = "OCC Course Finder";

    /**
     * In onCreate we set the content view.
     * Delete any previous database, import the data from the three csv files using their
     * respective methods in our DBHelper object, db.
     *
     * Then we populate three Lists, of Courses, Instructors and Offerings respectively.
     * Each list is then printed to the logcat.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        //TODO: Create the method importOfferingsFromCSV, then use it in this activity.
        db.importOfferingsFromCSV("offerings.csv");

        List<Course> allCourses = db.getAllCourses();
        for (Course course : allCourses)
            Log.i(TAG, course.toString());

        List<Instructor> allInstructors = db.getAllInstructors();
        for (Instructor instructor : allInstructors)
            Log.i(TAG, instructor.toString());

        //TODO: Get all the offerings from the database, then print them out to the Log
        List<Offering> allOfferings = db.getAllOfferings();
        for (Offering offering : allOfferings)
            Log.i(TAG, offering.toString());

    }
}
