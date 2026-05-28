package cn.zswltech.mithras.others.hand.extract.contract;

import com.alibaba.fastjson.JSONObject;
import cn.zswltech.mithras.others.hand.extract.HandRequestUtil;

/**
 * 合同模块 汉得 http提取
 *
 * @author wangchuanhao
 * @date 2022/9/23 10:49 AM
 */
public class ContractHandRequestUtil {

    /**
     * 合同基本信息
     * @return
     */
    public static String baseList() {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractMapper.queryConContractDetails&layout_code=CONT301&tab_code=G_CON&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                JSONObject.parseObject("{\"randomString\":\"0.8943137614543217Thu Sep 22 2022 14:11:00 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

    /**
     * 客户信息
     * @param bodyData
     * @return
     */
    public static String clientInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractBpMapper.queryContractBp&layout_code=CONT303F1&tab_code=G_BP&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

    /**
     * 现金流
     * @param bodyData
     * @return
     */
    public static String cashflowInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper.queryConContractCashflowDetail&layout_code=CONT303F1&tab_code=PRJ_QUOTATION_CASHFLOW&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

    /**
     * 银行账号
     * @param bodyData
     * @return
     */
    public static String bankAccountInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.ContractBankAccountMapper.queryConContractAccountDetails&layout_code=CONT303F1&tab_code=G_LOAN_ACCOUNT_NUMBER&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

    /**
     * 担保措施
     * @param bodyData
     * @return
     */
    public static String guaranteeInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.ContractGuaranteeMapper.queryContractGuaranteeDetail&layout_code=CONT303F1&tab_code=G_GUARANTEE_INFORMATION&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

    /**
     * 抵押措施
     * @param bodyData
     * @return
     */
    public static String mortgageInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractMortgageMapper.queryContractMortgageDetail&layout_code=CONT303F1&tab_code=G_MORTGAGE&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

    /**
     * 质押物
     * @param bodyData
     * @return
     */
    public static String pledgeInfo(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractMortgageMapper.queryContractMortgageDetailOne&layout_code=CONT303F1&tab_code=G_MORTGAGE_PG&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

    /**
     * 资料清单
     * @param bodyData
     * @return
     */
    public static String materialsList(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractAttachmentMapper.queryConContractAttachment&layout_code=CONT303F1&tab_code=G_ATTACHMENT&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

    /**
     * 资料清单详情（点开下载后的tab页）
     * @param headerId
     * @return
     */
    public static String materialsDetail(String headerId) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/fnd/attachment/query?headerId=" + headerId + "&tableName=con_contract_attachment&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=false",
                JSONObject.parseObject("{\"randomString\":\"0.47257490015080705Fri Sep 23 2022 14:31:37 GMT+0800 (中国标准时间)\"}"),
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

    /**
     * 报价信息
     * @param bodyData
     * @return
     */
    public static String prjQuotation(JSONObject bodyData) {
        return HandRequestUtil.doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper.queryPrjQuotationDetail&layout_code=CONT303F1&tab_code=G_QUOTATION&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true",
                bodyData,
                "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10648&function_code=CONT301Q",
                "modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10648%26function_code%3DCONT301Q"
        );
    }

}
