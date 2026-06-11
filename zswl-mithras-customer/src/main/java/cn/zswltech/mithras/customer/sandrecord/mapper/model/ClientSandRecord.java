package cn.zswltech.mithras.customer.sandrecord.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author shaokang
 * @description 客户沙盘数据记录表
 * @date 2026-01-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("client_sand_record")
public class ClientSandRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 客户ID
    @TableField("client_id")
    private Long clientId;

    // 统一社会信用代码
    @TableField("usc_code")
    private String uscCode;

    // 客户名称
    @TableField("client_name")
    private String clientName;

    // 记录日期
    @TableField("record_date")
    private LocalDate recordDate;

    // 数据来源：VW-vw视图 WB-外部客户
    @TableField("data_source")
    private String dataSource;

    // 数据类型：A-增加 D-删除
    @TableField("data_type")
    private String dataType;

    // 数据标记,是否转为系统内部数据:1-是,0-否
    @TableField("data_mark")
    private String dataMark;

    // 是否第一版: 1-是,0-否
    @TableField("first_mark")
    private String firstMark;
}
