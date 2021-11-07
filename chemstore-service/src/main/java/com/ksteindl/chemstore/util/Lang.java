package com.ksteindl.chemstore.util;

public class Lang {

    public static final String ENTITY_IS_DELETED = "%s %s is deleted, operation is aborted";

    public static final String CHEMICAL_ENTITY_NAME = "chemical";
    public static final String CHEMICAL_SAME_NAME_FOUND_TEMPLATE = "Chemical with same name(s) was found (short name: '%s', exact name: '%s')";
    public static final String CHEMICAL_ALREADY_DELETED = "Chamical %s has been already deleted";

    public static final String MANUFACTURER_ENTITY_NAME = "manufacturer";
    public static final String MANUFACTURER_WITH_SAME_NAME_FOUND_TEMPLATE = "Manufacturer with name '%s' is already exists";
    public static final String MANUFACTURER_ALREADY_DELETED = "Manufacturer %s has been already deleted";

    public static final String APP_USER_ENTITY_NAME = "app-user";
    public static final String APP_USER_USERNAME_ATTRIBUTE_NAME = "username";
    public static final String APP_USER_PASSWORD_ATTRIBUTE_NAME = "password";
    public static final String APP_USER_PASSWORD2_ATTRIBUTE_NAME = "password2";
    public static final String APP_USER_SAME_NAME_FOUND_TEMPLATE = "App user with same username was found ('%s')";
    public static final String APP_USER_ALREADY_DELETED = "App user %s has been already deleted)";
    public static final String PASSWORDS_MUST_BE_THE_SAME = "password and password2 must be the same";
    public static final String WRONG_OLD_PASSWORD = "The given old password is not matching with the current password";
    public static final String USERNAME_CANNOT_BE_CHANGED = "username of the app user cannot be updated (%s, %s)";


    public static final String LAB_ENTITY_NAME = "lab";
    public static final String LAB_KEY_ATTRIBUTE_NAME = "key";
    public static final String LAB_SAME_NAME_FOUND_TEMPLATE = "Lab with same key/name was found (key: '%s', name: '%s')";
    public static final String LAB_KEY_CANNOT_BE_CHANGED = "key of the lab cannot be updated (%s, %s)";
    public static final String LAB_ALREADY_DELETED = "Lab %s has been already deleted";

    public static final String CHEM_ITEM_ENTITY_NAME = "chemItem";
    public static final String CHEM_ITEM_LAB_KEY_ATTRIBUTE_NAME = "labKey";
    public static final String CHEM_ITEM_ARRIVAL_DATE_ATTRIBUTE_NAME = "arrivalDate";
    public static final String CHEM_ITEM_CHEMICAL_SHORT_NAME_ATTRIBUTE_NAME = "chemicalShortName";
    public static final String CHEM_ITEM_MANUFACTURER_SHORT_NAME_ATTRIBUTE_NAME = "manufacturerName";
    public static final String CHEM_ITEM_BATCH_NUMBER_ATTRIBUTE_NAME = "batchNumber";
    public static final String CHEM_ITEM_QUANTITY_ATTRIBUTE_NAME = "quantity";
    public static final String CHEM_ITEM_UNIT_ATTRIBUTE_NAME = "unit";

    public static final String CHEM_ITEM_CREATION_NOT_AUTHORIZED = "Adding new chemical to %s is forbidden. %s has to be assigned to lab as user or as lab admin, or has to be the manager of the lab";
    public static final String CHEM_ITEM_ARRIVAL_DATE_IS_FUTURE = "Arrival date of the chemical item (%s) (arrivalDate) cannot be in the future";
    public static final String CHEM_ITEM_EXP_DATE_IS_IN_PAST = "Expiration Date of item (expirationDateBeforeOpened) (%s) cannot be in the past";


    public static final String CHEM_TYPE_WITH_SAME_NAME_FOUND_TEMPLATE = "Chemical item with name '%s' is already exists";
    public static final String CHEM_TYPE_ENTITY_NAME = "Chemical type";
    public static final String CHEM_TYPE_ALREADY_DELETED = "Chemical type %s has been already deleted";

    public static final String SHELF_TIME_SET_FORBIDDEN = "Setting self time for %s is forbidden. You have to be either the manager of the lab, or the admin";
    public static final String SHELF_LIFE_ALREADY_DELETED = "Shelf life of %s in %s is already deleted";
    public static final String SHELF_LIFE_ALREADY_EXISTS = "Shelf life for %s in %s is already exists";

    public static final String ROLE_ENTITY_NAME = "role";

    public static final String INVALID_UNIT = "Unit %s is invalid. Must be one of the list: %s";

    public static final String PASSWORD_TOO_SHORT = APP_USER_PASSWORD_ATTRIBUTE_NAME + " must be equal or greater than %s";










}
