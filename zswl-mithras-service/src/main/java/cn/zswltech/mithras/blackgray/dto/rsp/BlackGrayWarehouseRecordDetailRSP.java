package cn.zswltech.mithras.blackgray.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单记录表
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表列表-返回体")
public class BlackGrayWarehouseRecordDetailRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 企业名称
    */
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    /**
    * 统一社会信用代码
    */
    @ApiModelProperty(value = "统一社会信用代码")
    private String unifiedSocialCreditCode;

    /**
    * 业务类型
    */
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "任务编号")
    private String taskNum;

    /**
    * 观察期
    */
    @ApiModelProperty(value = "观察期")
    private String periodUnderObservation;

    /**
    * 风险规模（万元）
    */
    @ApiModelProperty(value = "风险规模（万元）")
    private Double riskScale;

    /**
    * 所属集团
    */
    @ApiModelProperty(value = "所属集团")
    private String membershipGroup;

    /**
    * 集团是否纳入黑名单
    */
    @ApiModelProperty(value = "集团是否纳入黑名单")
    private Integer blacklistStatus;

    /**
     * 申请原因类型
     */
    @ApiModelProperty(name = "申请原因下拉")
    private List<String> applyReasonType;

    @ApiModelProperty(name = "申请原因下拉")
    private List<String> applyReasonName;

    /**
    * 申请原因
    */
    @ApiModelProperty(value = "申请原因")
    private String applyReason;

    /**
    * 黑灰标识
    */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    /**
    * 集团黑灰标识
    */
    @ApiModelProperty(value = "集团黑灰标识")
    private String groupBlackGrayType;

    /**
    * 申请时间
    */
    @ApiModelProperty(value = "申请时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date applyTime;

    /**
    * 报告机构
    */
    @ApiModelProperty(value = "报告机构")
    private String applyOrganization;

    @ApiModelProperty(value = "所属部门")
    private String applyDept;

    /**
    * 入库时间
    */
    @ApiModelProperty(value = "入库时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date warehouseTime;

    /**
    * 出库时间
    */
    @ApiModelProperty(value = "出库时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date planOutboundTime;


    /**
     * 入库文件key
     */
    @ApiModelProperty(name = "入库文件key")
    private List<String> warehouseFileKeys;

    /**
     * 整改文件key
     */
    @ApiModelProperty(name = "整改文件key")
    private List<String> rectifyFileKeys;

    /**
    * 记录状态
    */
    @ApiModelProperty(value = "记录状态")
    private Integer auditStatus;

    /**
     * 创建人、发起人
     */
    @ApiModelProperty(name = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "创建人code")
    private String createByCode;

    /**
     * 审批任务id
     */
    private Long auditTaskId;

    @ApiModelProperty(value = "来源")
    private String source;

    @ApiModelProperty(value = "是否上报金控 0 不上报，1上报")
    private Integer reportFlag;

}
