package cn.zswltech.mithras.creditreport.versioning;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.creditreport.model.CreditReportClientItem;
import cn.zswltech.mithras.creditreport.model.CreditReportClientItemLib;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class CreditReportClientItemLibHandler extends CreditReportLibAbstractHandler<CreditReportClientItemLib, CreditReportClientItem,CreditReportListDTO> {

    @Override
    protected CreditReportClientItemLib entity2Lib(CreditReportClientItem searchDO) {
        CreditReportClientItemLib libDO = BeanUtil.copyProperties(searchDO, CreditReportClientItemLib.class);
        return libDO;
    }
    
    @Override
    protected CreditReportClientItem lib2Entity(CreditReportClientItemLib libDO) {
        CreditReportClientItem reportDO = BeanUtil.copyProperties(libDO, CreditReportClientItem.class);
        return reportDO;
    }

    @Override
    protected CreditReportListDTO lib2Rsp(CreditReportClientItemLib f) {
        return null;
    }

    @Override
    public String libMainIdFieldName() {
        return "credit_report_base_info_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "credit_report_base_info_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }
}
