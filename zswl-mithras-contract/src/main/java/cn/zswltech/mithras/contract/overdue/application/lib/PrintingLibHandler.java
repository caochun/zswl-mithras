package cn.zswltech.mithras.contract.overdue.application.lib;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.contract.overdue.application.OverdueBusinessModule;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingDetailDto;
import cn.zswltech.mithras.contract.overdue.mapper.model.DocPrinting;
import cn.zswltech.mithras.contract.overdue.mapper.model.DocPrintingLib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/8 16:25
 */
@Service
public class PrintingLibHandler extends LibAbstractHandler<DocPrintingLib, DocPrinting, PrintingDetailDto> {


    @Override
    protected DocPrintingLib entity2Lib(DocPrinting f) {
        return BeanUtil.copyProperties(f, DocPrintingLib.class);
    }

    @Override
    protected DocPrinting lib2Entity(DocPrintingLib t) {
        return BeanUtil.copyProperties(t, DocPrinting.class);
    }

    @Override
    protected PrintingDetailDto lib2Rsp(DocPrintingLib f) {
        return BeanUtil.copyProperties(f, PrintingDetailDto.class);
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }

    @Override
    public Enum<?> businessModuleEnum() {
        return OverdueBusinessModule.DOC_PRINTING;
    }
}
