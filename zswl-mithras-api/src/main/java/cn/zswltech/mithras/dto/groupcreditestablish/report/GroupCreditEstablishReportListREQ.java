package cn.zswltech.mithras.dto.groupcreditestablish.report;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 立项报告文件列表-请求体
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:43 AM
 */
@Data
@ApiModel("立项报告文件列表-请求体")
public class GroupCreditEstablishReportListREQ extends PageReq {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
