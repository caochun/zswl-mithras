package cn.zswltech.mithras.customer.model.client;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 法人公司科目指标表
 * </p>
 *
 * @author MyBatisPlusGenerater
 * @since 2022-06-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("corp_subject_item")
public class CorpSubjectItem extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 报表类型
     */
    @TableField("report_type")
    private String reportType;

    /**
     * 科目类型
     */
    @TableField("subject_type")
    private String subjectType;

    /**
     * 科目代码
     */
    @TableField("subject_code")
    private String subjectCode;

    /**
     * 科目名称
     */
    @TableField("subject_name")
    private String subjectName;

    /**
     * 年度
     */
    @TableField("year")
    private Integer year;

    /**
     * 季度
     */
    @TableField("quarter")
    private Integer quarter;

    /**
     * 值文本
     */
    @TableField("subject_value")
    private Long subjectValue;

}
