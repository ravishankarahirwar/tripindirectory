package directory.tripin.com.tripindirectory.NewLookCode

import java.io.Serializable


data class BasicQueryPojo (var mSourceCity:String = "",
                           var mDestinationCity:String = "",
                           var mFleets: ArrayList<String>?) : Serializable