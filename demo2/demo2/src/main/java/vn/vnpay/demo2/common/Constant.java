package vn.vnpay.demo2.common;

public class Constant {
    public static final String REQUEST_ID_PREFIX = "requestId";
    public static final int NETTY_PORT = 8082;
    public static final String CONFIG_FILE = "config.properties";
    public static final String COMMAND_CODE_PREFIX = "FC";
    public static final String TRANSACTION_CODE_PREFIX = "TC";
    public static final int limitScan = 5;
    public static final long cronjob_period = 20;
    public static final long cronjob_initial_delay = 40;
    public static final int initial_scan = 1;
    public static final String REDIRECT_AUTHENTICATION_URL = "http://localhost:8008/demo3/authorization";
    public static final String VERIFY_TOKEN_EXTERNAL = "http://localhost:8008/verify/accessToken";
    public static final String GET_ACCOUNT_INFO_EXTERNAL = "http://localhost:8008/demo3/getAccountInfo";
    public static final String CLIENT_ID = "fee-client";
    public static final String CLIENT_SECRET = "M/hYpnrDvLhr8Ecwio9JNQ==";
    public static final String SECRET_KEY = "hh1suaAIrHvuIg00o4a7ADIvwuW+zyRQIKJVg98Ya8gwfix4tgXaD27wCBlbh7PV";
    public static final String WELCOME_MESSAGE = "Welcome, ";

}
