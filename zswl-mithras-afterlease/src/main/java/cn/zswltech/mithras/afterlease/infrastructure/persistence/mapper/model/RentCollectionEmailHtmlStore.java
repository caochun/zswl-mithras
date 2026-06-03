package cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送租金催收邮件html缓存
 *
 * @author wangchuanhao
 * @date 2022/11/22 3:38 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentCollectionEmailHtmlStore extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收款主表id
     */
    @TableField(value = "collection_id")
    private Long collectionId;

    /**
     * html key
     */
    @TableField(value = "html_key")
    private String htmlKey;

    /**
     * html内容
     */
    @TableField(value = "html_data")
    private String htmlData;

    /**
     * 是否默认记录（无界面手填内容）
     */
    @TableField(value = "default_flag")
    private Integer defaultFlag;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

}
