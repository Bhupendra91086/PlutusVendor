
package com.app.plutusvendorapp.bean.regvendor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterVendor {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("content")
    @Expose
    private Content content;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

}
