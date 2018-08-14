package directory.tripin.com.tripindirectory.NewLookCode

import java.io.Serializable


data class BasicQueryPojo (var mSourceCity:String = "",
                           var mDestinationCity:String = "",
                           var mFleets: ArrayList<String>?) : Serializable
    {
        override fun toString(): String = "Fleet Requirement\n" +
                "Route: $mSourceCity to $mDestinationCity\n" +
                "Fleet: ${mFleets.toString()}"
    }
