package cn.zswltech.mithras.customer.vwsync.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("client_vw_sync")
public class ClientVwSync {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("client_id")
    private String clientId;

    @TableField("cert_number")
    private String certNumber;

    @TableField("client_name")
    private String clientName;

    @TableField("sync_time")
    private Date syncTime;

    @TableField("sync_batch")
    private String syncBatch;

}
