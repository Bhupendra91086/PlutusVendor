package com.app.plutusvendorapp.bean.survey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyJson {

    private String heading;
    private List<String> item = null;
    private String subheading;
    private List<String> type = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public String getSubheading() {
        return subheading;
    }

    public void setSubheading(String subheading) {
        this.subheading = subheading;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}