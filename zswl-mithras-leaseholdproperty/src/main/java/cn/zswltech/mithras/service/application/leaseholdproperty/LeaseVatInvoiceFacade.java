package cn.zswltech.mithras.service.application.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseVatInvoiceApplicationService;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemVatInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author yupengfei
 * @date 2024/5/9 15:12
 */
@Slf4j
@Service
public class LeaseVatInvoiceFacade implements LeaseVatInvoiceApplicationService {

    @Resource
    private LeaseItemVatInvoiceService leaseItemVatInvoiceService;

    @Resource
    private HttpServletResponse httpServletResponse;

    /**
     * 增值税发票上传
     */
    @Override
    public R<Void> vatInvoiceUpload(LeaseVatInvoiceUploadREQ req) {
        return leaseItemVatInvoiceService.vatInvoiceUpload(req);
    }

    /**
     * 增值税发票重新上传
     */
    @Override
    public R<Void> anewUpload(LeaseVatInvoiceUploadREQ req) {
        return leaseItemVatInvoiceService.anewUpload(req);
    }

    /**
     * 增值税发票分页列表
     */
    @Override
    public R<PageR<LeaseVatInvoiceListRSP>> queryVatInvoiceList(LeaseVatInvoiceQueryREQ req) {
        return leaseItemVatInvoiceService.queryVatInvoiceList(req);
    }

    /**
     * 增值税发票批量删除
     */
    @Override
    public R<Void> remove(LeaseVatInvoiceRemoveREQ req) {
        return leaseItemVatInvoiceService.invoiceRemove(req);
    }

    /**
     * 增值税发票批量修改
     */
    @Override
    public R<Void> update(LeaseVatInvoiceUpdateREQ req) {
        return leaseItemVatInvoiceService.invoiceUpdate(req);
    }

    /**
     * 统计各状态发票数量
     */
    @Override
    public R<LeaseVatInvoiceCountRSP> count(LeaseItemIdREQ req) {
        return leaseItemVatInvoiceService.invoiceCount(req);
    }

    /**
     * 增值税发票金额校验
     */
    @Override
    public R<String> amountCheckout(LeaseItemIdREQ req) {
        return leaseItemVatInvoiceService.amountCheckout(req);
    }

    /**
     * 增值税发票重新验真
     */
    @Override
    public R<Void> retest(LeaseVatInvoiceRetestREQ req) {
        return leaseItemVatInvoiceService.retest(req);
    }

    /**
     * 增值税发票下载
     */
    @Override
    public void exportExcel(LeaseVatInvoiceQueryREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租赁物清单发票列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            leaseItemVatInvoiceService.exportExcel(httpServletResponse.getOutputStream(), req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出租赁物清单发票列表发生未知异常", e);
            throw new MithrasException("导出租赁物清单发票列表发生未知异常");
        }
    }

    @Override
    public R<Void> locked(LeaseVatInvoiceLockREQ req) {
        return leaseItemVatInvoiceService.locked(req);
    }
}
