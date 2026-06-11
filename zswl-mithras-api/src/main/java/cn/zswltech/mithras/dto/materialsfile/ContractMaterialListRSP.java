package cn.zswltech.mithras.dto.materialsfile;

import cn.zswltech.mithras.dto.CommonFileSortWeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/18
 * @description
 */
@Data
@ApiModel("合同资料清单-返回体")
public class ContractMaterialListRSP {
    @ApiModelProperty("分组类型")
    private String groupType;

    @ApiModelProperty("分组类型名称")
    private String groupTypeName;

    @ApiModelProperty("文件列表")
    private List<FileData> fileDataList;

    @ApiModelProperty("排序")
    private int sort;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @ApiModel("合同资料清单文件信息-返回体")
    public static class FileData extends CommonFileSortWeight {
        @ApiModelProperty("文件id")
        private Long fileId;

        @ApiModelProperty("资料类型")
        private String materialType;

        @ApiModelProperty("资料类型名称")
        private String materialTypeName;

        @ApiModelProperty("文件名称")
        private String fileName;

        @ApiModelProperty("创建时间戳")
        private long createTimestamp;

        @ApiModelProperty("创建人")
        private String createByName;

        @ApiModelProperty("创建时间")
        private LocalDateTime createTime;

        @ApiModelProperty
        private Integer idType = 1;

        @Override
        protected String sortKey() {
            return this.fileName;
        }

        @Override
        public long createTimestamp() {
            return this.createTimestamp;
        }
    }
}
