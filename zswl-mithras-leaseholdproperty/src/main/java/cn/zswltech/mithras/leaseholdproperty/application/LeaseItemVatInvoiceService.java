package cn.zswltech.mithras.leaseholdproperty.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemIdREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceCountRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceLockREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceQueryREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceRemoveREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceRetestREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceUpdateREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceUploadREQ;
import cn.zswltech.mithras.leaseholdproperty.model.LeaseItemVatInvoice;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.ServletOutputStream;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/8 16:55
 */
public interface LeaseItemVatInvoiceService extends IService<LeaseItemVatInvoice> {

    R<Void> vatInvoiceUpload(LeaseVatInvoiceUploadREQ req);

    R<Void> anewUpload(LeaseVatInvoiceUploadREQ req);

    R<PageR<LeaseVatInvoiceListRSP>> queryVatInvoiceList(LeaseVatInvoiceQueryREQ req);

    R<Void> invoiceRemove(LeaseVatInvoiceRemoveREQ req);

    R<Void> invoiceUpdate(LeaseVatInvoiceUpdateREQ req);

    R<LeaseVatInvoiceCountRSP> invoiceCount(LeaseItemIdREQ req);

    R<String> amountCheckout(LeaseItemIdREQ req);

    R<Void> retest(LeaseVatInvoiceRetestREQ req);

    R<Void> exportExcel(ServletOutputStream outputStream, LeaseVatInvoiceQueryREQ req);

    R<Void> locked(LeaseVatInvoiceLockREQ req);

    List<String> getLockedFileName(Long leaseholdId, String operateType);
}
