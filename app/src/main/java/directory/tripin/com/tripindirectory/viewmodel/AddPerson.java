package directory.tripin.com.tripindirectory.viewmodel;

import android.content.Context;
import android.widget.EditText;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 14-12-2017
 */

public class AddPerson {
    private EditText personName;
    private EditText pesonContact;

    public AddPerson(Context context) {
        this.personName = new EditText(context);
        this.pesonContact = new EditText(context);
    }

    public EditText getPersonName() {
        return personName;
    }

    public void setPersonName(EditText personName) {
        this.personName = personName;
    }

    public EditText getPesonContact() {
        return pesonContact;
    }

    public void setPesonContact(EditText pesonContact) {
        this.pesonContact = pesonContact;
    }
}
