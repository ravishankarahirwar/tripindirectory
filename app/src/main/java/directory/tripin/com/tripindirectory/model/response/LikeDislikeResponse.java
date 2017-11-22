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
