package cn.zswltech.mithras.dto.client.client;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/19
 * @description
 */
@Data
@ApiModel("导出客户转移模版-请求体")
public class ClientTransferExportREQ extends VersionBaseREQ {
    @NotNull(message = "客户ids不能为空")
    @ApiModelProperty("客户ids")
    private List<Long> clientIds;

    @NotNull
    @ApiModelProperty("客户经理id")
    private Long belongSponsorId;


    @NotNull
    @ApiModelProperty("所属部门id")
    private Long belongDeptId;


    @ApiModelProperty("审批流中的businessKey")
    private String batchNo;
}
