package cn.zswltech.mithras.dto.contract.file;

import cn.hutool.core.lang.Pair;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description
 */
@Data
@ApiModel("合同文本返回体")
public class ContractFileRSP {
    @ApiModelProperty("合同文本分组列表")
    private List<ContractFileGroupRSP> contractFileGroupList;
    @ApiModelProperty("可上传类型列表")
    private List<Pair<String, String>> canUploadTypeList;
}
