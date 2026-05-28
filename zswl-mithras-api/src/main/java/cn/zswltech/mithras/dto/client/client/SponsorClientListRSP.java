package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yibin
 */
@Data
@ApiModel("获取指定用户负责的客户列表-返回体")
public class SponsorClientListRSP {

    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("客户id")
    private Long id;
    @ApiModelProperty("客户编号")
    private String clientCode;
    @ApiModelProperty("客户类型")
    private String clientType;
    @ApiModelProperty("当前所属经理id")
    private Long belongSponsorId;
    @ApiModelProperty("当前所属经理名称")
    private String belongSponsorName;
    @ApiModelProperty("当前所属部门id")
    private Long belongDeptId;
    @ApiModelProperty("当前所属部门名称")
    private String belongDeptName;
    @ApiModelProperty("是否处于流程中")
    private Boolean inProcess;
    @ApiModelProperty("流程状态")
    private String processStatus;
    @ApiModelProperty("立项列表")
    private List<ClientProjRSP> clientProjRSPList;

    @Data
    public static class ClientProjRSP {
        @ApiModelProperty("项目名称")
        private String projectName;
        @ApiModelProperty("项目编号")
        private String projCode;
        @ApiModelProperty("业务类型")
        private String bizType;
        @ApiModelProperty("是否在流程中")
        private Boolean inProcess = false;
    }

}
