package cn.zswltech.mithras.third.datashare.service.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: ldhu
 * @Data: 2026/3/2
 * @desc: /api/open/expenseReport/auto/audit/result/detail?businessCode={businessCode}&corporateFlag={corporateFlag}&entityType={entityType}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailReq {
    private String businessCode; //单号和recordId不能同时为空，有recordId优先使用recordId进行处理。使用单号只能查未删除单据的最新一次智能审核结果
    private Boolean corporateFlag; //非必传，仅针对报销单/对公支付单有效，防止报销单和对公支付单单号重复 true:对公支付单 false:报销单 默认为空
    private Integer entityType;//1001：申请单/对公申请单，1002：报销单/对公支付单。不传默认1002
    private Long recordId;//单号和recordId不能同时为空，有recordId优先使用recordId进行处理。使用recordId可以查已删除单据
}
