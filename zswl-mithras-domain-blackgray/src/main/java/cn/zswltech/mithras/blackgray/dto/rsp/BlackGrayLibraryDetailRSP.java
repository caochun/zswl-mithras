package cn.zswltech.mithras.blackgray.dto.rsp;

import cn.zswltech.mithras.blackgray.annotation.DesensitizeField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单库列表-返回体")
public class BlackGrayLibraryDetailRSP {

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

    /**
     * 风险规模（万元）
     */
    @ApiModelProperty(value = "风险规模")
    private String riskScale;

    /**
     * 所属集团
     */
    @ApiModelProperty(value = "所属集团")
    private String membershipGroup;

    /**
     * 黑灰标识
     */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    /**
     * 申请时间
     */
    @ApiModelProperty(value = "申请时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date applyTime;

    /**
     * 入库原因
     */
    @DesensitizeField
    @ApiModelProperty(value = "入库原因")
    private String warehouseReason;

    @DesensitizeField
    @ApiModelProperty(value = "申请机构")
    private String applyOrganization;

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
     * 申请文件keys
     */
    @ApiModelProperty(value = "申请文件keys")
    private List<String> rectifyFileKeys;

    /**
     * 创建人、发起人
     */
    @ApiModelProperty(value = "create_by")
    private Long createBy;

    @ApiModelProperty(value = "create_by")
    private String createByCode;

    private String createName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    @ApiModelProperty(value = "来源")
    private String source;


}
