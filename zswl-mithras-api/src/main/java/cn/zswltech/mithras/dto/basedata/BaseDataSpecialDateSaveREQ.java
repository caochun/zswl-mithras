package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2022/11/4
 * @description
 */
@Data
@ApiModel("基础数据-特殊日期-保存请求体")
public class BaseDataSpecialDateSaveREQ {
    @ApiModelProperty("主键id，新增为空")
    private Long id;

    @ApiModelProperty("日期，yyyy-MM-dd")
    @NotBlank(message = "日期不能为空")
    private String specialDate;

    @ApiModelProperty("类型 WORKDAY-工作日 HOLIDAY-节假日")
    @NotBlank(message = "类型不能为空")
    private String specialType;
}
