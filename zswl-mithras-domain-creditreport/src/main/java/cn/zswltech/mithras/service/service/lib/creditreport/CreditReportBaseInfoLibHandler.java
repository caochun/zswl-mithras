package cn.zswltech.mithras.service.service.lib.creditreport;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportBaseInfo;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportBaseInfoLib;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class CreditReportBaseInfoLibHandler extends CreditReportLibAbstractHandler<CreditReportBaseInfoLib, CreditReportBaseInfo,CreditReportListDTO> {

    @Override
    protected CreditReportBaseInfoLib entity2Lib(CreditReportBaseInfo searchDO) {
        CreditReportBaseInfoLib libDO = BeanUtil.copyProperties(searchDO, CreditReportBaseInfoLib.class);
        return libDO;
    }

    @Override
    protected CreditReportBaseInfo lib2Entity(CreditReportBaseInfoLib libDO) {
        CreditReportBaseInfo reportDO = BeanUtil.copyProperties(libDO, CreditReportBaseInfo.class);
        return reportDO;
    }

    @Override
    protected CreditReportListDTO lib2Rsp(CreditReportBaseInfoLib f) {
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
}
