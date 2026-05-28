package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author: heng
 * @Date: 2025/12/3 17:17
 */
@Data
@ApiModel("租后管理-检查台账列表-请求体")
public class AfterLeaseCheckLedgerListREQ extends PageReq {

    @ApiModelProperty("业务部门ID")
    private List<Long> belongDeptId;

    @ApiModelProperty("当前状态")
    private Integer checkStatus;

    @ApiModelProperty("是否逾期")
    private Integer overdue;

    @ApiModelProperty("租后截止日期开始")
    private LocalDate deadLineFrom;

    @ApiModelProperty("租后截止日期结束")
    private LocalDate deadLineTo;

}
