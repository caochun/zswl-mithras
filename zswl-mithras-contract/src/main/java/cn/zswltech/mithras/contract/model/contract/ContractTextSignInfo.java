package cn.zswltech.mithras.contract.model.contract;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 合同文件签约信息表
 *
 * @author bigbear
 * @TableName contract_text_sign_info
 */
@Data
@TableName(value = "contract_text_sign_info")
@EqualsAndHashCode(callSuper = true)
public class ContractTextSignInfo extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联contract_text_manage主键id
     */
    @TableField(value = "main_id")
    private Long mainId;

    /**
     * 关联materials_list主键id
     */
    @TableField(value = "source_file_id")
    private Long sourceFileId;

    /**
     * 文本签约方式
     */
    @TableField(value = "text_sign_way")
    private String textSignWay;

    /**
     * 文本签约状态
     */
    @TableField(value = "text_sign_status")
    private String textSignStatus;

    /**
     * 推送时间
     */
    @TableField(value = "push_time")
    private LocalDateTime pushTime;

    /**
     * 签署完成的文件id
     */
    @TableField(value = "signed_file_id")
    private Long signedFileId;

    /**
     * 转换完成的文件id
     */
    @TableField(value = "converted_file_id")
    private Long convertedFileId;

    /**
     * 上传契约锁平台的文档id
     */
    @TableField(value = "qys_document_id")
    private Long qysDocumentId;

    /**
     * 契约锁平台合同ID
     */
    @TableField(value = "qys_contract_id")
    private Long qysContractId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Long getFileId() {
        if (Objects.nonNull(signedFileId)) {
            return signedFileId;
        } else {
            return convertedFileId;
        }
    }

}