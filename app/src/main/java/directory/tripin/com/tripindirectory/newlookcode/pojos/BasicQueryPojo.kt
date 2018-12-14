package directory.tripin.com.tripindirectory.newlookcode.pojos

import java.io.Serializable


data class BasicQueryPojo (var mSourceCity:String = "",
                           var mDestinationCity:String = "",
                           var mSourceHub:String = "",
                           var mDestinationHub:String = "",
                           var mFleets: ArrayList<String>?) : Serializable
    {
        override fun toString(): String = "Fleet Requirement\n" +
                "Route: $mSourceCity to $mDestinationCity\n" +
                "Fleet: ${mFleets.toString()}"
    }
