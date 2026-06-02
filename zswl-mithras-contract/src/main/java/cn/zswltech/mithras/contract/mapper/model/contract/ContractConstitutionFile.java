package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.contract.enums.contract.ContractConstitutionFileTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * @author yupengfei
 * @date 2024/4/18 9:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "contract_constitution_file")
public class ContractConstitutionFile extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键：唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 承租人/担保措施 id
     */
    @TableField(value = "tenantry_id")
    private Long tenantryId;

    /**
     * 章程文件id
     */
    @TableField(value = "materials_list_id")
    private Long materialsListId;

    /**
     * 章程文件类型
     *
     * @see ContractConstitutionFileTypeEnum
     */
    @TableField(value = "file_type")
    private String fileType;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return this.contractId;
    }
}
