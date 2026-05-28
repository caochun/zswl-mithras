package cn.zswltech.mithras.dto.process.modify.remark;

import cn.zswltech.mithras.validation.ControllerMissParamException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author yibin
 */

@Data
@ApiModel("变更流程-附加标记信息-新增-请求体")
public class ProcessModifyRemarkAddREQ {

    @NotBlank
    @ApiModelProperty("模块类型")
    private String moduleType;

    /**
     * remark_type
     */
    @NotBlank
    @ApiModelProperty("变更流程标记信息类型")
    private String remarkType;

    /**
     * remark_json
     */
    @NotNull
    @ApiModelProperty("标记信息")
    private ProcessModifyObjDTO remarkJson;

    /**
     * main_id
     */
    @NotNull
    @ApiModelProperty("主表id")
    private Long mainId;

    public void check() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        Validator validator = vf.getValidator();
        Set<ConstraintViolation<ProcessModifyRemarkAddREQ>> set = validator.validate(this);
        if (!set.isEmpty()) {
            ConstraintViolation<ProcessModifyRemarkAddREQ> constraintViolation = set.iterator().next();
            throw new ControllerMissParamException(constraintViolation.getPropertyPath() + constraintViolation.getMessage());
        }
    }
}
