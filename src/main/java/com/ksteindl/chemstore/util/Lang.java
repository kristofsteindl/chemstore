package com.ksteindl.chemstore.util;

public class Lang {

    public static final String CHEMICAL_ENTITY_NAME = "chemical";
    public static final String CHEMICAL_SAME_NAME_FOUND_TEMPLATE = "Chemical with same name(s) was found (short name: '%s', exact name: '%s')";
    public static final String CHEMICAL_IS_DELETED = "Chemical %s is deleted, operation is aborted";

    public static final String MANUFACTURER_ENTITY_NAME = "manufacturer";
    public static final String MANUFACTURER_WITH_SAME_NAME_FOUND_TEMPLATE = "Manufacturer with name '%s' is already exists";
    public static final String MANUFACTURER_IS_DELETED = "Manufacturer %s is deleted, operation is aborted";


    public static final String APP_USER_ENTITY_NAME = "app-user";
    public static final String APP_USER_USERNAME_ATTRIBUTE_NAME = "username";
    public static final String APP_USER_PASSWORD_ATTRIBUTE_NAME = "password";
    public static final String APP_USER_PASSWORD2_ATTRIBUTE_NAME = "password2";
    public static final String APP_USER_SAME_NAME_FOUND_TEMPLATE = "App user with same username was found ('%s')";
    public static final String PASSWORDS_MUST_BE_THE_SAME = "password and password2 must be the same";
    public static final String USERNAME_CANNOT_BE_CHANGED = "username of the app user cannot be updated (%s, %s)";
    public static final String APP_USER_IS_DELETED = "User %s is deleted, operation is aborted";


    public static final String LAB_ENTITY_NAME = "lab";
    public static final String LAB_KEY_ATTRIBUTE_NAME = "key";
    public static final String LAB_SAME_NAME_FOUND_TEMPLATE = "Lab with same key/name was found (key: '%s', name: '%s')";
    public static final String LAB_KEY_CANNOT_BE_CHANGED = "key of the lab cannot be updated (%s, %s)";
    public static final String LAB_IS_DELETED = "Lab %s is deleted, operation is aborted";

    public static final String ROLE_ENTITY_NAME = "role";

    public static final String PASSWORD_TOO_SHORT = APP_USER_PASSWORD_ATTRIBUTE_NAME + " must be equal or greater than %s";










}
