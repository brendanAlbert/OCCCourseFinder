package edu.orangecoastcollege.cs273.occcoursefinder;

/**
 * The <code>Course</code> class represents a single course at Orange Coast College,
 * including its alpha (e.g. CS), number (e.g. A273) and title (e.g. Mobile Application Development)
 *
 * @author Michael Paulding
 */
public class Course {
    private long mId;
    private String mAlpha;
    private String mNumber;
    private String mTitle;

    public Course(long id, String alpha, String number, String title) {
        mId = id;
        mAlpha = alpha;
        mNumber = number;
        mTitle = title;
    }

    public Course(String alpha, String number, String title) {
        this(-1, alpha, number, title);
    }

    public long getId() {
        return mId;
    }

    void setId(int id)
    {
        mId = id;
    }

    public String getAlpha() {
        return mAlpha;
    }

    public void setAlpha(String alpha) {
        mAlpha = alpha;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public String toString() {
        return "Course{" +
                "Id=" + mId +
                ", Alpha='" + mAlpha + '\'' +
                ", Number='" + mNumber + '\'' +
                ", Title='" + mTitle + '\'' +
                '}';
    }
}
