package cn.zswltech.mithras.ftp.newftp.model.config;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ftp参数设定配置表
 * @TableName new_ftp_parameter_setting_config
 */
@TableName(value ="new_ftp_parameter_setting_config")
@Data
public class NewFtpParameterSettingConfig extends BaseModel implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 参数类别
     */
    @TableField(value = "category")
    private String category;

    /**
     * 分类中文名
     */
    @TableField(value = "category_display")
    private String categoryDisplay;

    /**
     * 参数名称
     */
    @TableField(value = "param_name")
    private String paramName;

    /**
     * 参数其他名称，扩展用
     */
    @TableField(value = "param_other_name")
    private String paramOtherName;

    /**
     * 公式
     */
    @TableField(value = "formula")
    private String formula;

    /**
     * 参数值
     */
    @TableField(value = "value")
    private Integer value;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}