package cn.zswltech.mithras.service.mapper.model.ep;

import cn.zswltech.mithras.common.annotation.NotCompareColumn;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZHANGXIN
 */
@Data
@Accessors(chain = true)
@TableName("ep_changeinfo")
public class EpChangeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 企业编码
     */
    @TableId(value = "enterprise_code")
    private String enterpriseCode;

    /**
     * 变更事项
     */
    private Integer changeItem;

    /**
     * 变更事项描述
     */
    @TableId(value = "change_note")
    private String changeNote;

    /**
     * 变更前内容
     */
    @TableId(value = "before_change")
    private String beforeChange;

    /**
     * 变更后内容
     */
    @TableId(value = "after_change")
    private String afterChange;

    /**
     * 变更日期
     */
    @TableId(value = "change_date")
    private Date changeDate;

    /**
     * 发布时间
     */
    @TableId(value = "insert_time")
    private Date insertTime;

    /**
     * 更新时间
     */
    @TableId(value = "msg_update_time")
    private Date MsgUpdateTime;

    /**
     * JSID
     */
    @TableId(value = "jsid")
    private Long jsid;

    /**
     * DataJson
     */
    @TableId(value = "data_json")
    private String dataJson;

    /**
     * 是否历史
     */
    @TableId(value = "if_history")
    private Integer ifHistory;

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
