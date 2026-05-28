package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/9
 * @description
 */
@Data
@ApiModel("租后检查-业务部门项目（更改为客户）汇总信息-返回体")
public class AfterLeaseCheckClientDeptInfoRSP {
    @ApiModelProperty("业务部门id")
    private Long deptId;
    @ApiModelProperty("业务部门名称")
    private String deptName;
    @ApiModelProperty("客户id列表")
    private List<Long> clientIdList;
    @ApiModelProperty("需检查客户数量")
    private Integer toCheckCount;
}
