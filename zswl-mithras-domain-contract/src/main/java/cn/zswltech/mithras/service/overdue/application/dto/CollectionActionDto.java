package cn.zswltech.mithras.service.overdue.application.dto;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/23 10:08
 */
@Data
@ApiModel(value = "催收动作列表")
public class CollectionActionDto extends ListBaseRSP {

    @ApiModelProperty(value = "催收id")
    private Long ocId;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "日期")
    private LocalDate date;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "进展/发函原因")
    private String describe;

    @ApiModelProperty(value = "人员")
    private String processPerson;

    @ApiModelProperty(value = "发函类型")
    private String letterType;

    @ApiModelProperty(value = "合同id")
    private List<Long> contractIds;

    @ApiModelProperty(value = "合同codes")
    private List<String> contractCodes;

    @ApiModelProperty(value = "审批状态")
    private String processStatus;


    public void setData(LocalDateTime date) {
        if (date == null) {
            return;
        }
        this.date = date.toLocalDate();
    }
}
