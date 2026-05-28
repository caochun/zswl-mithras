package cn.zswltech.mithras.dto.dashboard;

import cn.zswltech.mithras.dto.AccountReq;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardClientOverviewSettledREQ extends PageReq {
    @ApiModelProperty("客户名称")
    private Long clientId;
    private String clientName;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    //结清时间
    private LocalDate dealLineFrom;
    private LocalDate dealLineTo;

    private AccountReq accountVo;
}
