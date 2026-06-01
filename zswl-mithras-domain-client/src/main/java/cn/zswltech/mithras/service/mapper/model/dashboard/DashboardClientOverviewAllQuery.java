package cn.zswltech.mithras.service.mapper.model.dashboard;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@Data
public class DashboardClientOverviewAllQuery {
    //客户名称
    private String clientName;
    private Long clientId;
    private Set<Long> limitClientIds;
    private List<Long> authBizDeptIds;
    private Long authCurrentUserId;

    //风控行业分类
    private String riskControlIndustryClassifyCode;
    //资产五级分类code AssetClassifyResultEnum
    private String assetClassifyResultCode;
    //省份
    private String provinceCode;
    //业务部门ID
    private Long bizDeptId;
    //项目主办ID
    private Long projSponsorUserId;
    private LocalDate createFrom;
    private LocalDate createTo;
    private List<String> clientStatus;

    //结清时间
    private LocalDate dealLineFrom;
    private LocalDate dealLineTo;

    private List<Long> clientIds;
}
