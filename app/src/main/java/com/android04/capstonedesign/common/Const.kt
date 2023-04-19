package com.android04.capstonedesign.common
// 어플 전역에서 사용되는 상수

const val MINUTE = 1000 * 60
const val ENTER_FROM_SERVICE = "enter_from_service"
const val NOTIFICATION_ID = 10
const val SERVICE_DESTROYED = "service_destroyed"
const val INVALID_DATA = "invalid_data"
//Firebase + DTO
const val LOG_DATA = "LogData"
const val ERROR_LOG = "ErrorLog"
const val GOOGLE_EMAIL = "Google_email"
const val GOOGLE_ID = "googleId"
const val USER_PROFILE = "UserProfile"
const val POINT = "Point"
const val SUBSCRIBED_PRODUCT = "SubscribedProduct"
const val SUBSCRIBED_PRODUCT_LOG = "SubscribedProductLog"
const val PRODUCT_TYPE = "productType"
const val POINT_LOG = "PointLog"
const val APP_INFO = "AppInfo"
const val LOGIN_TYPE = "loginType"
// Retrofit
const val RESPONSE_OK = "ok"
// Type + State + Message
const val MESSAGE_INIT = "init"
const val MESSAGE_SIGN_UP_POINT = "Celebrate sign up"
const val MESSAGE_PRODUCT_SUB = "Subscribed Product"
const val MESSAGE_PRODUCT_SUB_GPS = "Subscribed Location Insight"
const val MESSAGE_PRODUCT_SUB_APP = "Subscribed App Usages Insight"
const val MESSAGE_LOG_UPLOAD_APP = "Upload App usages Log"
const val MESSAGE_LOG_UPLOAD_GPS = "Upload Location Log"
//NetworkType
const val NETWORK_ONLY = "Network Only"
const val WIFI_ONLY = "Wifi Only"
const val BOTH = "Both"
// Insight
const val TAB_LOCATION = "Location Insight"
const val TAB_APP = "App Stats Insight"

enum class ProductPageType {
    MY, REC
}

enum class LoginType(val code: Int) {
    SELLER(0), BUYER(1)
}

enum class ProductType(val code: Int) {
    INIT(0), LOCATION(1), APP_USAGE(2), LOCATION_INSIGHT(3), APP_USAGE_INSIGHT(4)
}

enum class Sex(val code: Int) {
    MALE(0), FEMALE(1)
}

enum class LogType(val code: Int) {
    INIT(0), PRODUCT_SUB(10), PRODUCT_UNSUB(11), POINT_PLUS(20), POINT_MINUS(21), LOG_UPLOAD(30), LOG_DOWNLOAD(31)
}

enum class LogState(val code: Int) {
    APPROVED(0), DENIED(1), ERROR(2)
}

enum class AppType(val code: Int) {
    CAMERA(0), SNS(1), BROWSER(2), DOCUMENT(3), GAME(4), MAP(5), DATING(6), MEDIA(7), SHOPPING(8), OTT(9), UTILS(10), BANK(11), MESSENGER(12), MUSIC(13), ENTERTAINMENT(14)
}