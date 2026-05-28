package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 金融局报表数据权限
 * @author hspcadmin
 * @date 2025-09-25
 */
@Data
@ApiModel("金融局报表数据权限列表-返回体")
public class AssociationReportDataAccessListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 角色code
    */
    @ApiModelProperty(value = "角色code")
    private String roleCode;

    /**
    * 角色名称
    */
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
    * 可以新增/编辑的报表，多个以英文逗号分隔
    */
    @ApiModelProperty(value = "可以新增/编辑的报表，多个以英文逗号分隔")
    private String addReportCategoryCodes;

    /**
    * 可以查看的报表，多个以英文逗号分隔
    */
    @ApiModelProperty(value = "可以查看的报表，多个以英文逗号分隔")
    private String queryReportCategoryCodes;

    /**
    * 审批流程
    */
    @ApiModelProperty(value = "审批流程")
    private String flowName;

    /**
    * 逻辑删除，0-未删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除")
    private Integer deleted;

}
