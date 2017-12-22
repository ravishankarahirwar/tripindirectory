package directory.tripin.com.tripindirectory.model.RawDataPojos;

/**
 * Created by Yogesh N. Tikam on 12/22/2017.
 */

public class MainRawPojo
{


    private ServiceType[] serviceType;

    private String like;

    private String emailId;

    private NatureOfbusiness[] natureOfbusiness;

    private String address;

    private String name;

    private AreaOfOperation[] areaOfOperation;

    private String dislike;

    private String[] contactPerson;

    private Mobile[] mobile;






    public ServiceType[] getServiceType ()
    {
        return serviceType;
    }

    public void setServiceType (ServiceType[] serviceType)
    {
        this.serviceType = serviceType;
    }



    public String getLike ()
    {
        return like;
    }

    public void setLike (String like)
    {
        this.like = like;
    }



    public String getEmailId ()
    {
        return emailId;
    }

    public void setEmailId (String emailId)
    {
        this.emailId = emailId;
    }

    public NatureOfbusiness[] getNatureOfbusiness ()
    {
        return natureOfbusiness;
    }

    public void setNatureOfbusiness (NatureOfbusiness[] natureOfbusiness)
    {
        this.natureOfbusiness = natureOfbusiness;
    }



    public String getAddress ()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }




    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public AreaOfOperation[] getAreaOfOperation ()
    {
        return areaOfOperation;
    }

    public void setAreaOfOperation (AreaOfOperation[] areaOfOperation)
    {
        this.areaOfOperation = areaOfOperation;
    }

    public String getDislike ()
    {
        return dislike;
    }

    public void setDislike (String dislike)
    {
        this.dislike = dislike;
    }



    public String[] getContactPerson ()
    {
        return contactPerson;
    }

    public void setContactPerson (String[] contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public Mobile[] getMobile ()
    {
        return mobile;
    }

    public void setMobile (Mobile[] mobile)
    {
        this.mobile = mobile;
    }




    public class Mobile
    {
        private String roleInOrganization;

        private String cellNo;

        private _id _id;

        private String name;

        public String getRoleInOrganization ()
        {
            return roleInOrganization;
        }

        public void setRoleInOrganization (String roleInOrganization)
        {
            this.roleInOrganization = roleInOrganization;
        }

        public String getCellNo ()
        {
            return cellNo;
        }

        public void setCellNo (String cellNo)
        {
            this.cellNo = cellNo;
        }

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [roleInOrganization = "+roleInOrganization+", cellNo = "+cellNo+", _id = "+_id+", name = "+name+"]";
        }
    }


    public class NatureOfbusiness
    {
        private _id _id;

        private String name;

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [_id = "+_id+", name = "+name+"]";
        }
    }

    public class Payload
    {
        private _id _id;

        private String name;

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [_id = "+_id+", name = "+name+"]";
        }
    }



    public class Length
    {
        private _id _id;

        private String name;

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [_id = "+_id+", name = "+name+"]";
        }
    }

    public class Tyres
    {
        private _id _id;

        private String name;

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [_id = "+_id+", name = "+name+"]";
        }
    }

    public class Fleet
    {
        private BodyType[] bodyType;

        private Model[] model;

        private _id _id;

        private Length[] length;

        private String[] payload;

        private String[] tyres;

        private String vehicleType;

        private String[] make;

        public BodyType[] getBodyType ()
        {
            return bodyType;
        }

        public void setBodyType (BodyType[] bodyType)
        {
            this.bodyType = bodyType;
        }

        public Model[] getModel ()
        {
            return model;
        }

        public void setModel (Model[] model)
        {
            this.model = model;
        }

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public Length[] getLength ()
        {
            return length;
        }

        public void setLength (Length[] length)
        {
            this.length = length;
        }

        public String[] getPayload ()
        {
            return payload;
        }

        public void setPayload (String[] payload)
        {
            this.payload = payload;
        }

        public String[] getTyres ()
        {
            return tyres;
        }

        public void setTyres (String[] tyres)
        {
            this.tyres = tyres;
        }

        public String getVehicleType ()
        {
            return vehicleType;
        }

        public void setVehicleType (String vehicleType)
        {
            this.vehicleType = vehicleType;
        }

        public String[] getMake ()
        {
            return make;
        }

        public void setMake (String[] make)
        {
            this.make = make;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [bodyType = "+bodyType+", model = "+model+", _id = "+_id+", length = "+length+", payload = "+payload+", tyres = "+tyres+", vehicleType = "+vehicleType+", make = "+make+"]";
        }
    }

    public class BodyType
    {
        private _id _id;

        private String name;

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [_id = "+_id+", name = "+name+"]";
        }
    }



    public class Model
    {
        private _id _id;

        private String name;

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [_id = "+_id+", name = "+name+"]";
        }
    }

    public class ServiceType
    {
        private _id _id;

        private String name;

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [_id = "+_id+", name = "+name+"]";
        }
    }

    public class AreaOfOperation
    {
        private _id _id;

        private String state;

        private String regionName;

        private Lat_lng lat_lng;

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getState ()
        {
            return state;
        }

        public void setState (String state)
        {
            this.state = state;
        }

        public String getRegionName ()
        {
            return regionName;
        }

        public void setRegionName (String regionName)
        {
            this.regionName = regionName;
        }

        public Lat_lng getLat_lng ()
        {
            return lat_lng;
        }

        public void setLat_lng (Lat_lng lat_lng)
        {
            this.lat_lng = lat_lng;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [_id = "+_id+", state = "+state+", regionName = "+regionName+", lat_lng = "+lat_lng+"]";
        }
    }


    public class Lat_lng
    {
        private String type;

        private String[] coordinates;

        public String getType ()
        {
            return type;
        }

        public void setType (String type)
        {
            this.type = type;
        }

        public String[] getCoordinates ()
        {
            return coordinates;
        }

        public void setCoordinates (String[] coordinates)
        {
            this.coordinates = coordinates;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [type = "+type+", coordinates = "+coordinates+"]";
        }
    }



    public class CreatedAt
    {
        private String $date;

        public String get$date ()
        {
            return $date;
        }

        public void set$date (String $date)
        {
            this.$date = $date;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [$date = "+$date+"]";
        }
    }


    public class UpdatedAt
    {
        private String $date;

        public String get$date ()
        {
            return $date;
        }

        public void set$date (String $date)
        {
            this.$date = $date;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [$date = "+$date+"]";
        }
    }

    public class _id
    {
        private String $oid;

        public String get$oid ()
        {
            return $oid;
        }

        public void set$oid (String $oid)
        {
            this.$oid = $oid;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [$oid = "+$oid+"]";
        }
    }






}
