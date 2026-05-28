package cn.zswltech.mithras.blackgray.dto.rsp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.blackgray.annotation.DesensitizeField;
import cn.zswltech.mithras.blackgray.enums.BusinessType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

import static cn.zswltech.mithras.blackgray.utils.EnterpriseRemarkUtil.getEnterpriseRemark;


/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单库列表-返回体")
public class BlackGrayLibraryOrgListRSP {

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
    @Column(name = "risk_scale")
    @DesensitizeField
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

    @ApiModelProperty(value = "集团黑灰标识")
    private String groupBlackGrayType;

    @ApiModelProperty(value = "来源")
    private String source;

    /**
     * 申请时间
     */
    @ApiModelProperty(value = "申请时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date applyTime;

    /**
     * 入库原因
     */
    @ApiModelProperty(value = "入库原因")
    private String applyReason;

    @ApiModelProperty(value = "入库原因")
    private List<String> applyReasonType;

    @ApiModelProperty(value = "入库原因名称")
    private List<String> applyReasonName;

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

    @ApiModelProperty(value = "共享类型 0金融企业黑名单， 1金控黑名单")
    private Integer shareType;

    @ApiModelProperty(value = "是否报送金控")
    private Integer reportFlag;

    public String getRemark() {
        if(ObjectUtil.isEmpty(enterpriseName) || ObjectUtil.isEmpty(unifiedSocialCreditCode) || ObjectUtil.isEmpty(businessType)){
            return null;
        }
        return getEnterpriseRemark(enterpriseName, unifiedSocialCreditCode, BusinessType.valueOf(businessType));
    }
}
