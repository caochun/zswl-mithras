package cn.zswltech.mithras.blackgray.persistence.model;

import cn.zswltech.gruul.dao.dal.tkmybatis.IEntity;
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
@TableName(value = "black_gray_library")
public class BlackGrayLibrary extends IEntity {
    /**
     * id
     */
    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
     *
     */
    @Column(name = "`business_type`")
    private String businessType;

    /**
     * 黑灰名单数据摘要（企业名称+统一社会信用代码+业务类型）加密后获得
     */
    @Column(name = "data_digest")
    private String dataDigest;

    /**
     * 观察期
     */
    @Column(name = "period_under_observation")
    private String periodUnderObservation;

    /**
     * 风险规模（万元）
     */
    @Column(name = "risk_scale")
    private BigDecimal riskScale;

    /**
     * 所属集团
     */
    @Column(name = "membership_group")
    private String membershipGroup;

    /**
     * 所属集团
     */
    @Column(name = "group_credit_code")
    private String groupCreditCode;

    /**
     * 是否集团主企业
     */
    @Column(name = "group_leader_flag")
    private Boolean groupLeaderFlag;

    /**
     * 集团是否纳入黑名单
     */
    @Column(name = "blacklist_status")
    private Boolean blacklistStatus;

    /**
     * 申请原因类型
     */
    @Column(name = "apply_reason_type")
    private String applyReasonType;

    /**
     * 申请原因 = 入库原因
     */
    @Column(name = "apply_reason")
    private String applyReason;

    /**
     * 黑灰标识
     * {@link BlackGrayTypeEnum#name()}
     */
    @Column(name = "black_gray_type")
    private String blackGrayType;

    /**
     * 黑灰标识数字，BLACK=2, GRAY=1，用于聚合、排序，方便查询
     */
    @Column(name = "black_gray_type_num")
    private Integer blackGrayTypeNum;

    /**
     * 优先级排序，越小优先级越高
     */
    @Column(name = "black_gray_sort")
    private Integer blackGraySort;

    /**
     * 集团黑灰标识
     */
    @Column(name = "group_black_gray_type")
    private String groupBlackGrayType;

    /**
     * 申请时间
     */
    @Column(name = "apply_time")
    private Date applyTime;

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
     * 黑灰记录id
     */
    @Column(name = "record_id")
    private Long recordId;

    /**
     * 0在库，1出库
     **/
    @Column(name = "stock_status")
    private Integer stockStatus;
    /**
     * 共享类型 0金融企业黑名单， 1金控黑名单
     **/
    @Column(name = "share_type")
    private Integer shareType;

    /**
     * 来源
     * {@link BlackGraySourceEnum#name(}
     **/
    @Column(name = "source")
    private String source;

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

    /**
     * 入库文件key
     */
    @Column(name = "warehouse_file_keys")
    private String warehouseFileKeys;

    /**
     * 整改文件key
     */
    @Column(name = "rectify_file_keys")
    private String rectifyFileKeys;

    /**
     * 报送标识 0不报送，1已报送
     */
    @Column(name = "report_flag")
    private Integer reportFlag;

    public static final String ID = "id";

    public static final String APPLY_ORGANIZATION = "applyOrganization";

    public static final String ENTERPRISE_NAME = "enterpriseName";

    public static final String UNIFIED_SOCIAL_CREDIT_CODE = "unifiedSocialCreditCode";

    public static final String BUSINESS_TYPE = "businessType";

    public static final String STOCK_STATUS = "stockStatus";

    public static final String BLACK_GRAY_TYPE = "blackGrayType";

    public static final String SOURCE = "source";
    public static final String UPDATE_TIME = "updateTime";

    public static final String APPLY_TIME = "applyTime";

    public static final String AUDIT_STATUS = "auditStatus";

    public static final String WAREHOUSE_TIME = "warehouseTime";

    public static final String PLAN_OUTBOUND_TIME = "planOutboundTime";

    public static final String RECORD_ID = "recordId";

    public static final String APPLY_REASON_TYPE = "applyReasonType";

    public static final String MEMBERSHIP_GROUP = "membershipGroup";
    public static final String GROUP_CREDIT_CODE = "groupCreditCode";
    public static final String GROUP_LEADER_FLAG = "groupLeaderFlag";

    public static final String BLACK_GRAY_SORT = "blackGraySort";

}