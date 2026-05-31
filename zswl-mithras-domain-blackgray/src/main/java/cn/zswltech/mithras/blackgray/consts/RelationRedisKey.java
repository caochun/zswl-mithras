package cn.zswltech.mithras.blackgray.consts;

public class RelationRedisKey {

    public static final Long DRILL_API_RESULT_CACHE_SEC = 60 * 60 * 24L; // 默认缓存1天
    public static final Long SEC_PER_HOUR = 60 * 60L;

    public static final String PARTY_IMPORT_CACHE_KEY = "PARTY_IMPORT_CACHE_KEY:";
    public static final String TRADE_IMPORT_CACHE_KEY = "TRADE_IMPORT_CACHE_KEY:";


    // 标记钻取任务正在执行的锁，期间用户无法查看或操作变更数据
    public static final String PARTY_DRILL_TASK_RUNNING = "PARTY_DRILL_TASK_RUNNING";

    // creditCode-enterpriseCode映射关系存储
    public static final String DRILL_CREDIT_CODE_TO_ENTERPRISE_CODE = "DRILL_CREDIT_CODE_TO_ENTERPRISE_CODE:";

    public static final String DRILL_EP_MAIN_BY_CREDIT_CODE = "DRILL_EP_MAIN_BY_CREDIT_CODE:";

    public static final String DRILL_EP_MAIN_BY_NAME = "DRILL_EP_MAIN_BY_NAME:";

    public static final String DRILL_EP_INFO_BY_CREDIT_CODE = "DRILL_EP_INFO_BY_CREDIT_CODE:";

    public static final String DRILL_EP_INFO_BY_NAME = "DRILL_EP_INFO_BY_NAME:";

    public static final String DRILL_ENT_UP_HOLDER_API_KEY = "DRILL_ENT_UP_HOLDER_API_KEY:";

    public static final String DRILL_NATURE_DOWN_HOLDER_API_KEY = "DRILL_NATURE_DOWN_HOLDER_API_KEY:";

    public static final String DRILL_ENT_DOWN_HOLDER_API_KEY = "DRILL_ENT_DOWN_HOLDER_API_KEY:";

    public static final String DRILL_2LEVEL_ENT_DOWN_HOLDER_API_KEY = "DRILL_2LEVEL_ENT_DOWN_HOLDER_API_KEY:";

    public static final String DRILL_ACTUAL_CONTROL_API_KEY = "DRILL_ACTUAL_CONTROL_API_KEY:";

    public static final String DRILL_BENEFICIARY_API_KEY = "DRILL_BENEFICIARY_API_KEY:";

    public static final String DRILL_COMPANY_BASIC_INFO_KEY = "DRILL_COMPANY_BASIC_INFO_KEY:";

    public static final String DRILL_COMPANY_INFO_KEY = "DRILL_COMPANY_INFO_KEY:";

    public static final String DRILL_COMPANY_STAFF_KEY = "DRILL_COMPANY_STAFF_KEY:";

    public static final String DRILL_AC_BY_CREDIT_CODE = "DRILL_AC_BY_CREDIT_CODE:";
    public static final String DRILL_DIRECTOR_BY_CREDIT_CODE = "DRILL_DIRECTOR_BY_CREDIT_CODE:";
    public static final String DRILL_BE_BY_CREDIT_CODE = "DRILL_BE_BY_CREDIT_CODE:";
    public static final String DRILL_INDUSTRY_BY_ENTERPRISE_CODE = "DRILL_INDUSTRY_BY_ENTERPRISE_CODE_STD32:";
    public static final String DRILL_ADDRESS_DO_BY_ENTERPRISE_CODE = "DRILL_ADDRESS_DO_BY_ENTERPRISE_CODE:";


    // 不受缓存开关配置影响，永远开启，缓存恒生的区域字典数据与innerCode对应关系
    public static final String ALWAYS_LC_AREA_BY_INNER_CODE = "ALWAYS_LC_AREA_BY_INNER_CODE:";

    public static final String NULL_FLAG = "NIL";
}
