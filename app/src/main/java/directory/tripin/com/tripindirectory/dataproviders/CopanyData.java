package directory.tripin.com.tripindirectory.dataproviders;

import java.util.HashMap;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 17-03-2018
 */

public class CopanyData {
    public static final HashMap<String, Boolean> getTypeOfServices() {
         HashMap<String, Boolean> mTypesofServicesHashMap;

        mTypesofServicesHashMap = new HashMap<>();
        mTypesofServicesHashMap.put("FTL".toUpperCase(), false);
        mTypesofServicesHashMap.put("Part Loads".toUpperCase(), false);
        mTypesofServicesHashMap.put("Open Body Truck Load".toUpperCase(), false);
        mTypesofServicesHashMap.put("Trailer Load".toUpperCase(), false);
        mTypesofServicesHashMap.put("Parcel".toUpperCase(), false);
        mTypesofServicesHashMap.put("ODC".toUpperCase(), false);
        mTypesofServicesHashMap.put("Import Containers".toUpperCase(), false);
        mTypesofServicesHashMap.put("Export Containers".toUpperCase(), false);
        mTypesofServicesHashMap.put("Chemical".toUpperCase(), false);
        mTypesofServicesHashMap.put("Petrol".toUpperCase(), false);
        mTypesofServicesHashMap.put("Diesel".toUpperCase(), false);
        mTypesofServicesHashMap.put("Oil".toUpperCase(), false);
        mTypesofServicesHashMap.put("Packer and Movers".toUpperCase(), false);

        return mTypesofServicesHashMap;
    }

    public static final HashMap<String, Boolean> getNatureOfBusiness() {
         HashMap<String, Boolean> mNatureofBusinessHashMap;
        mNatureofBusinessHashMap = new HashMap<>();

        mNatureofBusinessHashMap.put("Fleet Owner".toUpperCase(), false);
        mNatureofBusinessHashMap.put("Transport Contractor".toUpperCase(), false);
        mNatureofBusinessHashMap.put("Commission Agent".toUpperCase(), false);

        return mNatureofBusinessHashMap;

    }
}
