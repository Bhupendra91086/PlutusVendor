
package com.app.plutusvendorapp.bean.serveritem;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.app.plutusvendorapp.bean.item.Item;

public class ItemResponse {

    @SerializedName("data")
    @Expose
    private List<Item> data = null;
    @SerializedName("total")
    @Expose
    private Integer total;

    public List<Item> getData() {
        return data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
