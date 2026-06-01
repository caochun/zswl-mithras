package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BlackGrayApprovalSubmitREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/12/4 7:37 下午
 * @Version 1.0
 **/
@Data
@ApiModel("黑灰名单提交审批-请求体")
public class BlackGrayApprovalSubmitREQ {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "下一审批人")
    private String auditUser;

}
