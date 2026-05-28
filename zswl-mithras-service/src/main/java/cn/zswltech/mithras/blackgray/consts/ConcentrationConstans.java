package cn.zswltech.mithras.blackgray.consts;


import java.util.Arrays;
import java.util.List;

/**
 * @Author:fengming.dai
 */
public class ConcentrationConstans {
    public static final List<String> CODE = Arrays.asList("I10000079_xz197",
            "I10000395_xz198","I10000079_xz196","I10000395_xz199"
    );

    /**
     * rdis 缓存公司所属集团名称和所属集团主企业名称的key
     */
    public static final String GROUP_TITLE_BY_COMPANY_NAME = "GROUP_TITLE_BY_COMPANY_NAME:";
    public static final String GROUP_CONTROLLER_BY_COMPANY_NAME = "GROUP_CONTROLLER_BY_COMPANY_NAME:";


    // 站内信待办的标题&内容&邮件标题。
    public static final  String CONTENT = "【集中度管理】客户信息报送待处理";

    /**
     * 国资委 映射 type 行业
     */
    public static  final  byte INDUSTRY = (byte)2;

}
