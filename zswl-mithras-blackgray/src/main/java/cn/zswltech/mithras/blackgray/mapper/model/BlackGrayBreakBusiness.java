package cn.zswltech.mithras.blackgray.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName(value = "black_gray_break_business")
public class BlackGrayBreakBusiness {

    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 黑灰名单ID
     */
    @Column(name = "black_gray_id")
    private Long blackGrayId;

    /**
     * 企业名称
     */
    @Column(name = "enterprise_name")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @Column(name = "unified_social_credit_code")
    private String unifiedSocialCreditCode;


    /**
     * 黑灰标识
     * {@link BlackGrayTypeEnum#name()}
     */
    @Column(name = "black_gray_type")
    private String blackGrayType;


    /**
     * 拟开展业务类型
     */
    @Column(name = "proposed_business_type")
    private String proposedBusinessType;

    /**
     * 原计划出库时间
     */
    @Column(name = "plan_outbound_time")
    private Date planOutboundTime;

    /**
     * 拟开展业务规模（万元）
     */
    @Column(name = "propose_business_scale")
    private BigDecimal proposeBusinessScale;

    /**
     * 申请原因
     */
    @Column(name = "apply_reason")
    private String applyReason;

    /**
     * 申请机构
     */
    @Column(name = "apply_organization")
    private String applyOrganization;

    /**
     * 申请部门
     */
    @Column(name = "apply_dept")
    private String applyDept;

    /**
     * 文件
     */
    @Column(name = "apply_file_keys")
    private String applyFileKeys;

    /**
     * 突破流程状态
     */
    @Column(name = "audit_status")
    private Integer auditStatus;

    /**
     * 创建人、发起人
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 最后更新人id
     */
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "report_flag")
    private Integer reportFlag;

    public static final String APPLY_ORGANIZATION = "applyOrganization";

    public static final String BUSINESS_TYPE = "proposedBusinessType";

    public static final String AUDIT_STATUS = "auditStatus";

    public static final String CREATE_TIME = "createTime";

    public static final String BLACK_GRAY_TYPE = "blackGrayType";

    public static final String ENTERPRISE_NAME = "enterpriseName";

    public static final String PROPOSED_BUSINESS_TYPE = "proposedBusinessType";

}