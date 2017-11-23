package directory.tripin.com.tripindirectory.model.response;

import directory.tripin.com.tripindirectory.factory.Response;

/**
 * Created by Yogesh N. Tikam on 22/11/2017.
 */

public class LikeDislikeResponse implements Response {

    private LikeDislikeData data;

    public LikeDislikeData getData() {
        return data;
    }

    public void setData(LikeDislikeData data) {
        this.data = data;
    }

    public class LikeDislikeData {

        private String[] phone;

        private String[] userDisliked;

        private String[] inbox;

        private String __v;

        private String like;

        private String updatedAt;

        private String flag;

        private String _id;

        private String address;

        private String createdAt;

        private String description;

        private String name;

        private String dislike;

        private Lat_lng lat_lng;

        private String[] contactPerson;

        private Mobile[] mobile;

        private String[] userLiked;

        public String[] getPhone() {
            return phone;
        }

        public void setPhone(String[] phone) {
            this.phone = phone;
        }

        public String[] getUserDisliked() {
            return userDisliked;
        }

        public void setUserDisliked(String[] userDisliked) {
            this.userDisliked = userDisliked;
        }

        public String[] getInbox() {
            return inbox;
        }

        public void setInbox(String[] inbox) {
            this.inbox = inbox;
        }

        public String get__v() {
            return __v;
        }

        public void set__v(String __v) {
            this.__v = __v;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
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

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDislike() {
            return dislike;
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

        public String[] getContactPerson() {
            return contactPerson;
        }

        public void setContactPerson(String[] contactPerson) {
            this.contactPerson = contactPerson;
        }

        public Mobile[] getMobile() {
            return mobile;
        }

        public void setMobile(Mobile[] mobile) {
            this.mobile = mobile;
        }

        public String[] getUserLiked() {
            return userLiked;
        }

        public void setUserLiked(String[] userLiked) {
            this.userLiked = userLiked;
        }

        public class Mobile {
            private String cellNo;

            private String _id;

            private String name;

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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public class Lat_lng {
            private String type;

            private String[] coordinates;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String[] getCoordinates() {
                return coordinates;
            }

            public void setCoordinates(String[] coordinates) {
                this.coordinates = coordinates;
            }
        }
    }
}
