package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 绩效-拨备表
 * @author vico
 * @date 2023-06-19
 */
@Data
@ApiModel("绩效-拨备表新增-返回体")
public class KpiProvisionBaseInfoAddRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

}
