package cn.zswltech.mithras.dto.client.client;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yibin
 */
@Data
@ApiModel("获取指定用户负责的客户列表-返回体")
public class SponsorClientListNewRSP {

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
    @ApiModelProperty("资产五级分类结果")
    private String assertClassifyResult;
    @ApiModelProperty("立项列表")
    private List<ClientProjRSP> clientProjRSPList;

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
        @ApiModelProperty("新的部门id")
        private Long toBelongDeptId;
        @ApiModelProperty("新的部门名称")
        private String toBelongDeptName;
        @ApiModelProperty("新的协办id列表")
        private List<Long> toCosponsorIds;
        @ApiModelProperty("新的协办名称")
        private String toCosponsorNames;
        @ApiModelProperty("项目流状态")
        private String projStatus;
        @ApiModelProperty("项目资料归档状态")
        private String projArchiveStatus;
        @ApiModelProperty("风险移交比例")
        private Integer riskTransferValue;
        @ApiModelProperty("收益移交比例")
        private Integer incomeTransferValue;
        @ApiModelProperty("客户移交详情id")
        private Long transferWeightId;
    }

}
