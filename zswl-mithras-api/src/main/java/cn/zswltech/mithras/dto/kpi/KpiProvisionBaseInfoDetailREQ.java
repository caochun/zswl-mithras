package cn.zswltech.mithras.dto.kpi;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 绩效-拨备表
 * @author vico
 * @date 2023-06-19
 */
@Data
@ApiModel("绩效-拨备表详情-请求体")
public class KpiProvisionBaseInfoDetailREQ extends PageReq {

    //@NotNull
    @ApiModelProperty("id")
    private Long id;

    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    /**
     * 利润所属部门id
     */
    @ApiModelProperty(value = "利润所属部门id")
    private Long profitBelongDeptId;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

}
