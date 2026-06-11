package cn.zswltech.mithras.customer.model.client;

import cn.zswltech.mithras.customer.enums.ClientAuthEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author junke
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("client")
@SponsorField(value = "belongSponsorId", belongDeptField = "belongDeptId", cosponsorField = "")
public class Client extends BaseModel implements IEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("client_name")
    private String clientName;
    @TableField("client_type")
    private String clientType;
    @TableField("client_code")
    private String clientCode;
    @TableField("domestic_or_abroad")
    private String domesticOrAbroad;
    @TableField("special_org_code")
    private String specialOrgCode;
    @TableField("usc_code")
    private String uscCode;
    @TableField("client_status")
    private String clientStatus;
    @TableField("process_status")
    private String processStatus;
    @TableField("cert_type")
    private String certType;
    @TableField("cert_number")
    private String certNumber;
    @TableField("create_by_dept")
    private Long createByDept;

    /**
     * 最新的版本
     */
    @TableField("newest_version")
    private String newestVersion;

    /**
     * 是否汉得导入
     */
    @TableField("hand_import_flag")
    private Integer handImportFlag;

    /**
     * 天眼查查到的客户名称 隐藏的 用于和天眼查交互
     */
    @TableField("tyc_name")
    private String tycName;

    /**
     * 所属部门id
     */
    @TableField("belong_dept_id")
    @IncludeNull
    private Long belongDeptId;


    /**
     * 所属负责项目经理的id
     */
    @TableField("belong_sponsor_id")
    @IncludeNull
    private Long belongSponsorId;

    /**
     * 权限类型{@link ClientAuthEnum#name()}
     **/
    @TableField("auth_type")
    private String authType;

    /**
     * 隶属省份，指标计算专用字段
     */
    @TableField("province_of_affiliation")
    private String provinceOfAffiliation;

    /**
     * 隶属省份是人工选择
     */
    @TableField("artificial_province")
    private boolean artificialProvince;

    //非数据库字段
    @TableField(exist = false)
    private String industryType;

    /**
     * 非数据库字段
     */
    @TableField(exist = false)
    private String bizClientType;

    /**
     * 非数据库字段
     */
    @TableField(exist = false)
    private Long contractId;

    /**
     * 非数据库字段
     */
    @TableField(exist = false)
    private String contractType;

    /**
     * 是否已释放
     */
    @TableField(value = "is_released")
    private Integer isReleased;

    /**
     * 最新一次更新人
     */
    @TableField("latest_user_id")
    private Long LatestUserId;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }
}
