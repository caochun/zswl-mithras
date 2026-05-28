package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/26
 * @description
 */
@Data
public class DashboardProjectPledgeREQ {
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("质押/监管情况 PledgeTypeEnum")
    private String pledgeStatus;
    @ApiModelProperty("融资状态")
    private String financingStatus;
    @ApiModelProperty("融资编号")
    private String financingCode;
    private List<Long> ids;
}
