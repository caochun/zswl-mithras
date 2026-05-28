package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxiong
 * @description 租赁物查重返回体，暂时一个字段用一个类，方便后续拓展字段
 * @since 2023-09-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseCheckRepeatRSP {

    @ApiModelProperty(value = "中登网查重日期, 格式：yyyy-MM-dd HH:mm:ss")
    private String repeatDate;

    @ApiModelProperty(value = "关联流程ID")
    private String relevanceFlowId;
}
