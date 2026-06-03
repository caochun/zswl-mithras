/*
package cn.zswltech.mithras.service.service.lib.creditreport;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpBankAccount;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpBankAccountLib;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportDO;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportLibDO;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class CreditReportLibHandler extends LibAbstractHandler<CreditReportLibDO, CreditReportDO,CreditReportListDTO> {

    @Override
    protected CreditReportLibDO entity2Lib(CreditReportDO searchDO) {
        CreditReportLibDO libDO = BeanUtil.copyProperties(searchDO, CreditReportLibDO.class);
        return libDO;
    }

    @Override
    protected CreditReportDO lib2Entity(CreditReportLibDO libDO) {
        CreditReportDO reportDO = BeanUtil.copyProperties(libDO, CreditReportDO.class);
        return reportDO;
    }

    @Override
    protected CreditReportListDTO lib2Rsp(CreditReportLibDO f) {
        return null;
    }

    @Override
    public String libMainIdFieldName() {
        return "id";
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
        return BusinessModuleEnum.CREDIT_REPORT_SELECT;
    }
}
*/
