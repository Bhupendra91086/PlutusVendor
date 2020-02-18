
package com.app.plutusvendorapp.bean.serverdeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DealActiveResponse {

    @SerializedName("data")
    @Expose
    private List<DealActive> data = null;
    @SerializedName("total")
    @Expose
    private Integer total;

    public List<DealActive> getData() {
        return data;
    }

    public void setData(List<DealActive> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
