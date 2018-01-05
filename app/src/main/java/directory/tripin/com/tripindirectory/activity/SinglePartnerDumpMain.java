package directory.tripin.com.tripindirectory.activity;

/**
 * Created by Shubham on 12/26/2017.
 */
public class SinglePartnerDumpMain
{
    private Phone[] phone;

    private String emailId;

    private NatureOfbusiness[] natureOfbusiness;

    private String address;

    private String description;

    private String name;

    private AreaOfOperation[] areaOfOperation;

    private ServiceType[] serviceType;

    private String like;

    private String dislike;

    private Mobile[] mobile;

    public Phone[] getPhone ()
    {
        return phone;
    }

    public void setPhone (Phone[] phone)
    {
        this.phone = phone;
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

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
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

    public String getDislike ()
    {
        return dislike;
    }

    public void setDislike (String dislike)
    {
        this.dislike = dislike;
    }

    public Mobile[] getMobile ()
    {
        return mobile;
    }

    public void setMobile (Mobile[] mobile)
    {
        this.mobile = mobile;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [phone = "+phone+", emailId = "+emailId+", natureOfbusiness = "+natureOfbusiness+", address = "+address+", description = "+description+", name = "+name+", areaOfOperation = "+areaOfOperation+", serviceType = "+serviceType+", like = "+like+", dislike = "+dislike+", mobile = "+mobile+"]";
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

    public class Phone
    {
        private String landline;

        private _id _id;

        private String ext;

        public String getLandline ()
        {
            return landline;
        }

        public void setLandline (String landline)
        {
            this.landline = landline;
        }

        public _id get_id ()
        {
            return _id;
        }

        public void set_id (_id _id)
        {
            this._id = _id;
        }

        public String getExt ()
        {
            return ext;
        }

        public void setExt (String ext)
        {
            this.ext = ext;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [landline = "+landline+", _id = "+_id+", ext = "+ext+"]";
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



}
