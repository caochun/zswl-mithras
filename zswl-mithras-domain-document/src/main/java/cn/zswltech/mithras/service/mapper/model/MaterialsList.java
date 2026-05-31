package cn.zswltech.mithras.service.mapper.model;

import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @create: 2022-07-21
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("materials_list")
public class MaterialsList extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 归属id
     */
    @TableField("belong_id")
    private Long belongId;

    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 资料类型
     */
    @TableField("materials_type")
    private String materialsType;

    /**
     * 资料子类型
     */
    @TableField("materials_sub_type")
    private String materialSubType;

    /**
     * oss上传文件名
     */
    @TableField("oss_filename")
    private String ossFilename;

    /**
     * 附件名
     */
    @TableField("filename")
    private String filename;

    /**
     * 文件名后缀
     */
    @TableField("suffix")
    private String suffix;


    @TableField("file_path")
    private String filePath;

    @TableField("system_generate")
    private Integer systemGenerate;

    @TableField("source_business_key")
    private String sourceBusinessKey;

    @TableField("location")
    private String location;

    /**
     * 是否被编辑
     * 0 否，1 是
     */
    @TableField("is_edit")
    private Integer isEdit;

    @Override
    public void setMainId(Long id) {
        this.belongId = id;
    }

    @Override
    public Long getMainId() {
        return belongId;
    }

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }
}
