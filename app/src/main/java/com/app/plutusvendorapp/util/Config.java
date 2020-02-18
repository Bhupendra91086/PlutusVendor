package com.app.plutusvendorapp.util;

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

   /* public static final String GET_DEAL_NEAR_ME ="https://evd-gimme.appspot.com/api/gimme/deals/nearby?";
    public static final String ADD_GIMME_CLIENT ="https://data-ingestion-dot-evd-gimme.appspot.com/api/add/gimme/users";
    public static final String BUY_DEAL ="https://data-ingestion-dot-evd-gimme.appspot.com/api/add/gimme/claimed-deals";*/

    public static final String GET_DEAL_NEAR_ME = "https://prod-gimme.appspot.com/api/gimme/deals/nearby?";
    public static final String ADD_GIMME_CLIENT = "https://prod-gimme.appspot.com/api/add/gimme/users";
    public static final String BUY_DEAL = "https://prod-gimme.appspot.com/api/add/gimme/claimed-deals";
    public static final String UPDATE_GIMME_CLIENT = "https://prod-gimme.appspot.com/api/edit/gimme/users";


    public static final String GET_CLIENT_CLAIMED_DEAL = "https://prod-gimme.appspot.com/api/gimme/claimed-deals/client";
    public static final String FirebaseInitial = "gs://";

    public static final String DEAL_COUNT = "https://prod-gimme.appspot.com/api/v1/claim/deal/";
    public static final String DEAL_CLAIM_LIMIT ="https://prod-gimme.appspot.com/api/v1/deal/";

    public static final String BUCKET_NAME = "gs://evd-gimme";
    public static final String ITEM_IMAGE_FOLDER="items";


    public static final String UPDATE_VENDOR="https://prod-gimme.appspot.com/api/v1/update/item";
    public static final String POST_AUTO_DEAL = "https://prod-gimme.appspot.com/api/v1/add/auto-deal";
    public static final String POST_DEAL  = "https://prod-gimme.appspot.com/api/v1/add/deal";


}