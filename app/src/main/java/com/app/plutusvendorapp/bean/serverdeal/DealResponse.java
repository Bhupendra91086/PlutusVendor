
package com.app.plutusvendorapp.bean.serverdeal;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealResponse {

    @SerializedName("data")
    @Expose
    private List<Deal> data = null;
    @SerializedName("total")
    @Expose
    private Integer total;

    public List<Deal> getData() {
        return data;
    }

    public void setData(List<Deal> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
