package cn.zswltech.mithras.customer.dto.client;

import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class ClientBasicInfoDTO {
   //客户ID
    private Long clientId;
   //客户名称
    private String clientName;
    //分控行业分类
    private String riskControlIndustryClassifyCode;
   //国标行业分类code
    private String industryTypeCode;
   //企业性质code
    private String enterpriseNatureCode;
   //省份code
    private String provinceCode;
   //业务部门ID
    private Long bizDeptId;
   //项目主办ID
    private Long projSponsorUserId;
    private Long createBy;
    private String clientType;
}
