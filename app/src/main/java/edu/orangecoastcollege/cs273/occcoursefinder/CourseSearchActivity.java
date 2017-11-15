package edu.orangecoastcollege.cs273.occcoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class CourseSearchActivity extends AppCompatActivity {

    private DBHelper db;
    private static final String TAG = "OCC Course Finder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        //TODO: Create the method importOfferingsFromCSV, then use it in this activity.


        List<Course> allCourses = db.getAllCourses();
        for (Course course : allCourses)
            Log.i(TAG, course.toString());

        List<Instructor> allInstructors = db.getAllInstructors();
        for (Instructor instructor : allInstructors)
            Log.i(TAG, instructor.toString());

        //TODO: Get all the offerings from the database, then print them out to the Log


    }
}
