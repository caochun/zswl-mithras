package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;

/**
 * @description 绩效考核-参数设置基本表
 * @author vico
 * @date 2024-09-21
 */
@Data
@ApiModel("绩效考核-参数设置基本表列表-返回体")
public class KpiParameterBaseListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 生效月份
    */
    @ApiModelProperty(value = "生效月份 RecordStatus")
    private LocalDate effectMonth;

    /**
    * 参数状态
    */
    @ApiModelProperty(value = "参数状态")
    private String parameterStatus;

    private Long createBy;

    @ApiModelProperty(value = "创建人名称")
    private String createByName;

}
