package cn.zswltech.mithras.third.datashare.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 费控共享
 */
@Getter
@Setter
@TableName("data_share_fk")
public class DataShareFk extends BaseModelWithLogicDelete implements IEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 单据编号
     */
    @TableField("business_no")
    private String businessNo;
    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;
    /**
     * 文件url
     */
    @TableField("file_url")
    private String fileUrl;
    /**
     * 苍穹发送状态 1:成功; 0:未发送; -1:发送异常
     */
    @TableField("status")
    private String status;
    /**
     * 重试次数
     */
    @TableField("retry_num")
    private Integer retryNum;


    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }
}
