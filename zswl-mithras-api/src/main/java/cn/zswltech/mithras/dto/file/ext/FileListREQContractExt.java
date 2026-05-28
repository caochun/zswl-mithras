package cn.zswltech.mithras.dto.file.ext;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 合同
 *
 * @author wangchuanhao
 * @date 2023/2/6 3:07 PM
 */
@Data
public class FileListREQContractExt {

    @ApiModelProperty("查询子类型：COMMON（主界面查询）、CHANGE（变更材料）、SETTLE（结清补充协议）")
    private String queryType;

}
