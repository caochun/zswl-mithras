package cn.zswltech.mithras.associationreport.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/4/21
 * @description 金融协会报送-上报文件字段排序
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("association_report_sort")
public class AssociationReportSort extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 报表编码
     */
    @TableField("report_category_code")
    private String reportCategoryCode;

    /**
     * 报表名称
     */
    @TableField("report_category_name")
    private String reportCategoryName;

    /**
     * 字段名称
     */
    @TableField("field_name")
    private String fieldName;

    /**
     * 字段排序
     **/
    @TableField("sort")
    private Integer sort;
}
