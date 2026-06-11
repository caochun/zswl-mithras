package cn.zswltech.mithras.third.ep.mapper.model;

import cn.zswltech.mithras.foundation.annotation.NotCompareColumn;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZHANGXIN
 * 立案信息
 */
@Data
@Accessors(chain = true)
@TableName("ep_caseinfo")
public class EpCaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 案号
     */
    @TableId("case_number")
    private String caseNumber;

    /**
     * 法院名称
     */
    @TableId("court_name")
    private String courtName;

    /**
     * 法官
     */
    @TableId("judge")
    private String judge;

    /**
     * 法官助理
     */
    @TableId("judge_assistant")
    private String judgeAssistant;

    /**
     * 立案日期
     */
    @TableId("case_date")
    private Date caseDate;

    /**
     * 开庭日期
     */
    @TableId("session_date")
    private Date sessionDate;

    /**
     * 结案日期
     */
    @TableId("end_date")
    private Date endDate;

    /**
     * 地区
     */
    @TableId("area")
    private String area;

    /**
     * 案件状态描述
     */
    @TableId("case_status_desc")
    private String caseStatusDesc;

    /**
     * 案件状态代码
     */
    @TableId("case_status")
    private Integer caseStatus;

    /**
     * 链接地址
     */
    @TableId("link_address")
    private String linkAddress;

    /**
     * 插入时间
     */
    @TableId("insert_time")
    private Date insertTime;

    /**
     * 更新时间
     */
    @TableId("msg_update_time")
    private Date msgUpdateTime;

    /**
     * 公司名称
     */
    @TableId("company_name")
    private String companyName;

    /**
     * 信用代码
     */
    @TableId("credit_code")
    private String creditCode;

    /**
     * 唯一标识符
     */
    @TableId("jsid")
    private Long jsid;

    /**
     * 消息id
     */
    @TableId("msg_id")
    private String msgId;

    @NotCompareColumn
    @TableField(value = "create_by", updateStrategy = FieldStrategy.NEVER)
    private Long createBy;

    @NotCompareColumn
    @TableField("update_by")
    private Long updateBy;


}
