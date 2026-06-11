package cn.zswltech.mithras.blackgray.constant;

public class PortraitRedisKeyConstants {
    /*
        PortraitServiceImpl中通过warpHttpGet方法请求恒生接口之前的前置缓存统一key
        完整key = WRAP_REQUEST:{url最后一段}?{query...} 如 WRAP_REQUEST:staff?enterprise_name=中润经济发展有限责任公司
     */
    public static final String WRAP_REQUEST = "WRAP_REQUEST:";

    public static final String OUTER_SEARCH = "OUTER_SEARCH:";

    /*
        24小时的秒数，目前该缓存默认保存24小时
     */
    public static final long SEC_OF_DAY = 24 * 60 * 60;

    public static final String HAS_UPPER_NODES = "HAS_UPPER_NODES:";
}
