package directory.tripin.com.tripindirectory.model.response;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.factory.Response;

/**
 * Created by Yogesh N. Tikam on 11/21/2017.
 */

public class ElasticSearchResponse implements Response {

    private ArrayList<PartnerData> data;

    public ArrayList<PartnerData> getData() {
        return data;
    }

    public void setData(ArrayList<PartnerData> data) {
        this.data = data;
    }
    public class PartnerData {

        private String[] phone;

        private String _id;

        private String address;

        private String name;

        private String like;

        private String dislike;

        private Lat_lng lat_lng;

        private Mobile[] mobile;
        private ArrayList<String> userLiked = new ArrayList<>();
        private ArrayList<String> userDisliked = new ArrayList<>();

        public String[] getPhone() {
            return phone;
        }

        public void setPhone(String[] phone) {
            this.phone = phone;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public String getDislike() {
            return dislike;
        }

        public ArrayList<String> getUserLiked() {
            return userLiked;
        }

        public void setUserLiked(ArrayList<String> userLiked) {
            this.userLiked = userLiked;
        }

        public ArrayList<String> getUserDisliked() {
            return userDisliked;
        }

        public void setUserDisliked(ArrayList<String> userDisliked) {
            this.userDisliked = userDisliked;
        }

        public void setDislike(String dislike) {
            this.dislike = dislike;
        }

        public Lat_lng getLat_lng() {
            return lat_lng;
        }

        public void setLat_lng(Lat_lng lat_lng) {
            this.lat_lng = lat_lng;
        }

        public Mobile[] getMobile() {
            return mobile;
        }

        public void setMobile(Mobile[] mobile) {
            this.mobile = mobile;
        }

        public class Mobile {
            private String cellNo;

            private String _id;

            public String getCellNo() {
                return cellNo;
            }

            public void setCellNo(String cellNo) {
                this.cellNo = cellNo;
            }

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }

        }

        public class Lat_lng {
            private String[] coordinates;

            public String[] getCoordinates() {
                return coordinates;
            }

            public void setCoordinates(String[] coordinates) {
                this.coordinates = coordinates;
            }

        }


    }

}
