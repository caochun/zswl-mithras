package cn.zswltech.mithras.blackgray.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@TableName(value = "black_gray_business_dict")
public class BlackGrayBusinessDict {
    /**
     * id
     */
    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_type")
    private String businessType;

    /**
     * 机构ID
     */
    @Column(name = "org_id")
    private Long orgId;

    /**
     * 机构名称
     */
    @Column(name = "org_name")
    private Long orgName;

    /**
     * 业务类型名称
     */
    @Column(name = "business_name")
    private String businessName;

    /**
     * 业务类型描述
     */
    @Column(name = "business_desc")
    private String businessDesc;

    /**
     * 层级
     */
    @Column(name = "level")
    private Integer level;

    /**
     * 父ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 所属金控类型主ID，即一级ID
     */
    @Column(name = "main_id")
    private Long mainId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取机构ID
     *
     * @return org_id - 机构ID
     */
    public Long getOrgId() {
        return orgId;
    }

    /**
     * 设置机构ID
     *
     * @param orgId 机构ID
     */
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取机构名称
     *
     * @return org_name - 机构名称
     */
    public Long getOrgName() {
        return orgName;
    }

    /**
     * 设置机构名称
     *
     * @param orgName 机构名称
     */
    public void setOrgName(Long orgName) {
        this.orgName = orgName;
    }

    /**
     * 获取业务类型名称
     *
     * @return business_name - 业务类型名称
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * 设置业务类型名称
     *
     * @param businessName 业务类型名称
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /**
     * 获取业务类型描述
     *
     * @return business_desc - 业务类型描述
     */
    public String getBusinessDesc() {
        return businessDesc;
    }

    /**
     * 设置业务类型描述
     *
     * @param businessDesc 业务类型描述
     */
    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    /**
     * 获取层级
     *
     * @return level - 层级
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * 设置层级
     *
     * @param level 层级
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * 获取父ID
     *
     * @return parent_id - 父ID
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 设置父ID
     *
     * @param parentId 父ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取所属金控类型主ID，即一级ID
     *
     * @return main_id - 所属金控类型主ID，即一级ID
     */
    public Long getMainId() {
        return mainId;
    }

    /**
     * 设置所属金控类型主ID，即一级ID
     *
     * @param mainId 所属金控类型主ID，即一级ID
     */
    public void setMainId(Long mainId) {
        this.mainId = mainId;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
}