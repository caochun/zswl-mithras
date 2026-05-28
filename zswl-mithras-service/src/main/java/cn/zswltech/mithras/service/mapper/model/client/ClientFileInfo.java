package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhouning
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "client_file_info")
public class ClientFileInfo extends BaseModelWithLogicDelete {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("client_id")
    private Long clientId;

    @TableField("batch_no")
    private String batchNo;

    @TableField("user_id")
    private Long userId;

    @TableField("file_id")
    private Long fileId;

    @TableField("process_instance_id")
    private String processInstanceId;

}
