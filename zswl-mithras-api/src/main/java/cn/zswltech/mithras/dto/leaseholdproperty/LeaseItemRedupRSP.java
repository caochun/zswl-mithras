package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: ldhu
 * @date: 2025/12/5
 * @desc: 租赁物查重结果
 **/
@Data
public class LeaseItemRedupRSP {
    @ApiModelProperty("重复的租赁物清单id")
    private Boolean matchFlag = false;

    @ApiModelProperty("重复的租赁物清单id")
    private Map<Long,Set<String>> leaseItemListRowData = new HashMap<>();

    @ApiModelProperty("重复的租赁物项目清单")
    private Set<ProjModel> projList = new HashSet<>();

    public void addProjList(ProjModel projModel){
        projList.add(projModel);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ToString
    public static class ProjModel {
        @ApiModelProperty("项目id")
        private Long id;
        @ApiModelProperty("项目名称")
        private String projName;
        @ApiModelProperty("审批状态")
        private String approvalStatus;
        @ApiModelProperty("流程id")
        private String flowId;
    }
}
