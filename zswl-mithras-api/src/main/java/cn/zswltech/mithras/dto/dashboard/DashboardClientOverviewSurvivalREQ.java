package cn.zswltech.mithras.dto.dashboard;

import cn.zswltech.mithras.dto.AccountReq;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardClientOverviewSurvivalREQ extends PageReq {
    @ApiModelProperty("客户名称")
    private String clientName;
    private Long clientId;
    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassifyCode;
    @ApiModelProperty("资产五级分类code")
    private String assetClassifyResultCode;
    @ApiModelProperty("省份")
    private String provinceCode;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;

    private LocalDate createFrom;
    private LocalDate createTo;

    private AccountReq accountVo;

    private List<Long> clientIds;

}
