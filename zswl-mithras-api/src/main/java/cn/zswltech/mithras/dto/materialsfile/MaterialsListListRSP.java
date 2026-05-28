package cn.zswltech.mithras.dto.materialsfile;

import cn.zswltech.mithras.dto.CommonFileSortWeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author luyi
 */
@Data
@ApiModel("资料清单列表-返回体")
public class MaterialsListListRSP {

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("归属id")
    private Long id;

    @ApiModelProperty("业务分类资料列表")
    private List<MaterialGroup> businessMaterialList;

    @Data
    public static class MaterialGroup {
        @ApiModelProperty("资料类型")
        private String materialName;

        @ApiModelProperty("资料列表")
        List<UploadItem> materialList;

        @EqualsAndHashCode(callSuper = true)
        @Data
        public static class UploadItem extends CommonFileSortWeight {
            @ApiModelProperty("资料子类型")
            private String materialSubName;
            @ApiModelProperty("附件名")
            private String filename;
            @ApiModelProperty("记录id")
            private Long recordId;
            @ApiModelProperty("创建时间戳")
            private long createTimestamp;
            @ApiModelProperty("版本号")
            private Integer idType = 1;
            @ApiModelProperty("来源业务key")
            private String sourceBusinessKey;
            @ApiModelProperty("上传人id")
            private Long createBy;
            @ApiModelProperty("上传人名称")
            private String createByName;
            @ApiModelProperty("是否系统生成")
            private Integer systemGenerate;
            @ApiModelProperty("是否修改")
            private Boolean isChange;

            @Override
            protected String sortKey() {
                return this.filename;
            }

            @Override
            public long createTimestamp() {
                return createTimestamp;
            }
        }


        public Boolean getUploaded() {
            return null != materialList && materialList.size() > 0;
        }
    }

}
