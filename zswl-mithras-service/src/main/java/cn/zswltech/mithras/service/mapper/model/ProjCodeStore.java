package cn.zswltech.mithras.service.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 项目编号拆表 因为项目评审、项目立项都要生成项目编号了，项目编号又需要唯一，所以需要单独拎出来存
 * @author yeqing
 * @date 2022-07-08
 */
@Data
@TableName("proj_code_store")
public class ProjCodeStore {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
     * 项目编号
     */
    @TableField("proj_code")
    private String projCode;

    /**
    * 业务类型。租赁、保理、转租赁
    */
    @TableField("biz_type")
    private String bizType;

    /**
    * 单业务类型 序号
    */
    @TableField("type_seq_id")
    private Long typeSeqId;

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