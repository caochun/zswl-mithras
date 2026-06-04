package cn.zswltech.mithras.datashare.service.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * @author: ldhu
 * @Data: 2026/3/2
 * @desc:
 */

@Getter
@Setter
public class ArchivesReq {
    /*
    1001//编辑中/已撤回/已驳回/审核驳回
    1002//审批中
    1003//待审核
    1004//审核通过
    1005//已付款/流程结束
    1007//待付款
    1008//付款中
    1015//取消支付
    */
    private List<Integer> statusList; // 状态
    private String lastModifyStartDate; // 查询开始日期，可以搭配dateSearchField指定过滤的时间字段 格式为2017-04-24 15:00:00
    private String lastModifyEndDate;// 查询结束日期，可以搭配dateSearchField指定过滤的时间字段
    private Set<String> companyOIDList; // 单据申请人公司OID集合
    private Set<String> companyCodeList; // 单据申请人公司code集合
    private Set<String> docCompanyOIDList; // 单据头业务公司OID集合
    private Set<String> docCompanyCodeList; // 单据业务公司code集合
    private Boolean printFree;// 查询的报销单是否免打印
    private Set<String> applicantEmployeeIdList; // 申请人工号集合
    /*
    0//未收单
    1//已收单
    2//已退单
    3//待退单
    */
    private List<Integer> receiveStatusList;//收单状态
    /*
    0//未寄单
    1//已寄单
    2//已退单
    3//待退单
    */
    private List<Integer> sendBillStatusList;//寄单状态
    private List<Integer> businessCodeList;//单号集合
    private List<String> formCodeList;//表单code集合
    private Boolean corporateFlag; //是否查询对公支付单，默认为false
    private Boolean queryIncludeArchived; //是否查询包含已归档单据，默认为false
    private List<sortDTO> sortDTOList; // 是否根据条件排序，默认为最后修改日期(lastModifyDate)降序和(id)升序排序   暂时支持id和lastModifyDate两个字段排序 升序：ASC 降序：DESC
    private String dateSearchField;// 日期搜索属性 最后更新日期:lastModifyDate，最后提交日期:lastSubmittedDate，付款日期:realPaymentDate，记账日期:bookDate，审批通过日期:approvalDate，审核通过日期:auditApprovalDate，默认值为最后更新日期 如果传的值不在范围则走默认值

    @Setter
    @Getter
    @AllArgsConstructor
    public static class sortDTO {
        private String property;
        private String direction;
    }
}
