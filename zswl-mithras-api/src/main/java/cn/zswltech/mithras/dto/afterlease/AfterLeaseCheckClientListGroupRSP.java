package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/25
 * @description
 */
@Data
@ApiModel("租后管理-季度计划项目（更改为客户）列表-返回体")
public class AfterLeaseCheckClientListGroupRSP {
    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("需检查项目（更改为客户）数量")
    private Integer toCheckCount;

    @ApiModelProperty("项目（更改为客户）检查完成数量")
    private Integer finishCount;

    @ApiModelProperty("项目（更改为客户）列表")
    private List<AfterLeaseCheckClientListRSP> clientList;
}
