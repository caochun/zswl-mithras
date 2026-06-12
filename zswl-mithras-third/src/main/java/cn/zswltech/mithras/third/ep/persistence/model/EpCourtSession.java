package cn.zswltech.mithras.third.ep.persistence.model;

import cn.zswltech.mithras.foundation.annotation.NotCompareColumn;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZHANGXIN
 */
@Data
@Accessors(chain = true)
@TableName("ep_courtsession")
public class EpCourtSession  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 消息id
     */
    @TableId("msg_id")
    private String msgId;

    /**
     * 法院名称。
     */
    @TableId("court_name")
    private String courtName;

    /**
     * 法庭名称。
     */
    @TableId("court_room")
    private String courtRoom;

    /**
     * 法院部门。
     */
    @TableId("department")
    private String department;

    /**
     * 合法日期。
     */
    @TableId("lawful_day")
    private Date lawfulDay;

    /**
     * 日程日期。
     */
    @TableId("schedule_date")
    private Date scheduleDate;

    /**
     * 案号。
     */
    @TableId("case_number")
    private String caseNumber;

    /**
     * 案由。
     */
    @TableId("subject_matter")
    private String subjectMatter;

    /**
     * 案由代码。
     */
    @TableId("subject_matter_code")
    private String subjectMatterCode;

    /**
     * 原告。
     */
    @TableId("plaintiff")
    private String plaintiff;

    /**
     * 被告。
     */
    @TableId("defendant")
    private String defendant;

    /**
     * 当事人名称。
     */
    @TableId("party_name")
    private String partyName;

    /**
     * 首席法官。
     */
    @TableId("chief_judge")
    private String chiefJudge;

    /**
     * 省份信息。
     */
    @TableId("state")
    private String state;

    /**
     * 省份代码。
     */
    @TableId("state_code")
    private String stateCode;

    /**
     * 插入时间。
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableId("insert_time")
    private Date insertTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableId("msg_update_time")
    private Date msgUpdateTime;

    /**
     * 公告内容。
     */
    @TableId("content")
    private String content;

    /**
     * 公司名称。
     */
    @TableId("company_name")
    private String companyName;

    /**
     * 信用代码。
     */
    @TableId("credit_code")
    private String creditCode;

    /**
     * 唯一标识符。
     */
    @TableId("jsid")
    private Long jsid;

    @NotCompareColumn
    @TableField(value = "create_by", updateStrategy = FieldStrategy.NEVER)
    private Long createBy;

    @NotCompareColumn
    @TableField("update_by")
    private Long updateBy;

}
