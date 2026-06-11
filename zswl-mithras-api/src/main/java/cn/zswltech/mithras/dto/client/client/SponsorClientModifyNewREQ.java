package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yibin
 */
@Data
@ApiModel("新的提交转移编辑用户负责的客户-请求体")
public class SponsorClientModifyNewREQ implements Serializable {

        @ApiModelProperty("客户id")
        private Long id;
        @ApiModelProperty("客户移交详情id")
        private Long transferWeightId;
        @ApiModelProperty("项目名称")
        private String projectName;
        @ApiModelProperty("项目编号")
        private String projectCode;
        @ApiModelProperty("合同编号")
        private String contractCode;
        @ApiModelProperty("新的主办id")
        private Long toSponsorId;
        @ApiModelProperty("新的主办名称")
        private String toSponsorName;
        @ApiModelProperty("新的部门id")
        private Long toBelongDeptId;
        @ApiModelProperty("新的部门名称")
        private String toBelongDeptName;
        @ApiModelProperty("项目资料归档状态")
        private String projArchiveStatus;
        @ApiModelProperty("风险移交比例")
        private Integer riskTransferValue;
        @ApiModelProperty("收益移交比例")
        private Integer incomeTransferValue;
        @ApiModelProperty("项目编号")
        private String projCode;
        @ApiModelProperty("业务类型")
        private String bizType;
        @ApiModelProperty("新的协办id列表")
        private List<Long> toCosponsorIds;
        @ApiModelProperty("新的协办名称")
        private String toCosponsorNames;
        @ApiModelProperty("项目流状态")
        private String projStatus;
        @ApiModelProperty("客户经理id")
        private Long belongSponsorId;

        @ApiModelProperty("随机批次号")
        private String batchNo;

        @ApiModelProperty("流程实例id")
        public String processInstanceId;

}
