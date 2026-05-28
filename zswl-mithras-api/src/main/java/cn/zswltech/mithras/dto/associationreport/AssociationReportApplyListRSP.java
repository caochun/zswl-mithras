package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 金融局报表申请表
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("金融局报表申请表列表-返回体")
public class AssociationReportApplyListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 申请的报表实例唯一标识，多个以逗号分隔
    */
    @ApiModelProperty(value = "申请的报表实例唯一标识，多个以逗号分隔")
    private String reportInstanceIds;

    /**
    * 审批状态
    */
    @ApiModelProperty(value = "审批状态")
    private String approvalStatus;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除，1-已删除")
    private Integer deleted;

}
