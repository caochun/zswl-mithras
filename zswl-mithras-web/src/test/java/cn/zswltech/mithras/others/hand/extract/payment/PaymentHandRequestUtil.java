package cn.zswltech.mithras.others.hand.extract.payment;

import com.alibaba.fastjson.JSONObject;
import cn.zswltech.mithras.others.hand.extract.HandRequestUtil;

/**
 * 付款http工具类
 *
 * @author wangchuanhao
 * @date 2022/9/23 4:01 PM
 */
public class PaymentHandRequestUtil {

    /**
     * 付款申请维护
     * @return
     */
    public static String baseList() {
        return HandRequestUtil.doPost("http://10.100.222.10/core/csh/payment/req/hd/query?pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                JSONObject.parseObject("{\"randomString\":\"0.8943137614543217Thu Sep 22 2022 14:11:00 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 付款申请详情
     * @return
     */
    public static String baseInfo(String paymentId) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.csh.mapper.SnzlCusCshPaymentReqLnMapper.queryHd&layout_code=CSH302F1&tab_code=F_OTHER%20SITUATIONS&bp_seq=&document_category=CSH_PAYMENT_REQ&document_type=PAYMENT_REQ&pagesize=10&pagenum=1&pageSize=10&pageNum=1&_fetchall=false&_autocount=true",
                JSONObject.parseObject("{\"payment_req_id\":" + paymentId + ",\"randomString\":\"0.8943137614543217Thu Sep 22 2022 14:11:00 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 本次支付明细
     * @param bodyData
     * @return
     */
    public static String paymentFlowList(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.csh.mapper.SnzlCusCshPaymentReqLnMapper.queryHd&layout_code=CSH302F1&tab_code=F_OTHER%20SITUATIONS&bp_seq=&document_category=CSH_PAYMENT_REQ&document_type=PAYMENT_REQ&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 资料清单
     * @param bodyData
     * @return
     */
    public static String materialsList(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.csh.mapper.PaymentReqAttachmentMapper.query&layout_code=CSH302F1&tab_code=G_CSH_PAYMENT&bp_seq=&document_category=CSH_PAYMENT_REQ&document_type=PAYMENT_REQ&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 资料清单详情（点开下载后的tab页）
     * @param headerId
     * @return
     */
    public static String materialsDetail(String headerId) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/fnd/attachment/query?headerId=" + headerId + "&tableName=csh_payment_req_attachment&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=false",
                JSONObject.parseObject("{\"randomString\":\"0.3423908841268022Thu Sep 22 2022 14:42:54 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 问卷模块1 合同主体、租赁物、抵质押物相关
     * @param bodyData
     * @return
     */
    public static String paymenyAnswer1(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.csh.mapper.PaymentReqContractItemMapper.selectBasic&layout_code=CSH302F1&tab_code=G_CSH_REQUIREMENT_1&bp_seq=&document_category=CSH_PAYMENT_REQ&document_type=PAYMENT_REQ&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 问卷模块2 付款套件签署相关
     * @param bodyData
     * @return
     */
    public static String paymenyAnswer2(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.csh.mapper.PaymentReqSuiteToSignMapper.selectBasic&layout_code=CSH302F1&tab_code=G_CSH_REQUIREMENT_4&bp_seq=&document_category=CSH_PAYMENT_REQ&document_type=PAYMENT_REQ&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 问卷模块3 付款手续相关
     * @param bodyData
     * @return
     */
    public static String paymenyAnswer3(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.csh.mapper.PaymentReqFormalitiesMapper.selectBasic&layout_code=CSH302F1&tab_code=G_CSH_REQUIREMENT_3&bp_seq=&document_category=CSH_PAYMENT_REQ&document_type=PAYMENT_REQ&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 问卷模块4 项目最终有权决议机构决议放款/起租条件的达成情况
     * @param bodyData
     * @return
     */
    public static String paymenyAnswer4(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.csh.mapper.PaymentReqLoanStartTermMapper.selectBasic&layout_code=CSH302F1&tab_code=G_CSH_REQUIREMENT_2&bp_seq=&document_category=CSH_PAYMENT_REQ&document_type=PAYMENT_REQ&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 问卷模块5 项目最终有权决议机构决议限制性措施的达成情况
     * @param bodyData
     * @return
     */
    public static String paymenyAnswer5(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.csh.mapper.PaymentReqRestrictiveMapper.selectBasic&layout_code=CSH302F1&tab_code=G_CSH_REQUIREMENT_5&bp_seq=&document_category=CSH_PAYMENT_REQ&document_type=PAYMENT_REQ&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

    /**
     * 附件列表
     * @param bodyData
     * @return
     */
    public static String attachmentList(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.csh.mapper.PaymentReqAttachmentMapper.queryList&layout_code=CSH302F1&tab_code=G_CSH_ATTCHMENT&bp_seq=&document_category=CSH_PAYMENT_REQ&document_type=PAYMENT_REQ&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview?function_group_id=10293&function_code=CSH302",
                "modules/CSH/CSH_TRX/CSH302/csh_payment_req_maintain.lview%3Ffunction_group_id%3D10293%26function_code%3DCSH302"
        );
    }

}
