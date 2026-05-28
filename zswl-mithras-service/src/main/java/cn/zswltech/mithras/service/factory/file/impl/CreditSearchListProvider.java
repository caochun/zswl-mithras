package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.file.ext.FileListREQCreditSearchExt;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportMaterialTypeEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditSearchFileQueryType;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import cn.zswltech.mithras.service.others.MithrasException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bigbear
 */
@Component
public class CreditSearchListProvider extends AbstractFileListProvider {
    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.CREDIT_REPORT_SELECT;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        FileListExtQuery extQuery = new FileListExtQuery();
        if(CollectionUtils.isNotEmpty(req.getMaterialsTypes()))
        {
            if (CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT.name().equals(req.getMaterialsTypes().get(0))) {
                if (req.getExt() == null) {
                    throw new MithrasException("缺少必填参数：文件查询类型");
                }
                FileListREQCreditSearchExt extREQ = JSONObject.parseObject(JSON.toJSONString(req.getExt()), FileListREQCreditSearchExt.class);
                extQuery.setMaterialsSubTypes(CreditSearchFileQueryType.listAll());
                extQuery.setClientId(extREQ.getClientId());
            }
        }
        return listGroup(req, extQuery);
    }

    @Override
    public PageR<FileListRSP> list(FileListREQ req) {
        FileListExtQuery extQuery = new FileListExtQuery();
       /* if(CollectionUtils.isEmpty(req.getMaterialsTypes()))
        {
            if (CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT.name().equals(req.getMaterialsTypes().get(0))) {
                if (req.getExt() == null) {
                    throw new MithrasException("缺少必填参数：文件查询类型");
                }
                FileListREQCreditSearchExt extREQ = JSONObject.parseObject(JSON.toJSONString(req.getExt()), FileListREQCreditSearchExt.class);
                extQuery.setMaterialsSubTypes(CreditSearchFileQueryType.listAll());
                extQuery.setClientId(extREQ.getClientId());
            }
        }*/
        return list(req, extQuery);
    }
}
