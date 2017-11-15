package edu.orangecoastcollege.cs273.occcoursefinder;

/**
 * The <code>Instructor</code> class represents an individual instructor at Orange Coast College,
 * including the instructor's last name, first name and email address.
 *
 * @author Michael Paulding
 */
public class Instructor {
    private long mId;
    private String mLastName;
    private String mFirstName;
    private String mEmail;

    public Instructor(long id, String lastName, String firstName, String email) {
        mId = id;
        mLastName = lastName;
        mFirstName = firstName;
        mEmail = email;
    }

    public Instructor(String lastName, String firstName, String email) {
        this(-1, lastName, firstName, email);
    }

    public long getId() {
        return mId;
    }

    void setId(int id)
    {
        mId = id;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "Id=" + mId +
                ", LastName='" + mLastName + '\'' +
                ", FirstName='" + mFirstName + '\'' +
                ", Email='" + mEmail + '\'' +
                '}';
    }
}
