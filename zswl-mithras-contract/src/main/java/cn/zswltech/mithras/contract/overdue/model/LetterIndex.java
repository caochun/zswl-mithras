package cn.zswltech.mithras.contract.overdue.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/29 15:39
 */
@Data
@TableName("oc_letter_index")
public class LetterIndex {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("year")
    private Integer year;

    @TableField("index")
    private Integer index;
}
