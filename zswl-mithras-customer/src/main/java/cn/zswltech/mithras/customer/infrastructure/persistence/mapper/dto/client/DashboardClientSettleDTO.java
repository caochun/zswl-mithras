package cn.zswltech.mithras.customer.infrastructure.persistence.mapper.dto.client;

import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class DashboardClientSettleDTO {
   //客户ID
    private Long clientId;
   //客户名称
    private String clientName;
    //项目名称,号拼接
    private String projNames;

    //业务类型
    private String bizType;
    //合同编号
    private String contractCodes;

    //业务部门ID
    private Long bizDeptId;
    //项目主办ID
    private Long projSponsorUserId;

    //分控行业分类
    private String riskControlIndustryClassifyCode;
   //国标行业分类code
    private String industryTypeCode;
    //资产五级分类code
    private String assetClassifyResultCode;
   //企业性质code
    private String enterpriseNatureCode;
   //省份code
    private String provinceCode;

   //评级结果（预留）
    private String gradeResult;
}
