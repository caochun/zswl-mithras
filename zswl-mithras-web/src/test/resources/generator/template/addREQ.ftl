package ${packageName};
<#if isAutoImport?exists && isAutoImport==true>
<#if isLombok?exists && isLombok==true>import lombok.Data;</#if>
<#if isSwagger?exists && isSwagger==true>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;</#if>
</#if>
/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
<#if isLombok?exists && isLombok==true>@Data</#if><#if isSwagger?exists && isSwagger==true>
@ApiModel("${classInfo.classComment}新增-请求体")</#if>
public class ${classInfo.className}AddREQ {

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem >
    <#switch fieldItem.fieldName>
        <#case "id">
        <#case "createTime">
        <#case "createBy">
        <#case "updateTime">
        <#case "updateBy">
            <#break>
        <#default>
    <#if isComment?exists && isComment==true>/**
    * ${fieldItem.fieldComment}
    */</#if><#if isSwagger?exists && isSwagger==true>
    @ApiModelProperty(value = "${fieldItem.fieldComment}")</#if>
    private ${fieldItem.fieldClass} ${fieldItem.fieldName};

    </#switch>
</#list>
</#if>
}
