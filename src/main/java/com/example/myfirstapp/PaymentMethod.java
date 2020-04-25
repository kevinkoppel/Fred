package com.example.myfirstapp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PaymentMethod {



    @JsonProperty("clientSecret")
    public String clientSecret;

    @JsonProperty("created")
    public Integer created;

    @JsonProperty("id")
    public String id;

    @JsonProperty("isLiveMode")
    public Boolean isLiveMode;

    @JsonProperty("objectType")
    public String objectType;

    @JsonProperty("paymentMethodId")
    public String paymentMethodId;

    @JsonProperty("paymentMethodTypes")
    public List<String> paymentMethodTypes;

    @JsonProperty("status")
    public String status;

    @JsonProperty("usage")
    public String usage;

    public PaymentMethod(String clientSecret, Integer created, String id, Boolean isLiveMode, String objectType, String paymentMethodId, List<String> paymentMethodTypes, String status, String usage) {
        this.paymentMethodId = paymentMethodId;
        this.clientSecret = clientSecret;
        this.created = created;
        this.id = id;
        this.isLiveMode = isLiveMode;
        this.objectType = objectType;
        this.paymentMethodTypes = paymentMethodTypes;
        this.status = status;
        this.usage = usage;
    }
    public PaymentMethod(){

    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getLiveMode() {
        return isLiveMode;
    }

    public void setLiveMode(Boolean liveMode) {
        isLiveMode = liveMode;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public List<String> getPaymentMethodTypes() {
        return paymentMethodTypes;
    }

    public void setPaymentMethodTypes(List<String> paymentMethodTypes) {
        this.paymentMethodTypes = paymentMethodTypes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
}
