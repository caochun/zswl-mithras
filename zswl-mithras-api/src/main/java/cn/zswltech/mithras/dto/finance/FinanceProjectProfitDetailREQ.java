package cn.zswltech.mithras.dto.finance;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("财务管理-项目利润-详情-请求参数")
public class FinanceProjectProfitDetailREQ extends PageReq {
    @ApiModelProperty("项目利润记录id")
    @NotNull(message = "项目利润id不能为空")
    private Long projectProfitId;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("主办id")
    private Long sponsorUserId;

    @ApiModelProperty("测算利润月份入参")
    private LocalDate yearAndMonth;
}
