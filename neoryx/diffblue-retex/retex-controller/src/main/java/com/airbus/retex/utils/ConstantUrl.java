package com.airbus.retex.utils;

/**
 * Constants for URL name
 *
 * @author mduretti
 */
public class ConstantUrl {


    private static final String ID = "/{id}";
    /**
     * private constructor
     */
    private ConstantUrl() {
    }

    /**
     * --------------------------------------------
     * -------------------- MAIN URLS -------------
     * --------------------------------------------
     */
    public static final String API = "/api";
    public static final String API_PUBLIC = API + "";

    /**
     * --------------------------------------------
     * -------------------- AUTH ------------------
     * --------------------------------------------
     */
    public static final String API_AUTH = API + "/auth";
    public static final String API_AUTH_LOGIN = API_AUTH + "/login";
    public static final String API_PUBLIC_AUTH = API_PUBLIC + "/auth";
    public static final String API_PUBLIC_AUTH_LOGIN = API_PUBLIC_AUTH + "/login";

    /**
     * --------------------------------------------
     * -------------------USERS -------------------
     * --------------------------------------------
     */
    public static final String API_USERS = ConstantUrl.API + "/users";
    public static final String API_USERS_ROLE = ConstantUrl.API + "/users/assignation";
    public static final String API_USER = ConstantUrl.API + "/users/{id}";

    public static final String API_USERS_REVOKE = API_USERS + "/{id}/revoke";
    public static final String API_USER_ME = ConstantUrl.API + "/users/me";
    public static final String API_USER_ROLES = ConstantUrl.API + "/users/{userId}/roles";
    public static final String API_USER_CAN_ACCESS_FEATURE = ConstantUrl.API + "/users/{userId}/feature/access";

    /**
     * --------------------------------------------
     * -------------------- ROLES -----------------
     * --------------------------------------------
     */
    public static final String API_ROLES = ConstantUrl.API + "/roles";
    public static final String API_ROLE_UPDATE = ConstantUrl.API + "/roles/{id}";
    public static final String API_ROLE_LABELS_UPDATE = ConstantUrl.API + "/roles/{id}";
    public static final String API_ROLE = API_ROLES + ID;

    public static final String API_ROLE_WITH_SEGREGATION_ADMIN = API_ROLES + "/segregation";
    public static final String API_ROLE_REVOKE = API_ROLES + "/revoke";
    /**
     * --------------------------------------------
     * --------------------LANGUAGES --------------
     * --------------------------------------------
     */
    public static final String API_LANGUAGE = ConstantUrl.API + "/languages";

    /**
     * --------------------------------------------
     * -------------- FUNCTIONALITIES -------------
     * --------------------------------------------
     */
    public static final String API_FUNCTIONALITIES = API + "/functionalities";
    public static final String API_FUNCTIONALITY = API + "/functionality/{id}";

    /**
     * --------------------------------------------
     * -------------- MATERIALS -------------------
     * --------------------------------------------
     */
    public static final String API_MATERIALS = ConstantUrl.API + "/materials";


    /**
     * --------------------------------------------
     * -------------- CLASSIFICATIONS -------------------
     * --------------------------------------------
     */
    public static final String API_CLASSIFICATIONS = ConstantUrl.API + "/classifications";

    /**
     * --------------------------------------------
     * -------------- TREATMENTS -----------------
     * --------------------------------------------
     */
    public static final String API_TREATMENTS = ConstantUrl.API + "/treatments";

    /**
     * --------------------------------------------
     * ----------------- DAMAGE LIBRARY------------
     * --------------------------------------------
     */
    public static final String API_DAMAGE_LIBRARY = ConstantUrl.API + "/damages";
    public static final String API_ADD_UPDATE_DAMAGED_DAMAGE = ID;
    public static final String API_FUNCTIONALITY_DAMAGES = "/{id}/functionality/{functionalityID}";
    public static final String API_REVOKE_FUNCTIONALITYDAMGE_OF_DAMAGE =  "/{id}/functionalityDamage/{functionalityDamageID}";
    public static final String API_ADD_DAMAGE_IMAGE = "/{id}/image";
    public static final String API_UPDATE_FUNCTIONALITY_DAMAGE = "/{id}/functionalityDamage/{functionalityDamageID}/update";
    public static final String API_ADD_FUNCTIONALITY_DAMAGE_IMAGE = "/{id}/functionalityDamage/{functionalityDamageID}/image";
    public static final String API_DELETE_DAMAGE_MEDIA = "/{id}/media";
    public static final String API_UPDATE_DAMAGE_STATUS = "/{id}/status";
    /**
     * --------------------------------------------
     * -------------------- FEATURES --------------
     * --------------------------------------------
     */
    public static final String API_FEATURE = API + "/features";

    /**
     * --------------------------------------------
     * -------------------- PARTS -----------------
     * --------------------------------------------
     */
    public static final String API_PARTS = ConstantUrl.API + "/parts";
    public static final String API_DELETE_PART = ID;
    public static final String API_PARTS_UPDATE = ID;
    public static final String API_PART = ID;
    public static final String API_DUPLICATE_PART = "/duplicate/{id}";
    public static final String API_PART_NUMBERS = "/part-numbers";
    public static final String API_PART_NUMBERS_ROOT = "/part-numbers-root";
    public static final String API_ADD_PART_MAPPING_IMAGE = "/{id}/image";
    public static final String API_PART_ADD_FUNCTIONAL_AREAS = "/{id}/functional-areas";


    /**
     * --------------------------------------------
     * ------------- PARTS DESIGNATIONS -----------
     * --------------------------------------------
     */
    public static final String API_DESIGNATIONS = ConstantUrl.API + "/designations";


    /**
     * --------------------------------------------
     * -------------------- ATA -------------------
     * --------------------------------------------
     */
    public static final String API_ATAS = ConstantUrl.API + "/atas";

    /**
     * --------------------------------------------
     * -------------- FUNCTIONAL AREA -------------
     * --------------------------------------------
     */
    public static final String API_FUNCTIONAL_AREAS = ConstantUrl.API + "/functional-areas/{id}";

    /**
     * --------------------------------------------
     * -------------- FUNCTIONAL AREA NAMES--------
     * --------------------------------------------
     */
    public static final String API_FUNCTIONALS_AREA_NAMES = ConstantUrl.API + "/functional-area-names";


    /**
     * --------------------------------------------
     * -------------------- MEDIA -----------------
     * --------------------------------------------
     */
    public static final String API_GET_MEDIA = ConstantUrl.API + "/media/{uuid}";
    public static final String API_POST_MEDIA_PRE_UPLOAD_FILES = ConstantUrl.API + "/media/pre-upload";


    /**
     * --------------------------------------------
     * -------------- ROUTING COMPONENT -----------
     * --------------------------------------------
     */
    public static final String API_ROUTING_COMPONENTS = ConstantUrl.API + "/routing-components";


    /**
     * --------------------------------------------
     * ---------------- MEASURE UNIT --------------
     * --------------------------------------------
     */
    public static final String API_MEASURE_UNITS = ConstantUrl.API + "/measure-units";

    /**
     * --------------------------------------------
     * ---------------- ROUTING --------------
     * --------------------------------------------
     */
    public static final String API_ROUTINGS = ConstantUrl.API + "/routings";
    public static final String API_ROUTING = API_ROUTINGS + ID;
    public static final String API_DELETE_ROUTING = API_ROUTINGS + ID;
    public static final String API_ROUTING_AVAILABLE_FILTERS = API_ROUTINGS + "/available-filters";
    private static final String SUFFIX_OPERATIONS = "/{routingId}/operations";
    private static final String SUFFIX_OPERATION = SUFFIX_OPERATIONS + "/{operationId}";
    public static final String API_ROUTING_INSPECTION = API_ROUTINGS + SUFFIX_OPERATION + "/tasks/{taskId}";
    public static final String API_ROUTING_INSPECTION_HEADER = API_ROUTING_INSPECTION + "/header";
    public static final String API_ROUTINGS_OPERATIONS = API_ROUTINGS + SUFFIX_OPERATIONS;
    public static final String API_DUPLICATE_ROUTING = API_ROUTING + "/duplicate";
    public static final String API_PUBLISH_ROUTING = API_ROUTING + "/publish";
    /**
     * --------------------------------------------
     * ---------------- OPERATION TYPE --------------
     * --------------------------------------------
     */
    public static final String API_OPERATION_TYPES = ConstantUrl.API + "/operation-types";
    public static final String API_OPERATION_TYPES_TODO_LIST = API_OPERATION_TYPES + "/{id}/todo-lists";


    /**
     * --------------------------------------------
     * -------------------- MISSION -------------------
     * --------------------------------------------
     */
    public static final String API_MISSIONS = ConstantUrl.API + "/missions";

    /**
     * --------------------------------------------
     * -------------------- ENVIRONMENT -------------------
     * --------------------------------------------
     */
    public static final String API_ENVIRONMENTS = ConstantUrl.API + "/environments";
    /**
     * --------------------------------------------
     * -------------- REQUEST -------------
     * --------------------------------------------
     */
    public static final String API_REQUESTS = ConstantUrl.API + "/requests";
    public static final String API_REQUEST = API_REQUESTS + ID;
    public static final String API_REQUEST_REQUESTERS = API_REQUESTS + "/requesters";
    public static final String API_REQUEST_UPDATE_TRUE = API_REQUEST + "/validate"; //FIXME 1 seul URL
    public static final String API_REQUEST_UPDATE_FALSE = API_REQUEST + "/save"; //FIXME 1 seul URL
    public static final String API_REQUEST_TECHNICAL_MANAGERS = API_REQUEST + "/managers";
    public static final String API_REQUEST_DELETE = API_REQUEST;
    public static final String API_REQUEST_UPDATE_STATUS = API_REQUEST;
    public static final String API_REQUEST_GET_VERSIONS = API_REQUEST +"/versions";


    /**
     * --------------------------------------------
     * -------------- CHILD REQUEST ---------------
     * --------------------------------------------
     */
    public static final String API_CHILD_REQUESTS = API_REQUEST + "/child-requests";
    public static final String API_CHILD_REQUEST =  API_CHILD_REQUESTS + "/{childRequestId}";
    public static final String API_CHILD_REQUEST_UPDATE_STATUS = API_CHILD_REQUEST;
    public static final String API_CHILD_REQUEST_DELETE = API_CHILD_REQUEST;
    public static final String API_CHILD_REQUEST_UPDATE = API_CHILD_REQUEST;
    public static final String API_CHILD_REQUEST_GET_VERSIONS = API_REQUESTS + "/child-requests/{childRequestId}/versions";


    /**
     * --------------------------------------------
     * -------------- FILTERING -------------------
     * --------------------------------------------
     */
    public static final String API_FILTERINGS = ConstantUrl.API + "/filterings";
    public static final String API_FILTERING = ConstantUrl.API_FILTERINGS + ID;
    public static final String API_FILTERING_POST_DRT = ConstantUrl.API_FILTERING+ "/drt";
    public static final String API_FILTERING_UPDATE = ConstantUrl.API_FILTERING;

    /**
     * --------------------------------------------
     * -------------- DRT -------------------
     * --------------------------------------------
     */
    public static final String API_DRTS = ConstantUrl.API + "/drts";
    public static final String API_DRT = ConstantUrl.API_DRTS + ID;
    public static final String API_DRT_HEADER = ConstantUrl.API_DRT + "/header";
    public static final String API_DRT_INSPECTION = ConstantUrl.API_DRT + "/operations/{operationId}/tasks/{taskId}";
    public static final String API_DRT_INSPECTION_QCHECK = ConstantUrl.API_DRT_INSPECTION + "/qcheck";
    public static final String API_DRT_OPERATIONS = ConstantUrl.API_DRT + "/operations";

    /**
     * --------------------------------------------
     * -------------------- CLIENT -------------------
     * --------------------------------------------
     */
    public static final String API_CLIENTS = ConstantUrl.API + "/clients";


    /**
     * --------------------------------------------
     * -------------------- AIRCRAFT FAMILY--------
     * --------------------------------------------
     */
    public static final String API_AIRCRAFTS_FAMILIES = ConstantUrl.API + "/aircraft-families";


    /**
     * --------------------------------------------
     * -------------------- AIRCRAFT TYPE-------------------
     * --------------------------------------------
     */
    public static final String API_AIRCRAFTS_TYPES = ConstantUrl.API + "/aircraft-types";


    /**
     * --------------------------------------------
     * -------------------- AIRCRAFT VERSION-------------------
     * --------------------------------------------
     */
    public static final String API_AIRCRAFTS_VERSIONS = ConstantUrl.API + "/aircraft-versions";

    /**
     * --------------------------------------------
     * -------------- AIRBUS_ENTITY -------------
     * --------------------------------------------
     */
    public static final String API_AIRBUS_ENTITIES = ConstantUrl.API + "/airbus-entities";
    public static final String API_AIRBUS_ENTITIES_SEARCH = ConstantUrl.API_AIRBUS_ENTITIES + "/search";
    /**
     * --------------------------------------------
     * -------------- ORIGIN ----------------------
     * --------------------------------------------
     */
    public static final String API_ORIGINS = ConstantUrl.API + "/origins";
    public static final String API_ORIGINS_SEARCH = ConstantUrl.API_ORIGINS + "/search";

    /**
     * --------------------------------------------
     * -------------- VERSION ----------------------
     * --------------------------------------------
     */
    public static final String API_VERSION = ConstantUrl.API + "/versions";
    public static final String API_VERSIONS = ConstantUrl.API_VERSION + "/";

    /**
     * --------------------------------------------
     * -------------- THING WORX ------------------
     * --------------------------------------------
     */
    public static final String API_THINGWORX_URL = ConstantUrl.API + "/thingworx/url";
    public static final String API_THINGWORX_MEASURES = ConstantUrl.API + "/thingworx/measures";
    public static final String API_THINGWORX_MEDIA_PREUPLOAD = ConstantUrl.API + "/thingworx/media/pre-upload";

}
