package com.symbol.sammiforge.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchCondition {
    @SerializedName("FromDate")
    public String FromDate;

    @SerializedName("ToDate")
    public String ToDate;

    @SerializedName("ContractNo")
    public String ContractNo;

    @SerializedName("AppCode")
    public String AppCode;

    @SerializedName("AndroidID")
    public String AndroidID;

    @SerializedName("Model")
    public String Model;

    @SerializedName("PhoneNumber")
    public String PhoneNumber;

    @SerializedName("DeviceName")
    public String DeviceName;

    @SerializedName("DeviceOS")
    public String DeviceOS;

    @SerializedName("AppVersion")
    public String AppVersion;

    @SerializedName("Remark")
    public String Remark;

    @SerializedName("SaleType")
    public String SaleType;

    @SerializedName("UserID")
    public String UserID;

    @SerializedName("PassWord")
    public String PassWord;

    @SerializedName("Language")
    public int Language;

    @SerializedName("NType")
    public String NType;

    @SerializedName("CustomerCode")
    public String CustomerCode;

    @SerializedName("PicFlag")
    public int PicFlag;

    @SerializedName("RepairNo")
    public String RepairNo;

    @SerializedName("SeqNo")
    public int SeqNo;

    @SerializedName("RepairPhoto1")
    public String RepairPhoto1;

    @SerializedName("RepairPhoto2")
    public String RepairPhoto2;

    @SerializedName("RepairPhoto3")
    public String RepairPhoto3;

    @SerializedName("RepairPhoto4")
    public String RepairPhoto4;

    @SerializedName("PicNo")
    public int PicNo;

    @SerializedName("InitFlag")
    public boolean InitFlag;
    /*@SerializedName("ScanList")
    public List<ScanListViewItem> ScanList;

    @SerializedName("ScanList2")
    public List<ScanListViewItem2> ScanList2;*/

    public SearchCondition(){}

}
