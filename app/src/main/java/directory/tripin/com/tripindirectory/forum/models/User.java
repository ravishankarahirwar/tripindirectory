package directory.tripin.com.tripindirectory.forum.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String mobileno;
    public String firebaseToken;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String firebaseToken) {
        this.username = username;
        this.mobileno = email;
        this.firebaseToken = firebaseToken;
    }

    public User(String username, String email) {
        this.username = username;
        this.mobileno = email;
    }
}
// [END blog_user_class]
