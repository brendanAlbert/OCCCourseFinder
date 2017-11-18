package edu.orangecoastcollege.cs273.occcoursefinder;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DBHelper is a helper model class that performs most of the functions related
 * to persisting data.
 *
 * The importCoursesFromCSV, importInstructorsFromCSV and importOfferingsFromCSV methods
 * convert the data from CSV files into rows in tables of the  SQLite database.
 *
 * There are also getter and setter functions for the various object types,
 * Courses, Instructors and Offerings, being stored in our OCC database.
 */
class DBHelper extends SQLiteOpenHelper {

    private Context mContext;

    //TASK: DEFINE THE DATABASE VERSION AND NAME  (DATABASE CONTAINS MULTIPLE TABLES)
    static final String DATABASE_NAME = "OCC";
    private static final int DATABASE_VERSION = 1;

    //TASK: DEFINE THE FIELDS (COLUMN NAMES) FOR THE COURSES TABLE
    private static final String COURSES_TABLE = "Courses";
    private static final String COURSES_KEY_FIELD_ID = "_id";
    private static final String FIELD_ALPHA = "alpha";
    private static final String FIELD_NUMBER = "number";
    private static final String FIELD_TITLE = "title";

    //TASK: DEFINE THE FIELDS (COLUMN NAMES) FOR THE INSTRUCTORS TABLE
    private static final String INSTRUCTORS_TABLE = "Instructors";
    private static final String INSTRUCTORS_KEY_FIELD_ID = "_id";
    private static final String FIELD_FIRST_NAME = "first_name";
    private static final String FIELD_LAST_NAME = "last_name";
    private static final String FIELD_EMAIL = "email";

    //TASK: DEFINE THE FIELDS (COLUMN NAMES) FOR THE OFFERINGS TABLE
    private static final String OFFERINGS_TABLE = "Offerings";
    private static final String FIELD_CRN = "crn";
    private static final String FIELD_SEMESTER_CODE = "semester_code";
    private static final String FIELD_COURSE_ID = "course_id";
    private static final String FIELD_INSTRUCTOR_ID = "instructor_id";

    /**
     * DBHelper is a parameterized constructor that accepts a Context argument.
     * This Context is passed to the super constructor.
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * onCreate is one of two methods which must be implemented when extending SQLiteOpenHelper.
     *
     * In this method we use SQL queries to build the various tables in the database.
     *
     * The new and interesting aspect of this project is how to make our database relational.
     * We will relate Courses to Instructors using a third table, Offerings.
     * In the Offerings table, a single entry will have two foreign keys.  These foreign keys
     * correspond to the primary keys of the Course and Instructor being related.
     * @param database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        String createQuery = "CREATE TABLE " + COURSES_TABLE + "("
                + COURSES_KEY_FIELD_ID + " INTEGER PRIMARY KEY, "
                + FIELD_ALPHA + " TEXT, "
                + FIELD_NUMBER + " TEXT, "
                + FIELD_TITLE + " TEXT" + ")";
        database.execSQL(createQuery);

        createQuery = "CREATE TABLE " + INSTRUCTORS_TABLE + "("
                + INSTRUCTORS_KEY_FIELD_ID + " INTEGER PRIMARY KEY, "
                + FIELD_FIRST_NAME + " TEXT, "
                + FIELD_LAST_NAME + " TEXT, "
                + FIELD_EMAIL + " TEXT" + ")";
        database.execSQL(createQuery);

        //TODO:  Write the query to create the relationship table "Offerings"
        //TODO:  Make sure to include foreign keys to the Courses and Instructors tables
        createQuery = "CREATE TABLE " + OFFERINGS_TABLE + "("
                + FIELD_CRN + " INTEGER, "
                + FIELD_SEMESTER_CODE + " INTEGER, "
                + FIELD_COURSE_ID + " INTEGER, "
                + FIELD_INSTRUCTOR_ID + " INTEGER, "
                + "FOREIGN KEY ( " + FIELD_COURSE_ID
                + " ) REFERENCES " + COURSES_TABLE + " ( " + COURSES_KEY_FIELD_ID + " ), "
                + "FOREIGN KEY ( " + FIELD_INSTRUCTOR_ID
                + " ) REFERENCES " + INSTRUCTORS_TABLE + " ( " + INSTRUCTORS_KEY_FIELD_ID + " ) )";
        database.execSQL(createQuery);
    }

    /**
     * onUpgrade is the second of two methods which must be implemented
     * when extending SQLiteOpenHelper.
     *
     * onUpgrade drops the previous tables if they exist and calls onCreate
     * to rebuild the upgraded tables.
     * @param database
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase database,
                          int oldVersion,
                          int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + COURSES_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + INSTRUCTORS_TABLE);
        //COMPLETED:  Drop the Offerings table
        database.execSQL("DROP TABLE IF EXISTS " + OFFERINGS_TABLE);
        onCreate(database);
    }

    //********** COURSE TABLE OPERATIONS:  ADD, GETALL, EDIT, DELETE

    /**
     * addCourse adds a, you guessed it, course!
     * A writable reference to the database is acquired,
     * a ContentValues object is instantiated and values from the course
     * argument are put into it.
     *
     * This ContentValues object is then inserted into the database.
     * Finally the database is closed.
     * @param course
     */
    public void addCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_ALPHA, course.getAlpha());
        values.put(FIELD_NUMBER, course.getNumber());
        values.put(FIELD_TITLE, course.getTitle());

        db.insert(COURSES_TABLE, null, values);

        // CLOSE THE DATABASE CONNECTION
        db.close();
    }

    /**
     * getAllCourses returns a list of Courses from the database, which was
     * populated from the courses csv file.
     * @return List<Course>
     */
    public List<Course> getAllCourses() {
        List<Course> coursesList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(
                COURSES_TABLE,
                new String[]{COURSES_KEY_FIELD_ID, FIELD_ALPHA, FIELD_NUMBER, FIELD_TITLE},
                null,
                null,
                null, null, null, null);

        //COLLECT EACH ROW IN THE TABLE
        if (cursor.moveToFirst()) {
            do {
                Course course =
                        new Course(cursor.getLong(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3));
                coursesList.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return coursesList;
    }

    /**
     * deleteCourse takes a Course argument.
     * This course is deleted from the database based on its id.
     * Then close the db.
     * @param course
     */
    public void deleteCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        // DELETE THE TABLE ROW
        db.delete(COURSES_TABLE, COURSES_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(course.getId())});
        db.close();
    }

    /**
     * deleteAllCourses() gets a writable reference to the database.
     * Then all of the entries in the Courses table are deleted/dropped, but the table
     * is intact, it is just empty.
     * We then close the db.
     */
    public void deleteAllCourses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(COURSES_TABLE, null, null);
        db.close();
    }

    /**
     * updateCourse takes a Course argument.
     * Using this argument, a new ContentValues object is populated.
     * Then this ContentValues object is used to update the respective entry in the
     * Courses table.
     * Closes the db.
     * @param course
     */
    public void updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_ALPHA, course.getAlpha());
        values.put(FIELD_NUMBER, course.getNumber());
        values.put(FIELD_TITLE, course.getTitle());

        db.update(COURSES_TABLE, values, COURSES_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(course.getId())});
        db.close();
    }

    /**
     * getCourse returns a Course based on the long id argument.
     *
     *
     * A readable database reference is acquired and a Cursor object is populated.
     * The Cursor is populated using the information from the database of the Course associated
     * with the id argument.
     *
     * Using the Cursor we instantiate a new Course object.
     * Both the cursor and database are closed.
     * Return the new Course.
     * @param id of the course to be retrieved.
     * @return
     */
    public Course getCourse(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                COURSES_TABLE,
                new String[]{COURSES_KEY_FIELD_ID, FIELD_ALPHA, FIELD_NUMBER, FIELD_TITLE},
                COURSES_KEY_FIELD_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Course course = new Course(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3));

        cursor.close();
        db.close();
        return course;
    }


    //********** INSTRUCTOR TABLE OPERATIONS:  ADD, GETALL, EDIT, DELETE

    /**
     * addInstructor adds an instructor to the database using the data provided from the
     * Instructor argument passed in.
     * @param instructor
     */
    public void addInstructor(Instructor instructor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_LAST_NAME, instructor.getLastName());
        values.put(FIELD_FIRST_NAME, instructor.getFirstName());
        values.put(FIELD_EMAIL, instructor.getEmail());

        db.insert(INSTRUCTORS_TABLE, null, values);

        // CLOSE THE DATABASE CONNECTION
        db.close();
    }

    /**
     * getAllInstructors returns a List of all Instructors in the OCC database.
     * @return
     */
    public List<Instructor> getAllInstructors() {
        List<Instructor> instructorsList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(
                INSTRUCTORS_TABLE,
                new String[]{INSTRUCTORS_KEY_FIELD_ID, FIELD_LAST_NAME, FIELD_FIRST_NAME, FIELD_EMAIL},
                null,
                null,
                null, null, null, null);

        //COLLECT EACH ROW IN THE TABLE
        if (cursor.moveToFirst()) {
            do {
                Instructor instructor =
                        new Instructor(cursor.getLong(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3));
                instructorsList.add(instructor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return instructorsList;
    }

    /**
     * deleteInstructor takes an Instructor argument.  The Instructor table of the database is
     * searched for an instructor matching the provided argument and that entry is deleted.
     * @param instructor
     */
    public void deleteInstructor(Instructor instructor) {
        SQLiteDatabase db = this.getWritableDatabase();

        // DELETE THE TABLE ROW
        db.delete(INSTRUCTORS_TABLE, INSTRUCTORS_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(instructor.getId())});
        db.close();
    }

    /**
     * deleteAllInstructors() deletes/drops all Instructors from the Instructors table of the
     * OCC database.
     */
    public void deleteAllInstructors() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INSTRUCTORS_TABLE, null, null);
        db.close();
    }

    /**
     * updateInstructor takes an Instructor argument.  The Instructors table is searched for
     * an existing Instructor that matches the argument, and updates the existing Instructors
     * information to match that of the provided Instructor argument.
     * @param instructor
     */
    public void updateInstructor(Instructor instructor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_FIRST_NAME, instructor.getFirstName());
        values.put(FIELD_LAST_NAME, instructor.getLastName());
        values.put(FIELD_EMAIL, instructor.getEmail());

        db.update(INSTRUCTORS_TABLE, values, INSTRUCTORS_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(instructor.getId())});
        db.close();
    }

    /**
     * getInstructor accepts a long as an argument.  An Instructor matching that id
     * is Instantiated and returned using the data retrieved from the database.
     * @param id
     * @return
     */
    public Instructor getInstructor(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                INSTRUCTORS_TABLE,
                new String[]{INSTRUCTORS_KEY_FIELD_ID, FIELD_LAST_NAME, FIELD_FIRST_NAME, FIELD_EMAIL},
                INSTRUCTORS_KEY_FIELD_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Instructor instructor = new Instructor(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3));

        cursor.close();
        db.close();
        return instructor;
    }


    //********** OFFERING TABLE OPERATIONS:  ADD, GETALL, EDIT, DELETE
    //COMPLETED:  Create the following methods: addOffering, getAllOfferings, deleteOffering
    //COMPLETED:  deleteAllOfferings, updateOffering, and getOffering
    //COMPLETED:  Use the Courses and Instructors methods above as a guide.

    /**
     * addOffering accepts four arguments.  A Course CRN number, a semester code,
     * the id of the course and the id of the instructor.
     *
     * The provided arguments are put into a ContentValues object and inserted into the db.
     *
     * @param crn this is unique identifier for a Course
     * @param semesterCode this is the semester in which the course is being taught
     * @param courseId id of the course
     * @param instructorId id of the instructor
     */
    public void addOffering(int crn, int semesterCode, long courseId, long instructorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_CRN, crn);
        values.put(FIELD_SEMESTER_CODE, semesterCode);
        values.put(FIELD_COURSE_ID, courseId);
        values.put(FIELD_INSTRUCTOR_ID, instructorId);

        db.insert(OFFERINGS_TABLE, null, values);

        db.close();
    }

    /**
     * getAllOfferings returns all Offerings in the db.
     * @return
     */
    public List<Offering> getAllOfferings() {
        List<Offering> offeringList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(
                OFFERINGS_TABLE,
                new String[]{FIELD_CRN, FIELD_SEMESTER_CODE, FIELD_COURSE_ID, FIELD_INSTRUCTOR_ID},
                null,
                null,
                null, null, null, null);

        if (cursor.moveToFirst()){
            do {
                Offering offering = new Offering((int) cursor.getLong(0),
                        (int) cursor.getLong(1),
                        getCourse(cursor.getLong(2)),
                        getInstructor(cursor.getLong(3)));
                offeringList.add(offering);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return offeringList;
    }

    /**
     * deleteOffering deletes the entry in the database matching the provided argument.
     * @param offering
     */
    public void deleteOffering(Offering offering) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OFFERINGS_TABLE, FIELD_CRN + " = ?",
                new String[]{String.valueOf(offering.getCRN())});
        db.close();
    }

    /**
     * deleteAllOfferings deletes all Offerings from the Offerings table in the database.
     */
    public void deleteAllOfferings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OFFERINGS_TABLE, null, null);
        db.close();
    }

    /**
     * updateOffering updates the entry in the Offerings table which matches the offering
     * argument.
     * @param offering
     */
    public void updateOffering(Offering offering){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_CRN, offering.getCRN());
        values.put(FIELD_SEMESTER_CODE, offering.getSemesterCode());
        values.put(FIELD_COURSE_ID, offering.getCourse().getId());
        values.put(FIELD_INSTRUCTOR_ID, offering.getInstructor().getId());

        db.update(OFFERINGS_TABLE, values, FIELD_CRN + " = ?",
                new String[]{String.valueOf(offering.getCRN())});
        db.close();
    }

    /**
     * getOffering returns an Offering object.  The Offering object is built from the
     * values of the entry stored in the table which matches the id argument.
     * @param id
     * @return
     */
    public Offering getOffering(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                OFFERINGS_TABLE,
                new String[]{FIELD_CRN, FIELD_SEMESTER_CODE, FIELD_COURSE_ID, FIELD_INSTRUCTOR_ID},
                FIELD_CRN + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Offering offering = new Offering(
                (int) cursor.getLong(0),
                (int) cursor.getLong(1),
                getCourse(cursor.getLong(2)),
                getInstructor(cursor.getLong(3)));
        cursor.close();
        db.close();
        return offering;
    }








    //********** IMPORT FROM CSV OPERATIONS:  Courses, Instructors and Offerings
    //COMPLETED:  Write the code for the import OfferingsFromCSV method.

    /**
     * importOfferingsFromCSV accepts a string argument which represents the name of the
     * csv file asset we are importing data from.
     *
     * Steps:
     * 1) Instantiate an AssetManager object by get(ting)Assets from the current context.
     * 2) Declare an inputStream.
     * 3) Using a try catch block, open the AssetManager object using the csvFile name argument
     * and store the resulting inputStream.
     * 4) Instantiate a BufferedReader object using a new InputStreamReader instantiated
     * using the inputStream as an argument.
     * 5) In a try catch block, while the BufferedReader object is not null, addOfferings
     * to the Offerings table using lines read from the BufferedReader.
     * 6) return true if successful
     * @param csvFile name of the csv file
     * @return true is successful
     */
    boolean importOfferingsFromCSV(String csvFile) {
        AssetManager manager = mContext.getAssets();
        InputStream inputStream;
        try {
            inputStream = manager.open(csvFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4) {
                    Log.d("OCC Course Finder", "Skipping Bad CSV Row: " + Arrays.toString(fields));
                    continue;
                }
                int crn = Integer.parseInt(fields[0].trim());
                int semesterCode = Integer.parseInt(fields[1].trim());
                Long courseId = Long.parseLong(fields[2].trim());
                Long instructorId = Long.parseLong(fields[3].trim());
                addOffering(crn, semesterCode, courseId, instructorId);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * importCoursesFromCSV accepts a string argument which represents the name of the
     * csv file asset we are importing data from.
     *
     * Steps:
     * 1) Instantiate an AssetManager object by get(ting)Assets from the current context.
     * 2) Declare an inputStream.
     * 3) Using a try catch block, open the AssetManager object using the csvFile name argument
     * and store the resulting inputStream.
     * 4) Instantiate a BufferedReader object using a new InputStreamReader instantiated
     * using the inputStream as an argument.
     * 5) In a try catch block, while the BufferedReader object is not null, addCourses
     * to the Courses table using lines read from the BufferedReader.
     * 6) return true if successful
     * @param csvFileName name of the csv file
     * @return true is successful
     */
    boolean importCoursesFromCSV(String csvFileName) {
        AssetManager manager = mContext.getAssets();
        InputStream inStream;
        try {
            inStream = manager.open(csvFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4) {
                    Log.d("OCC Course Finder", "Skipping Bad CSV Row: " + Arrays.toString(fields));
                    continue;
                }
                int id = Integer.parseInt(fields[0].trim());
                String alpha = fields[1].trim();
                String number = fields[2].trim();
                String title = fields[3].trim();
                addCourse(new Course(id, alpha, number, title));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * importInstructorsFromCSV accepts a string argument which represents the name of the
     * csv file asset we are importing data from.
     *
     * Steps:
     * 1) Instantiate an AssetManager object by get(ting)Assets from the current context.
     * 2) Declare an inputStream.
     * 3) Using a try catch block, open the AssetManager object using the csvFile name argument
     * and store the resulting inputStream.
     * 4) Instantiate a BufferedReader object using a new InputStreamReader instantiated
     * using the inputStream as an argument.
     * 5) In a try catch block, while the BufferedReader object is not null, addInstructors
     * to the Instructors table using lines read from the BufferedReader.
     * 6) return true if successful
     * @param csvFileName name of the csv file
     * @return true is successful
     */
    boolean importInstructorsFromCSV(String csvFileName) {
        AssetManager am = mContext.getAssets();
        InputStream inStream = null;
        try {
            inStream = am.open(csvFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4) {
                    Log.d("OCC Course Finder", "Skipping Bad CSV Row: " + Arrays.toString(fields));
                    continue;
                }
                int id = Integer.parseInt(fields[0].trim());
                String lastName = fields[1].trim();
                String firstName = fields[2].trim();
                String email = fields[3].trim();
                addInstructor(new Instructor(id, lastName, firstName, email));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
