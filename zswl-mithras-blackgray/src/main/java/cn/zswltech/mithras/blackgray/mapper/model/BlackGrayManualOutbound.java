package cn.zswltech.mithras.blackgray.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@TableName(value =  "black_gray_manual_outbound")
//@Table(name = "black_gray_manual_outbound")
public class BlackGrayManualOutbound {
    /**
     * id
     */
    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
     * 业务类型
     */
    @Column(name = "`business_type`")
    private String businessType;

    /**
     * 黑灰标识
     */
    @Column(name = "black_gray_type")
    private String blackGrayType;

    /**
     * 入库时间
     */
    @Column(name = "warehouse_time")
    private Date warehouseTime;

    /**
     * 出库时间
     */
    @Column(name = "plan_outbound_time")
    private Date planOutboundTime;

    /**
     * 实际出库时间
     */
    @Column(name = "actual_outbound_time")
    private Date actualOutboundTime;

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

    @Column(name = "warehouse_organization")
    private String warehouseOrganization;

    /**
     * 申请部门
     */
    @Column(name = "apply_dept")
    private String applyDept;


    /**
     * 申请文件keys
     */
    @Column(name = "apply_file_keys")
    private String applyFileKeys;

    /**
     * 出库状态
     */
    @Column(name = "audit_status")
    private Integer auditStatus;

    @Column(name = "source")
    private String source;

    @Column(name = "message")
    private String message;

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

    public static final String ID = "id";

    public static final String APPLY_ORGANIZATION = "applyOrganization";

    public static final String ENTERPRISE_NAME = "enterpriseName";

    public static final String UNIFIED_SOCIAL_CREDIT_CODE = "unifiedSocialCreditCode";

    public static final String BUSINESS_TYPE = "businessType";

    public static final String AUDIT_STATUS = "auditStatus";

    public static final String CREATE_TIME = "createTime";

    public static final String BLACK_GRAY_TYPE = "blackGrayType";

    public static final String PLAN_OUTBOUND_TIME = "planOutboundTime";


}