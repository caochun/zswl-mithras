package ${packageName};
<#if isAutoImport?exists && isAutoImport==true>
<#if isLombok?exists && isLombok==true>import lombok.Data;</#if>
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
</#if>

/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
<#if isLombok?exists && isLombok==true>@Data</#if>
public class ${classInfo.className} extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem >
    <#switch fieldItem.fieldName>
        <#case "createTime">
        <#case "createBy">
        <#case "updateTime">
        <#case "updateBy">
            <#break>
        <#default>
    <#if isComment?exists && isComment==true>/**
    * ${fieldItem.fieldComment}
    */</#if>
    <#if fieldItem.columnName != "id">
    @TableField("${fieldItem.columnName}")
    <#else>
    @TableId(type = IdType.AUTO)
    </#if>
    private ${fieldItem.fieldClass} ${fieldItem.fieldName};

    </#switch>
</#list>
</#if>
}
