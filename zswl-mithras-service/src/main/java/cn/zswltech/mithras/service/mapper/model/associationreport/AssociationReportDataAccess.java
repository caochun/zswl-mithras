package cn.zswltech.mithras.service.mapper.model.associationreport;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.common.model.BaseModel;

/**
 * @description 金融局报表数据权限
 * @author hspcadmin
 * @date 2025-09-25
 */
@Data
public class AssociationReportDataAccess extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 角色code
    */
    @TableField("role_code")
    private String roleCode;

    /**
    * 角色名称
    */
    @TableField("role_name")
    private String roleName;

    /**
    * 可以新增/编辑的报表，多个以英文逗号分隔
    */
    @TableField("add_report_category_codes")
    private String addReportCategoryCodes;

    /**
    * 可以查看的报表，多个以英文逗号分隔
    */
    @TableField("query_report_category_codes")
    private String queryReportCategoryCodes;

    /**
    * 审批流程
    */
    @TableField("flow_name")
    private String flowName;

    /**
    * 逻辑删除，0-未删除
    */
    @TableField("deleted")
    private Integer deleted;

}
