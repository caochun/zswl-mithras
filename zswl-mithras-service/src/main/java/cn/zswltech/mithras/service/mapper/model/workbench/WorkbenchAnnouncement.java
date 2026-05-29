package cn.zswltech.mithras.service.mapper.model.workbench;
import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 首页工作台-公告
 * @author zhaozhengkang
 * @date 2023-03-15
 */
@Data
public class WorkbenchAnnouncement extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 标题
    */
    @TableField("title")
    private String title;

    /**
    * 正文
    */
    @TableField("content")
    private String content;

    /**
    * 图片id
    */
    @TableField("image")
    private Long image;

    /**
    * 置顶， 1为置顶
    */
    @TableField("top")
    private Integer top;

    @TableField("expiration_from")
    private LocalDate expirationFrom;

    @TableField("expiration_to")
    private LocalDate expirationTo;

}
