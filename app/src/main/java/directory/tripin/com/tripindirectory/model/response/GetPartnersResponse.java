package directory.tripin.com.tripindirectory.model.response;



import java.util.ArrayList;

import directory.tripin.com.tripindirectory.factory.Response;

/**
 * Created by Yogesh N. Tikam on 12/09/2017.
 */

public class GetPartnersResponse implements Response {

    private ArrayList<PartnersData> data;

    public ArrayList<PartnersData> getData() {
        return data;
    }

    public void setData(ArrayList<PartnersData> data) {
        this.data = data;
    }

    public class PartnersData {

        private String _id;
        private String name;
        private String address;

       private ContactData contact;

        public class ContactData {
            private String name;
            private String contact;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getContact() {
                return contact;
            }

            public void setContact(String contact) {
                this.contact = contact;
            }
        }

        private String mobile;
        private String score;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public ContactData getContact() {
            return contact;
        }

        public void setContact(ContactData contact) {
            this.contact = contact;
        }
    }
}
