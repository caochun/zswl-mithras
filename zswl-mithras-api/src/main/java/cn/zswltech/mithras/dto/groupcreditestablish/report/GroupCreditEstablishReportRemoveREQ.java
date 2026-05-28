package cn.zswltech.mithras.dto.groupcreditestablish.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@ApiModel("集团授信立项报告删除删除-请求体")
@Data
public class GroupCreditEstablishReportRemoveREQ {

    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private Long id;
}
