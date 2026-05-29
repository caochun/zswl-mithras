package cn.zswltech.mithras.service.mapper.model.leaseholdproperty;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

/**
 * 租赁物管理信息
 * @TableName lease_item_info
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="lease_item_info")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseItemInfo extends BaseModel {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目评审id
     */
    @TableField(value = "proj_review_id")
    private Long projReviewId;

    /**
     * 项目名称
     */
    @TableField(value = "proj_name")
    private String projName;

    /**
     * 客户ID
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 业务部门id
     */
    @TableField(value = "biz_dept_id")
    private Long bizDeptId;

    /**
     * 项目主办用户id
     */
    @TableField(value = "proj_sponsor_user_id")
    private Long projSponsorUserId;

    /**
     * 项目协办方用户id列表
     */
    @TableField(value = "proj_cosponsor_user_ids")
    private String projCosponsorUserIds;

    /**
     * 流程id
     */
    @TableField(value = "flow_id")
    private String flowId;

    /**
     * 流程id
     */
    @TableField(value = "relevance_flow_id")
    private String relevanceFlowId;

    /**
     * 中登网查重时间
     */
    @TableField(value = "duplicate_checking_date")
    private Date duplicateCheckingDate;

    /**
     * 占有合同ID列表
     */
    @TableField(value = "contract_ids")
    private String contractIds;

    /**
     * 租赁物类型
     */
    @TableField(value = "lease_item_types")
    private String leaseItemTypes;

    /**
     * 租赁物中文类型
     */
    @TableField(value = "lease_item_categories")
    private String leaseItemCategories;

    /**
     * 权属文件类型
     */
    @TableField(value = "ownership_file_types")
    private String ownershipFileTypes;

    /**
     * 价值认定文件
     */
    @TableField(value = "value_identification_files")
    private String valueIdentificationFiles;

//    /**
//     * 租赁物总额
//     * @deprecated
//     */
//    @Deprecated
//    @TableField(value = "total_amount_of_lease_item")
//    private Long totalAmountOfLeaseItem;

    /**
     * 流程状态
     */
    @TableField(value = "approval_status")
    private String approvalStatus;

    /**
     * 租赁物清单表头
     */
    @TableField(value = "item_list_header")
    private String itemListHeader;
}