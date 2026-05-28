package ${packageName};
<#if isAutoImport?exists && isAutoImport==true>
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ${entityDir}.${classInfo.className};
</#if>

/**
* @description ${classInfo.classComment}
* @author ${authorName}
* @date ${.now?string('yyyy-MM-dd')}
*/
public interface ${classInfo.className}Mapper extends BaseMapper<${classInfo.className}> {

}