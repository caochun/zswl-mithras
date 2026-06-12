package cn.zswltech.mithras.leaseholdproperty.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.LeaseVatInvoiceApi;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemIdREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceCountRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceLockREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceQueryREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceRemoveREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceRetestREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceUpdateREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceUploadREQ;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseVatInvoiceApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class LeaseVatInvoiceController implements LeaseVatInvoiceApi {
    @Resource
    private LeaseVatInvoiceApplicationService leaseVatInvoiceApplicationService;

    @Override
    public R<Void> vatInvoiceUpload(LeaseVatInvoiceUploadREQ req) {
        return leaseVatInvoiceApplicationService.vatInvoiceUpload(req);
    }

    @Override
    public R<Void> anewUpload(LeaseVatInvoiceUploadREQ req) {
        return leaseVatInvoiceApplicationService.anewUpload(req);
    }

    @Override
    public R<PageR<LeaseVatInvoiceListRSP>> queryVatInvoiceList(LeaseVatInvoiceQueryREQ req) {
        return leaseVatInvoiceApplicationService.queryVatInvoiceList(req);
    }

    @Override
    public R<Void> remove(LeaseVatInvoiceRemoveREQ req) {
        return leaseVatInvoiceApplicationService.remove(req);
    }

    @Override
    public R<Void> update(LeaseVatInvoiceUpdateREQ req) {
        return leaseVatInvoiceApplicationService.update(req);
    }

    @Override
    public R<LeaseVatInvoiceCountRSP> count(LeaseItemIdREQ req) {
        return leaseVatInvoiceApplicationService.count(req);
    }

    @Override
    public R<String> amountCheckout(LeaseItemIdREQ req) {
        return leaseVatInvoiceApplicationService.amountCheckout(req);
    }

    @Override
    public R<Void> retest(LeaseVatInvoiceRetestREQ req) {
        return leaseVatInvoiceApplicationService.retest(req);
    }

    @Override
    public void exportExcel(LeaseVatInvoiceQueryREQ req) {
        leaseVatInvoiceApplicationService.exportExcel(req);
    }

    @Override
    public R<Void> locked(LeaseVatInvoiceLockREQ req) {
        return leaseVatInvoiceApplicationService.locked(req);
    }
}
