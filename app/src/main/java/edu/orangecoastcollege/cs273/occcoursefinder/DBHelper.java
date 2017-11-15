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

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

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

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          int oldVersion,
                          int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + COURSES_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + INSTRUCTORS_TABLE);
        //TODO:  Drop the Offerings table
        onCreate(database);
    }

    //********** COURSE TABLE OPERATIONS:  ADD, GETALL, EDIT, DELETE

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

    public void deleteCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        // DELETE THE TABLE ROW
        db.delete(COURSES_TABLE, COURSES_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(course.getId())});
        db.close();
    }

    public void deleteAllCourses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(COURSES_TABLE, null, null);
        db.close();
    }

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

    public void deleteInstructor(Instructor instructor) {
        SQLiteDatabase db = this.getWritableDatabase();

        // DELETE THE TABLE ROW
        db.delete(INSTRUCTORS_TABLE, INSTRUCTORS_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(instructor.getId())});
        db.close();
    }

    public void deleteAllInstructors() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INSTRUCTORS_TABLE, null, null);
        db.close();
    }

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
    //TODO:  Create the following methods: addOffering, getAllOfferings, deleteOffering
    //TODO:  deleteAllOfferings, updateOffering, and getOffering
    //TODO:  Use the Courses and Instructors methods above as a guide.
    public void addOffering(Offering offering) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_CRN, offering.getCRN());
        values.put(FIELD_SEMESTER_CODE, offering.getSemesterCode());
        values.put(FIELD_COURSE_ID, offering.getCourse().getId());
        values.put(FIELD_INSTRUCTOR_ID, offering.getInstructor().getId());

        db.insert(OFFERINGS_TABLE, null, values);

        db.close();
    }
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
    public void deleteOffering(Offering offering) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OFFERINGS_TABLE, FIELD_CRN + " = ?",
                new String[]{String.valueOf(offering.getCRN())});
        db.close();
    }
    public void deleteAllOfferings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OFFERINGS_TABLE, null, null);
        db.close();
    }
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
    //TODO:  Write the code for the import OfferingsFromCSV method.
    public boolean importOfferingsFromCSV(String csvFile) {
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
                int crn = fields[1].trim();
                int semesterCode = fields[2].trim();
                int course = fields[3].trim();
                int instructor = fields[4].trim();
                addOffering(new Offering(crn, semesterCode, getCourse(course)));
            }
        }
    }

    public boolean importCoursesFromCSV(String csvFileName) {
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

    public boolean importInstructorsFromCSV(String csvFileName) {
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
