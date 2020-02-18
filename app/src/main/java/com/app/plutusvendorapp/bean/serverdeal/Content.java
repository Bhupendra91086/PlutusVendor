
package com.app.plutusvendorapp.bean.serverdeal;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;

import com.app.plutusvendorapp.database.Converters;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Entity
public class Content  implements Serializable {

    @SerializedName("images")
    @Expose
    @TypeConverters(Converters.class)
    @ColumnInfo(name = "images")
    private List<String> images = null;
    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;



    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("claimLimit")
    @Expose
    private Integer claimLimit;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("discount")
    @Expose
    private Integer discount;

    @SerializedName("itemId")
    @Expose
    private String itemId;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("validFrom")
    @Expose
    private String validFrom;
    @SerializedName("validTo")
    @Expose
    private String validTo;
    @SerializedName("vendorId")
    @Expose
    private String vendorId;

   /* @SerializedName("repeatEvery")
    @Expose
    private String repeatEvery;*/

    @SerializedName("repeatEvery")
    @Expose
    @TypeConverters(Converters.class)
    @ColumnInfo(name = "repeatEvery")
    private List<String> repeatEvery = null;


    public List<String> getRepeatEvery() {
        return repeatEvery;
    }

    public void setRepeatEvery(List<String> repeatEvery) {
        this.repeatEvery = repeatEvery;
    }



    @SerializedName("time")
    @Expose
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public Integer getValidForHours() {
        return validForHours;
    }

    public void setValidForHours(Integer validForHours) {
        this.validForHours = validForHours;
    }



    @SerializedName("validForHours")
    @Expose
    private Integer validForHours;



    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getClaimLimit() {
        return claimLimit;
    }

    public void setClaimLimit(Integer claimLimit) {
        this.claimLimit = claimLimit;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

}
