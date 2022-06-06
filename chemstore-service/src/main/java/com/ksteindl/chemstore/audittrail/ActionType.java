package com.ksteindl.chemstore.audittrail;

public enum ActionType {
    
    CREATE, UPDATE, DELETE, ARCHIVE;
    
    public static boolean contains(String value) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }
    
}
