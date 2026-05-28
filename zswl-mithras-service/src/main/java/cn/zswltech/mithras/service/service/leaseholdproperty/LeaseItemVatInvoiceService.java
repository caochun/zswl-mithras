package cn.zswltech.mithras.service.service.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemVatInvoice;
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
