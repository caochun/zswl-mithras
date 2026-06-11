package cn.zswltech.mithras.filingmaterials.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 
 * @author gxy
 * @TableName archived_materials_download_record 归档资料下载记录
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="archived_materials_download_record")
@Data
public class ArchivedMaterialsDownloadRecord extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    @TableField(value = "fileName")
    private String fileName;

    /**
     * 下载状态
     */
    @TableField(value = "download_status")
    private String downloadStatus;

    /**
     * 文件url
     */
    @TableField(value = "file_path")
    private String filePath;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}