package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class UrgeCollectionRecord {
    @ApiModelProperty("催收日期")
    private LocalDate urgeDate;
    @ApiModelProperty("催收人")
    private String urgePerson;
    @ApiModelProperty("催收备注")
    private String urgeRemark;
    @ApiModelProperty("催收通知单")
    private String urgeNoticeLetter;
}
