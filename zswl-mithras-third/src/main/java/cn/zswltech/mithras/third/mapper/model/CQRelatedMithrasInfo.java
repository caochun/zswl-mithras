package cn.zswltech.mithras.third.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName CQRelatedMithrasInfo
 * @Description
 * @Author jackerhe
 * @Date 2022/11/1 3:48 下午
 * @Version 1.0
 **/
@Data
@TableName("cq_related_mithras")
public class CQRelatedMithrasInfo extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    //苍穹付款申请单单号
    @TableField("billno")
    private String billno;

    //收付款申请编号
    @TableField("collection_code")
    private String collectionCode;

    //收付款类型
    @TableField("record_source")
    private String recordSource;
}
