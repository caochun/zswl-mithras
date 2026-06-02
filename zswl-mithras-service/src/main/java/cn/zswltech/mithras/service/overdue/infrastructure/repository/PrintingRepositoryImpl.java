package cn.zswltech.mithras.service.overdue.infrastructure.repository;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.contract.overdue.domain.docprinting.Printing;
import cn.zswltech.mithras.contract.overdue.domain.docprinting.PrintingCode;
import cn.zswltech.mithras.service.overdue.domain.docprinting.PrintingConverter;
import cn.zswltech.mithras.contract.overdue.domain.docprinting.PrintingRepository;
import cn.zswltech.mithras.contract.overdue.domain.litigation.LongId;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.EntityDiff;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.DocPrintingDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.DocPrintingLibDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.DocPrinting;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.DocPrintingLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:34
 */
@Component
public class PrintingRepositoryImpl extends PrintingRepository {

    @Resource
    private DocPrintingDao printingDao;
    @Resource
    private DocPrintingLibDao printingLibDao;
    @Resource
    private PrintingConverter printingConverter;

    protected PrintingRepositoryImpl() {
        super(Printing.class);
    }

    @Override
    protected LongId onInsert(Printing aggregate) {
        DocPrinting latestOne = printingDao.getOne(
                Wrappers.<DocPrinting>lambdaQuery()
                        .ge(DocPrinting::getCreateTime, LocalDate.now())
                        .orderByDesc(BaseModel::getCreateTime).last("limit 1"));
        PrintingCode code = latestOne == null ? new PrintingCode() : new PrintingCode(latestOne.getCode()).nextCode();
        aggregate.setCode(code);
        DocPrinting po = printingConverter.entity2Po(aggregate);
        printingDao.save(po);
        return new LongId(po.getId());
    }

    @Override
    protected Printing onSelect(LongId longId) {
        DocPrinting byId = printingDao.getById(longId.getId());
        return printingConverter.po2Entity(byId);
    }

    @Override
    protected void onUpdate(Printing aggregate, EntityDiff diff) {
        if (diff.isSelfModified()) {
            DocPrinting po = printingConverter.entity2Po(aggregate);
            printingDao.updateById(po);
        }
    }

    @Override
    protected void onDelete(LongId longId) {
        printingDao.remove(Wrappers.<DocPrinting>lambdaQuery().eq(DocPrinting::getId, longId.getId()));
    }

    @Override
    public Printing findLib(LongId longId, String version) {
        DocPrintingLib oneLib = printingLibDao.getOne(
                Wrappers.<DocPrintingLib>lambdaQuery()
                        .eq(DocPrintingLib::getOriginId, longId.getId())
                        .eq(DocPrintingLib::getVersion, version)
                        .last("limit 1")
        );
        return printingConverter.libPo2Entity(oneLib);
    }
}
