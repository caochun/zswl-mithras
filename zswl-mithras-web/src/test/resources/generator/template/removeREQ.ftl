package ${packageName};
<#if isAutoImport?exists && isAutoImport==true>
<#if isLombok?exists && isLombok==true>import lombok.Data;</#if>
<#if isSwagger?exists && isSwagger==true>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;</#if>
import javax.validation.constraints.NotNull;
</#if>
/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
<#if isLombok?exists && isLombok==true>@Data</#if><#if isSwagger?exists && isSwagger==true>
@ApiModel("${classInfo.classComment}删除-请求体")</#if>
public class ${classInfo.className}RemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
