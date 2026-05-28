package cn.zswltech.mithras.others.hand.extract.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.zswltech.mithras.others.hand.extract.HandRequestUtil;
import lombok.SneakyThrows;

/**
 * 汉得客户提取http工具类
 *
 * @author wangchuanhao
 * @date 2022/9/22 2:08 PM
 */
public class ClientHandRequestUtil {

    /**
     * 基本信息列表
     * @return
     */
    @SneakyThrows
    public static String baseList() {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterMapper.queryNew&layout_code=PRJ307&tab_code=G_QUERY_RESULT&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                JSONObject.parseObject("{\"randomString\":\"0.8943137614543217Thu Sep 28 2022 14:11:00 GMT+0800 (中国标准时间)\"}"),
                        "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                        "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人基本信息
     * @return
     */
    public static String corpBaseInfo(String bpId) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterMapper.queryDetails&layout_code=PRJ306F1&tab_code=F_BILLING&bp_seq=&pagesize=10&pagenum=1&pageSize=10&pageNum=1&_fetchall=false&_autocount=true",
                JSONObject.parseObject("{\"bp_id\":\"" + bpId + "\",\"randomString\":\"0.847252542507791Thu Sep 22 2022 14:14:00 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人管理层
     * @param bodyData
     * @return
     */
    public static String corpMasterInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.BpMasterManageMapper.queryDetails&layout_code=PRJ306F1&tab_code=G_MANAGE&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人关联方关系
     * @param bodyData
     * @return
     */
    public static String corpRelatedInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.BpMasterGroupMapper.query&layout_code=PRJ306F1&tab_code=GROUP&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    public static String normalBaseInfo(String bpId) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterMapper.queryForNp&layout_code=PRJ306F2&tab_code=F_SPOUSE&bp_seq=&pagesize=10&pagenum=1&pageSize=10&pageNum=1&_fetchall=false&_autocount=true",
                JSONObject.parseObject("{\"bp_id\":\"" + bpId + "\",\"randomString\":\"0.847252542507791Thu Sep 22 2022 14:14:00 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人地址信息
     * @param bodyData 详情接口的全量数据
     * @return
     */
    public static String corpAddressInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterAddressMapper.queryDetails&layout_code=PRJ306F1&tab_code=G_ADD&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人联系人信息
     * @param bodyData 详情接口的全量数据
     * @return
     */
    public static String corpContractInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterContactInfoMapper.query&layout_code=PRJ306F1&tab_code=G_CONTACT&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人股东信息
     * @param bodyData
     * @return
     */
    public static String corpShareholderInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.HlsBpMasterShareholderMapper.query&layout_code=PRJ306F1&tab_code=G_SHAREHOLDER&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人银行账号
     * @param bodyData
     * @return
     */
    public static String corpBankAccountInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.HlsBpMasterBankAccountMapper.query&layout_code=PRJ306F1&tab_code=G_BANK&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人资料清单列表
     * @param bodyData
     * @return
     */
    public static String corpMaterialsList(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.HlsCusBpMasterAttachmentMapper.queryList&layout_code=PRJ306F1&tab_code=G_DOCUMENT_LIST&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 自然人资料清单列表
     * @param bodyData
     * @return
     */
    public static String normalMaterialsList(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusBpMasterAttachmentMapper.queryList&layout_code=PRJ306F2&tab_code=G_DOCUMENT_LIST&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 附件列表
     * @param bodyData
     * @return
     */
    public static String attachmentList(JSONObject bodyData, String layoutCode) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusBpMasterAttachmentMapper.query&layout_code=" + layoutCode + "&tab_code=MASTER_ATTACHMENT&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 资料清单详情（点开下载后的tab页）
     * @return
     */
    public static String materialsDetail(String headerId) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/fnd/attachment/query?headerId=" + headerId + "&tableName=hls_bp_master_attachment&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=false",
                JSONObject.parseObject("{\"randomString\":\"0.3423908841268022Thu Sep 22 2022 14:42:54 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 自然人银行账户
     * @return
     */
    public static String normalBankAccountInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.HlsBpMasterBankAccountMapper.query&layout_code=PRJ306F2&tab_code=G_BANK&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人财报
     * @param bpId
     * @return
     */
    public static String corpSubjectList(String bpId) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/hls/bp/master/financial/hd/query?bp_id=" + bpId + "&documentCategory=HLS_BP_MASTER&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                JSONObject.parseObject("{\"randomString\":\"0.6390456521355425Thu Sep 22 2022 18:10:10 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 法人财报详情
     * @param statementHdId 财报id
     * @param lineType 财报类型
     * BALANCE_SHEET 资产负债表
     * PROFIT_STATEMENT 利润表
     * CASH_FLOW_STATEMENT 现金流量表
     * FINANCIAL_INDEX 业务指标表
     * @return
     */
    public static String corpSubjectDetail(String statementHdId, String lineType) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/hls/bp/master/financial/ln/query?fin_statement_hd_id=" + statementHdId + "&lineType=" + lineType + "&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                JSONObject.parseObject("{\"randomString\":\"0.7280762909000396Fri Sep 23 2022 09:42:47 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 自然人联系人
     * @param bodyData
     * @return
     */
    public static String normalContractInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.HlsBpMasterContactInfoMapper.query&layout_code=PRJ306F2&tab_code=G_CONTACT&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    /**
     * 自然人地址信息
     * @param bodyData
     * @return
     */
    public static String normalAddressInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterAddressMapper.queryDetails&layout_code=PRJ306F2&tab_code=G_ADD&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
        );
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(corpSubjectDetail("372", "BALANCE_SHEET")));
    }

}
