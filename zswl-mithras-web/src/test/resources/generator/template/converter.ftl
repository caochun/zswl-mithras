package ${packageName};
<#if isAutoImport?exists && isAutoImport==true>
import org.mapstruct.Mapper;
</#if>

/**
* @description ${classInfo.classComment}
* @author ${authorName}
* @date ${.now?string('yyyy-MM-dd')}
*/
@Mapper(componentModel = "spring")
public interface ${classInfo.className}Converter{

}
