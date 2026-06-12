package cn.zswltech.mithras.document.persistence.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description onlyoffice文件key存档
 * @author yeqing
 * @date 2022-07-08
 */
@Data
@TableName("onlyoffice_key_store")
public class OnlyofficeKeyStore {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 文件在oss中的路径
    */
    @TableField("file_oss_path")
    private String fileOssPath;

    /**
    * 文件url md5值
    */
    @TableField("file_oss_path_md5")
    private String fileOssPathMd5;

    /**
    * 随机生成的key，文件未改动前不变
    */
    @TableField("file_key")
    private String fileKey;

    /**
     * 文件id
     */
    @TableField("file_id")
    private Long fileId;

    /**
     * 文件id类型，1-编辑区，2-版本区
     */
    @TableField("file_id_type")
    private Integer fileIdType;

    /**
    * create_time
    */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
    * update_time
    */
    @TableField("update_time")
    private LocalDateTime updateTime;

}