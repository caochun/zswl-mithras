package cn.zswltech.mithras.dto.flow.search;

import cn.hutool.core.util.ReflectUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.lang.reflect.Field;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;

/**
 * @author yibin
 */
@Data
public class ProcessTaskExtra {
    public boolean allBlank() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object fieldValue = ReflectUtil.getFieldValue(this, field);
            if (null != fieldValue && isNotBlank(fieldValue.toString())) {
                return false;
            }
        }
        return true;
    }

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("合同编号")
    private String contractCode;
}
