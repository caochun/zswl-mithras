package cn.zswltech.mithras.dto.groupcreditreview;
import cn.zswltech.mithras.dto.ListBaseRSP;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信评审基本信息表列表-返回体")
public class GroupCreditReviewListRSP extends ListBaseRSP {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 授信主体客户id
     */
    @ApiModelProperty(value = "授信主体客户id")
    private Long clientId;

    @ApiModelProperty(value = "授信主体客户名称")
    private String clientName;

    /**
     * 集团授信立项基本信息表id
     */
    @ApiModelProperty(value = "集团授信立项基本信息表id")
    private Long groupCreditEstablishId;

    /**
     * 授信名称
     */
    @ApiModelProperty(value = "授信名称")
    private String projName;

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号")
    private String projCode;

    /**
     * 申报授信金额
     */
    @ApiModelProperty(value = "申报授信金额")
    private Long applyCreditAmount;

    /**
     * 业务部门id
     */
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;

    @ApiModelProperty(value = "业务部门名称")
    private String bizDeptName;

    /**
     * 项目主办用户id
     */
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;

    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户id列表")
    private List<Long> projCosponsorUserIds;

    @ApiModelProperty(value = "项目协办方用户名称列表")
    private List<String> projCosponsorUserNames;

    /**
     * 评审状态
     */
    @ApiModelProperty(value = "评审状态")
    private String groupCreditReviewStatus;

    /**
     * 流程状态
     */
    @ApiModelProperty(value = "流程状态")
    private String groupCreditReviewProcessStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

}
