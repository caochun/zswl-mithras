package cn.zswltech.mithras.service.overdue.application.query;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:41
 */
@Data
@ApiModel(value = "文书用印列表查询参数")
public class PrintingPageQuery extends PageReq {

    @ApiModelProperty(value = "用印类型")
    private String type;
    @ApiModelProperty(value = "申请人")
    private Long applicant;
}
