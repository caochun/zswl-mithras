package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 绩效-拨备表
 * @author vico
 * @date 2023-06-19
 */
@Data
@ApiModel("绩效-拨备表列表-返回体")
public class KpiProvisionBaseInfoListRSP {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 创建月份
     */
    @ApiModelProperty(value = "创建月份")
    private LocalDate provisionDate;

    /**
     * 拨备状态 KpiProvisionStatusEnum
     */
    @ApiModelProperty(value = "拨备状态 KpiProvisionStatusEnum")
    private String provisionStatus;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private Long createBy;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createByName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

}
