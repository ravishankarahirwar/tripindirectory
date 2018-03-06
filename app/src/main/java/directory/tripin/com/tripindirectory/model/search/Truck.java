package directory.tripin.com.tripindirectory.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 09-02-2018
 */

public class Truck {

    public Truck() {
        this.truckProperties = new ArrayList<>();
    }


    private String truckType;
    private boolean isTruckHave = false;
    private List<TruckProperty> truckProperties;

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public List<TruckProperty> getTruckProperties() {
        return truckProperties;
    }

    public void setTruckProperties(List<TruckProperty> truckProperties) {
        this.truckProperties = truckProperties;
    }

    public boolean isTruckHave() {
        return isTruckHave;
    }

    public void setTruckHave(boolean truckHave) {
        isTruckHave = truckHave;
    }

    //    private TruckProperty truckBodyType;
//    private TruckProperty truckLength;
//    private TruckProperty truckPayLoad;
//    private TruckProperty truckTyres;
//    private TruckProperty truckModelNo;
//    private TruckProperty truckMaker;
//
//    public List<TruckProperty> getmTruckProperties() {
//        return truckProperties;
//    }
//
//    public void setmTruckProperties(List<TruckProperty> mTruckProperties) {
//        this.truckProperties = mTruckProperties;
//    }
//
//    public String getTruckType() {
//        return truckType;
//    }
//
//    public void setTruckType(String truckType) {
//        this.truckType = truckType;
//    }
//
//    public TruckProperty getTruckBodyType() {
//        return truckBodyType;
//    }
//
//    public void setTruckBodyType(TruckProperty truckBodyType) {
//        this.truckBodyType = truckBodyType;
//    }
//
//    public TruckProperty getTruckLength() {
//        return truckLength;
//    }
//
//    public void setTruckLength(TruckProperty truckLength) {
//        this.truckLength = truckLength;
//    }
//
//    public TruckProperty getTruckPayLoad() {
//        return truckPayLoad;
//    }
//
//    public void setTruckPayLoad(TruckProperty truckPayLoad) {
//        this.truckPayLoad = truckPayLoad;
//    }
//
//    public TruckProperty getTruckTyres() {
//        return truckTyres;
//    }
//
//    public void setTruckTyres(TruckProperty truckTyres) {
//        this.truckTyres = truckTyres;
//    }
//
//    public TruckProperty getTruckModelNo() {
//        return truckModelNo;
//    }
//
//    public void setTruckModelNo(TruckProperty truckModelNo) {
//        this.truckModelNo = truckModelNo;
//    }
//
//    public TruckProperty getTruckMaker() {
//        return truckMaker;
//    }
//
//    public void setTruckMaker(TruckProperty truckMaker) {
//        this.truckMaker = truckMaker;
//    }
}
