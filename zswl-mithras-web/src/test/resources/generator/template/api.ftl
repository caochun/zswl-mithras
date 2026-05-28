package ${packageName};
<#if isAutoImport?exists && isAutoImport==true>
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import ${reqDir}.${classInfo.className}AddREQ;
import ${reqDir}.${classInfo.className}ModifyREQ;
import ${reqDir}.${classInfo.className}ListREQ;
import ${reqDir}.${classInfo.className}ListRSP;
import ${reqDir}.${classInfo.className}RemoveREQ;
</#if>

/**
* @description ${classInfo.classComment}
* @author ${authorName}
* @date ${.now?string('yyyy-MM-dd')}
*/
@Api(tags = "${classInfo.classComment}-接口")
public interface ${classInfo.className}Api {

    @ApiOperation("新增${classInfo.classComment}")
    @PostMapping("${classInfo.urlPrefix}/add")
    R<Void> add(@RequestBody @Valid ${classInfo.className}AddREQ req);

    @ApiOperation("修改${classInfo.classComment}")
    @PostMapping("${classInfo.urlPrefix}/modify")
    R<Void> modify(@RequestBody @Valid ${classInfo.className}ModifyREQ req);

    @ApiOperation("${classInfo.classComment}列表")
    @PostMapping("${classInfo.urlPrefix}/list")
    R<PageR<${classInfo.className}ListRSP>> list(@RequestBody @Valid ${classInfo.className}ListREQ req);

    @ApiOperation("删除${classInfo.classComment}")
    @PostMapping("${classInfo.urlPrefix}/remove")
    R<Void> remove(@RequestBody @Valid ${classInfo.className}RemoveREQ req);

}