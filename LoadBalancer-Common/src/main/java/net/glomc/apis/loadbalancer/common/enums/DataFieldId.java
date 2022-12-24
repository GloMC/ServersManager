package net.glomc.apis.loadbalancer.common.enums;

public enum DataFieldId {
    ONLINE("online"), MAX_ONLINE("max-online");

    private final String fieldId;

    DataFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldId() {
        return fieldId;
    }
}
