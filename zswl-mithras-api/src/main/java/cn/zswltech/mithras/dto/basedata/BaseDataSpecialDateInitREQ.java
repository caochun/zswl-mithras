package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/4
 * @description
 */
@Data
@ApiModel("基础数据-特殊日期-初始化请求体")
public class BaseDataSpecialDateInitREQ {
    @NotBlank
    private String content;

    @NotNull
    private Integer year;

}
