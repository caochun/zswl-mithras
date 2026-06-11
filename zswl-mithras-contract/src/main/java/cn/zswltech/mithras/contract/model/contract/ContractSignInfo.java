package cn.zswltech.mithras.contract.model.contract;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/11/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_sign_info")
public class ContractSignInfo extends BaseModelWithLogicDelete {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 文件id
     */
    @TableField(value = "file_id")
    private Long fileId;

    /**
     * 签约人id，如果是我方则为0，如果是对方则为对应客户id
     */
    @TableField(value = "signatory")
    private Long signatory;

    /**
     * 签章关键字（用于确定电子印章在文件中的位置）
     */
    @TableField(value = "sign_keyword")
    private String signKeyword;

    /**
     * 签约方式
     */
    @TableField(value = "sign_way")
    private String signWay;

    /**
     * 签约状态
     */
    @TableField(value = "sign_status")
    private String signStatus;

    /**
     * 签约完成时间
     */
    @TableField(value = "sign_finish_time")
    private LocalDateTime signFinishTime;

    /**
     * 合同面签是否需要展示 0：不需要 1：需要
     */
    @TableField(value = "face_sign_show_flag")
    private Integer faceSignShowFlag;

    /**
     * 生成的文件的模版Key {@link }
     */
    @TableField(value = "file_template_key")
    private String fileTemplateKey;

    /**
     * 实名认证状态
     */
    @TableField(value = "real_name_auth_status")
    private String realNameAuthStatus;
}
