package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName AssetClassifyNodeRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/1/4 3:48 下午
 * @Version 1.0
 **/
@ApiModel("五级分类-定级流程区返回体")
@Data
public class AssetClassifyNodeRSP {

    @ApiModelProperty("五级分类总流程状态")
    private Integer assetClassifyStatus;

    private List<NodeMessage> nodeMessages;

    @Data
    @ApiModel("节点信息")
    public class NodeMessage{
        private Long id;

        @ApiModelProperty("主表id")
        private Long assetClassifyId;

        @ApiModelProperty("节点名称 AssetClassifyBizNodeEnum")
        private String nodeName;

        @ApiModelProperty("排序")
        private Integer sort;

        @ApiModelProperty("节点开始时间")
        private LocalDateTime startTime;

        @ApiModelProperty("节点结束时间")
        private LocalDateTime endTime;

        @ApiModelProperty("节点所处状态 AssetClassifyStatusEnum")
        private String nodeStatue;
    }

    @ApiModelProperty("五级分类初分类型")
    private String assetClassifyInitType;
}
