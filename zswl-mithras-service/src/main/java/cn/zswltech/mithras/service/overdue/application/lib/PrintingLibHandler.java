package cn.zswltech.mithras.service.overdue.application.lib;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingDetailDto;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.DocPrinting;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.DocPrintingLib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
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
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.DOC_PRINTING;
    }
}
