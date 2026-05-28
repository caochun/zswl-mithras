package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 预算管理-预算考核
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核列表-请求体")
public class BudgetExamineListREQ extends PageReq {

    /**
     * 考核年份
     */
    @ApiModelProperty(value = "考核年份")
    private Integer examineYear;

    /**
     * 考核月份
     */
    @ApiModelProperty(value = "考核月份")
    private Integer examineMonth;

}
