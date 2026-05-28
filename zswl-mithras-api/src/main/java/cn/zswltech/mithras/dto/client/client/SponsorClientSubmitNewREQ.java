package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author yibin
 */
@Data
@ApiModel("新的提交转移指定用户负责的客户-请求体")
public class SponsorClientSubmitNewREQ implements Serializable {

    @Valid
    @NotEmpty(message = "您还未选择要移除的客户")
    @ApiModelProperty("移交客户列表")
    private List<TransferClient> transferClientList;

    @ApiModelProperty("当前所属主办id")
    private Long belongSponsorId;
    @ApiModelProperty("当前所属主办名称")
    private String belongSponsorName;
    @ApiModelProperty("当前所属主办部门id")
    private Long belongDeptId;
    @ApiModelProperty("当前所属主办部门名称")
    private String belongDeptName;

    @ApiModelProperty("正式移交日期")
    private LocalDate transferDate;

    @ApiModelProperty("说明")
    private String description;

    @NotBlank
    @ApiModelProperty("审批流中的businessKey")
    private String batchNo;


    @Data
    public static class TransferClient {
        @ApiModelProperty("客户名称")
        private String clientName;
        @ApiModelProperty("客户id")
        private Long id;
        @ApiModelProperty("客户编号")
        private String clientCode;
        @ApiModelProperty("客户类型")
        private String clientType;
        @ApiModelProperty("当前所属主办id")
        private Long belongSponsorId;
        @ApiModelProperty("当前所属主办名称")
        private String belongSponsorName;
        @ApiModelProperty("当前所属主办部门id")
        private Long belongDeptId;
        @ApiModelProperty("当前所属主办部门名称")
        private String belongDeptName;
        @ApiModelProperty("是否处于流程中")
        private Boolean inProcess;
        @ApiModelProperty("流程状态")
        private String processStatus;
        @ApiModelProperty("资产五级分类结果")
        private String assertClassifyResult;
        @ApiModelProperty("项目详情")
        private List<ClientProjRSP> clientProjRSPList;
    }

    @Data
    public static class ClientProjRSP {
        @ApiModelProperty("项目名称")
        private String projectName;
        @ApiModelProperty("项目编号")
        private String projCode;
        @ApiModelProperty("合同编号")
        private String contractCode;
        @ApiModelProperty("业务类型")
        private String bizType;
        @ApiModelProperty("是否在流程中")
        private Boolean inProcess = false;
        @ApiModelProperty("新的主办id")
        private Long toSponsorId;
        @ApiModelProperty("新的主办名称")
        private String toSponsorName;
        @ApiModelProperty("新的协办id列表")
        private List<Long> toCosponsorIds;
        @ApiModelProperty("项目流状态")
        private String projStatus;
        @ApiModelProperty("项目资料归档状态")
        private String projArchiveStatus;
        @ApiModelProperty("风险移交比例")
        private Integer riskTransferValue;
        @ApiModelProperty("收益移交比例")
        private Integer IncomeTransferValue;
        @ApiModelProperty("移交部门id")
        private Long toBelongDeptId;
        @ApiModelProperty("移交部门名称")
        private String toBelongDeptName;
    }

}
