package cn.zswltech.mithras.creditreport.service.req;

import cn.zswltech.mithras.creditreport.enums.CreditReportQueryReasonEnum;
import lombok.Data;

/**
 * @ClassName CreditReportBaseReq
 * @Description 单笔查询请求
 * @Author jackerhe
 * @Date 2025/11/21 09:07
 * @Version 1.0
 **/
@Data
public class CreditReportQueryReportReq extends CreditReportBaseReq {

    //企业名称
    private String entName;

    //企业身份标识类型
    private String entCertType;

    //企业身份标识号码
    private String entCertNum;

    /**
     * {@link CreditReportQueryReasonEnum#name()}
     **/
    //查询原因
    private String queryReason;

    //查询策略 1 本地， 2 远程， 3 本地优先
    private String qryStrategy;

    //信用报告有效天数，如不在有效天内，向征信中心发起查询
    //private String effectday;

    //档案号
    private String archiveId;

}
