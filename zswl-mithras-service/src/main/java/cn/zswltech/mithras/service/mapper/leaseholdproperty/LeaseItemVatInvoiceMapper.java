package cn.zswltech.mithras.service.mapper.leaseholdproperty;

import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVatInvoiceQueryREQ;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemVatInvoice;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/8 16:57
 */
public interface LeaseItemVatInvoiceMapper extends CustomBaseMapper<LeaseItemVatInvoice> {

    List<LeaseItemVatInvoice> queryVatInvoiceList(@Param("req") LeaseVatInvoiceQueryREQ req);

    List<String> queryRepeatInvoiceNo(@Param("leaseholdId") Long leaseholdId);
}
