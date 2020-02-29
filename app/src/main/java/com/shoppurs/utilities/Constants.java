package com.shoppurs.utilities;

/**
 * Created by Shweta on 8/10/2016.
 */
public class Constants {

    public static String APP_NAME="shoppurs";

    public static String MYPREFERENCEKEY="com."+APP_NAME+".MyPrefs";
    public static String GOOGLE_MAP_API_KEY="googleMapApiKey";
    public static String USER_ID="userID";
    public static String FIRST_NAME = "firstName";
    public static String USER_CODE="userCode";
    public static String USER_TYPE_ID="user_type_id";
    public static String EMAIL="email";
    public static String PASSWORD="password";
    public static String MOBILE_NO="mobileNo";
    public static String ADDRESS="address";
    public static String COUNTRY="country";
    public static String STATE="state";
    public static String USER_LAT="lat";
    public static String USER_LONG="long";
    public static String ZIP = "zip";
    public static String CITY = "city";
    public static String DB_NAME="db_name";
    public static String DB_USER_NAME="db_user_name";
    public static String SHOP_DB_USER_NAME="shop_db_user_name";
    public static String SHOP_DBNAME = "shop_db_name";
    public static String DB_PASSWORD="db_password";
    public static String SHOP_DB_PASSWORD="shop_db_password";
    public static String PROFILE_PIC = "profile_pic";
    public static String GST_NO = "gst_no";
    public static String CUST_ADDRESS="CUST_ADDRESS";
    public static String CUST_LAT="CUST_LAT";
    public static String CUST_LONG="CUST_LONG";
    public static String CUST_LOCALITY="CUST_LOCALITY";
    public static String CUST_CITY="CUST_CITY";
    public static String CUST_STATE="CUST_STATE";
    public static String CUST_COUNTRY="CUST_COUNTRY";
    public static String CUST_PINCODE="CUST_PINCODE";

    public static String CUST_CURRENT_ADDRESS="CUST_CURRENT_ADDRESS";
    public static String CUST_CURRENT_LAT="CUST_CURRENT_LAT";
    public static String CUST_CURRENT_LONG="CUST_CURRENT_LONG";


   // public static String DOB="dob";
    public static String LOCATION="location";
    public static String FULL_NAME="fullName";
    public static String JWT_TOKEN="jwt_token";
    public static String DB_VERSION="db_version";
    public static String OTP="otp";
    public static String USERNAME="username";
    public static String ROLE="role";
    public static String ACTIVATE_KEY="activate_key";
    public static String GUID="guid";
    public static String TOKEN="token";
    public static String CREATED="created";
    public static String MODIFIED="modified";
    public static String FORGOT_PASSWORD_REQUEST_TIME="forgot_password_request_time";
    public static String LAST_NAME="lastName";
    public static String IS_LOGGED_IN="isLoggedIn";
    public static String IS_DATABASE_CREATED="isDatabaseCreated";
    public static String VEHICLE_TYPE="vehicleType";
    public static String COLOR_THEME="colorTheme";
    public static String IS_DARK_THEME="isDarkTheme";

    public static String FCM_TOKEN="fcmToken";
    public static String IS_TOKEN_SAVED="isTokenSaved";
    public static String DEVICE_ID="deviceId";
    public static String NOTIFICATION_COUNTER="notificationCounter";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com."+APP_NAME;
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final String STATUS = PACKAGE_NAME +
            ".STATUS";

    public static final String SHOP_INSIDE_CODE = "shop_code";
    public static final String SHOP_INSIDE_NAME = "shop_name";


    //apis
    public static String UPDATE_BASIC_PROFILE="/api/customers/profile/update_basic_details";
    public static String UPDATE_ADDRESS="/api/customers/profile/update_address";
    public static String GENERATE_ORDER="/api/order/generate_order";
    public static String PLACE_ORDER="/api/order/place_order";
    public static String ADD_TRANS_DATA="trans/add_trans_data";
    public static String ADD_INVOICE_DATA="/api/trans/generate_invoice";
    public static String GET_COUNTRIES="/api/customers/countries";
    public static String GET_STATES="/api/customers/states?countryId=";
    public static String GET_CITIES="/api/customers/cities?stateId=";
    public static String GET_INVOICE="trans/get_invoice";
    public static String GET_COUPON_OFFER="offers/get_coupon_offer";

    public static String IS_HOME_DELIVERY_SELECTED ="isHomeDeliverySelected";
    public static String MIN_DELIVERY_DISTANCE="minDeliveryDistance";
    public static String MIN_DELIVERY_AMOUNT="minDeliveryAmount";
    public static String DELIVERY_EST_TIME="deliveryEstTime";
    public static String CHARGE_AFTER_MIN_DISTANCE="chargeAfterMinDistance";
    public static String SEND_MESSAGE="/api/chat/chat_for_support";
    public static String GET_MESSAGE="/api/chat/get_chat_for_support";
    public static String GET_CHAT_USERS="/api/chat/get_chat_users";
    public static String ASSIGN_STATUS="/api/delivery/order/get_shop_order_Status";

    public static String KHATA_TRANSACTIONS="/api/khata/transactions";


}
