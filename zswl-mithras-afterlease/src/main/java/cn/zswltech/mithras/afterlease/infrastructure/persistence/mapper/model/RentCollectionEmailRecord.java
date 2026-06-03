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
 * 租金催收发送邮件记录
 *
 * @author wangchuanhao
 * @date 2022/11/18 4:50 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentCollectionEmailRecord extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收款id
     */
    @TableField("collection_id")
    private Long collectionId;

    /**
     * 收信邮箱
     */
    @TableField("receiver_mail")
    private String receiverMail;

    /**
     * 备注
     */
    @TableField("comment")
    private String comment;

    /**
     * 邮件标题
     */
    @TableField("title")
    private String title;

    /**
     * 文件id(用于前端onlyoffice预览)
     */
    @TableField("file_id")
    private Long fileId;

    /**
     * 邮件主体内容
     */
    @TableField("mail_content")
    private String mailContent;

    /**
     * 银行账号id
     */
    @TableField("bank_id")
    private Long bankId;

    @TableField("html_key")
    private String htmlKey;

}
