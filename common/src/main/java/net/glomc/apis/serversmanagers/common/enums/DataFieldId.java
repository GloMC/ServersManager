package net.glomc.apis.serversmanagers.common.enums;

public enum DataFieldId {
    ONLINE("online", Integer.class), MAX_ONLINE("max-online", Integer.class);

    private final String fieldId;

    private final Class<?> type;


    DataFieldId(String fieldId, Class<?> type) {
        this.fieldId = fieldId;
        this.type = type;
    }

    public String getFieldId() {
        return fieldId;
    }

    public Class<?> type() {
        return type;
    }

}
