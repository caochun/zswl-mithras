package ${packageName};
<#if isAutoImport?exists && isAutoImport==true>
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${apiDir}.${classInfo.className}Api;
import ${reqDir}.${classInfo.className}AddREQ;
import ${reqDir}.${classInfo.className}ModifyREQ;
import ${reqDir}.${classInfo.className}ListREQ;
import ${reqDir}.${classInfo.className}ListRSP;
import ${reqDir}.${classInfo.className}RemoveREQ;
import ${serviceDir}.${classInfo.className}Service;
import ${entityDir}.${classInfo.className};
</#if>

import java.util.List;

/**
* @description ${classInfo.classComment}
* @author ${authorName}
* @date ${.now?string('yyyy-MM-dd')}
*/
@RestController
public class ${classInfo.className}Controller implements ${classInfo.className}Api {

    @Resource
    private ${classInfo.className}Service ${classInfo.lcHeadClassName}Service;

    @Override
    public R<Void> add(${classInfo.className}AddREQ req) {
        ${classInfo.lcHeadClassName}Service.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(${classInfo.className}ModifyREQ req){
        ${classInfo.lcHeadClassName}Service.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<${classInfo.className}ListRSP>> list(${classInfo.className}ListREQ req){
        Page<${classInfo.className}> data = ${classInfo.lcHeadClassName}Service.list(req);
        List<${classInfo.className}ListRSP> list = BeanUtil.copyToList(data.getRecords(), ${classInfo.className}ListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(${classInfo.className}RemoveREQ req){
        ${classInfo.lcHeadClassName}Service.remove(req);
        return R.ok();
    }

}