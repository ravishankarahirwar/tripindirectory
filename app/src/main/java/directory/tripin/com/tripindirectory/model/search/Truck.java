package directory.tripin.com.tripindirectory.model.search;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 09-02-2018
 */

public class Truck {
    private String truckType;
    private TruckProperty truckBodyType;
    private TruckProperty truckLength;
    private TruckProperty truckPayLoad;
    private TruckProperty truckTyres;
    private TruckProperty truckModelNo;
    private TruckProperty truckMaker;

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public TruckProperty getTruckBodyType() {
        return truckBodyType;
    }

    public void setTruckBodyType(TruckProperty truckBodyType) {
        this.truckBodyType = truckBodyType;
    }

    public TruckProperty getTruckLength() {
        return truckLength;
    }

    public void setTruckLength(TruckProperty truckLength) {
        this.truckLength = truckLength;
    }

    public TruckProperty getTruckPayLoad() {
        return truckPayLoad;
    }

    public void setTruckPayLoad(TruckProperty truckPayLoad) {
        this.truckPayLoad = truckPayLoad;
    }

    public TruckProperty getTruckTyres() {
        return truckTyres;
    }

    public void setTruckTyres(TruckProperty truckTyres) {
        this.truckTyres = truckTyres;
    }

    public TruckProperty getTruckModelNo() {
        return truckModelNo;
    }

    public void setTruckModelNo(TruckProperty truckModelNo) {
        this.truckModelNo = truckModelNo;
    }

    public TruckProperty getTruckMaker() {
        return truckMaker;
    }

    public void setTruckMaker(TruckProperty truckMaker) {
        this.truckMaker = truckMaker;
    }
}
