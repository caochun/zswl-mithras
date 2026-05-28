package ${packageName};
<#if isAutoImport?exists && isAutoImport==true>
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import ${reqDir}.${classInfo.className}AddREQ;
import ${reqDir}.${classInfo.className}ModifyREQ;
import ${reqDir}.${classInfo.className}ListREQ;
import ${reqDir}.${classInfo.className}ListRSP;
import ${reqDir}.${classInfo.className}RemoveREQ;
import ${mapperDir}.${classInfo.className}Mapper;
import ${entityDir}.${classInfo.className};
</#if>

/**
* @description ${classInfo.classComment}
* @author ${authorName}
* @date ${.now?string('yyyy-MM-dd')}
*/
@Service
public class ${classInfo.className}Service {

    @Resource
    private ${classInfo.className}Mapper ${classInfo.lcHeadClassName}Mapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(${classInfo.className}AddREQ req) {
        ${classInfo.className} info = BeanUtil.copyProperties(req, ${classInfo.className}.class);
        ${classInfo.lcHeadClassName}Mapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(${classInfo.className}ModifyREQ req) {
        ${classInfo.className} originalInfo = ${classInfo.lcHeadClassName}Mapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ${classInfo.className} info = BeanUtil.copyProperties(req, ${classInfo.className}.class);
        ${classInfo.lcHeadClassName}Mapper.updateById(info);
    }

    public Page<${classInfo.className}> list(${classInfo.className}ListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(${classInfo.className}RemoveREQ req) {
        ${classInfo.className} originalInfo = ${classInfo.lcHeadClassName}Mapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ${classInfo.lcHeadClassName}Mapper.deleteById(req.getId());
    }

}