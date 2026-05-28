package cn.zswltech.mithras.dto.contract.file;

import cn.zswltech.mithras.dto.CommonFileSortWeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@Data
@ApiModel("合同文件分组信息-返回体")
public class ContractFileGroupRSP {
    @ApiModelProperty("合同类型code")
    private String contractType;

    @ApiModelProperty("合同类型名称")
    private String contractTypeName;

    @ApiModelProperty("合同文件列表")
    private List<ContractFile> fileList;

    @ApiModelProperty("排序字段")
    private int sort;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @ApiModel("合同文件信息")
    public static class ContractFile extends CommonFileSortWeight {
        @ApiModelProperty("文件id")
        private Long id;

        @ApiModelProperty("文件名称")
        private String name;

        @ApiModelProperty("是否自动生成")
        private Integer isGenerate;

        @ApiModelProperty("创建时间戳")
        private Long createTimestamp;

        @Override
        protected String sortKey() {
            return this.name;
        }

        @Override
        public long createTimestamp() {
            return this.createTimestamp;
        }
    }
}
