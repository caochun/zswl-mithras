package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 绩效-拨备表
 * @author vico
 * @date 2023-06-19
 */
@Data
@ApiModel("绩效-拨备表列表-请求体")
public class KpiProvisionBaseInfoListREQ extends PageReq {

    /**
     * 创建月份
     */
    @ApiModelProperty(value = "创建月份")
    private LocalDate provisionDate;

}
