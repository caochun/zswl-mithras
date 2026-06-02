package cn.zswltech.mithras.contract.overdue.application.docprinting;

import cn.zswltech.mithras.contract.overdue.application.OverdueBusinessModule;
import cn.zswltech.mithras.contract.overdue.application.lib.PrintingLibHandler;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.DocPrinting;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/8 16:36
 */
@Service
public class PrintingVersionService extends CommonVersionService<DocPrinting> {
    @Resource
    private PrintingLibHandler printingLibHandler;

    @Override
    public void customFlushData(DocPrinting docPrinting, String version, boolean needClearLastFlag, Integer versionType) {
        printingLibHandler.flushData(version, docPrinting.getId(), needClearLastFlag, versionType);
    }

    @Override
    public void customReset(DocPrinting docPrinting, CommonVersion commonVersion) {

    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        return null;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, DocPrinting baseModel, Map<Long, String> userNameMap) {
        return null;
    }

    @Override
    public Enum<?> getBusinessModule() {
        return OverdueBusinessModule.DOC_PRINTING;
    }
}
