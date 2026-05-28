package cn.zswltech.mithras.dto.dashboard;

import cn.zswltech.mithras.dto.AccountReq;
import cn.zswltech.mithras.dto.PageReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DashboardClientOverviewOverdueREQ extends PageReq {
    @ApiModelProperty("客户Id")
    private String clientId;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty(value = "开始时间")
    @JsonIgnore
    private LocalDateTime beginTime;
    @ApiModelProperty(value = "结束时间")
    @JsonIgnore
    private LocalDateTime endTime;

    private AccountReq accountVo;

    private List<Long> ids;
}
