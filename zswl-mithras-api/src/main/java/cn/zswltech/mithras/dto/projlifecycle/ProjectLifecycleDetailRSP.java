package cn.zswltech.mithras.dto.projlifecycle;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @create: 2022-10-23
 **/

@Data
public class ProjectLifecycleDetailRSP {
    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("项目主办部门")
    private String projSponsorDeptName;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty("项目主办名")
    private String projSponsorUserName;

    @ApiModelProperty("客户信息")
    private List<ClientInfo> clientInfos;

    @Data
    public static class ClientInfo {
        @ApiModelProperty("客户id")
        private Long clientId;

        @ApiModelProperty("客户名称")
        private String clientName;

        @ApiModelProperty("客户类型")
        private String clientType;


        @ApiModelProperty("客户类别")
        private String clientCategory;

    }
}
