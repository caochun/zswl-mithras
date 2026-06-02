package cn.zswltech.mithras.service.service.afterlese;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportBaseREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportBaseRSP;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportBase;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.service.bo.AfterLeaseClientDataBO;
import cn.zswltech.mithras.service.service.bo.AfterLeaseListExpandBO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
public interface AfterLeaseCheckReportBaseService extends IService<NewAfterLeaseCheckReportBase> {
    NewAfterLeaseCheckReportBase getReportBase(Long checkPlanClientId);

    AfterLeaseCheckReportBaseRSP getCheckReportBaseRSP(Long checkPlanClientId, String version);

    Long saveReportBase(AfterLeaseCheckReportBaseREQ req);

    void removeByCheckPlanClientId(Long checkPlanClientId);

    AfterLeaseClientDataBO getAfterLeaseClientDataBO(Long clientId, boolean rich);

    Map<Long, AfterLeaseClientDataBO> getAfterLeaseClientDataBO(Map<Long, Client> clientMap,
                                                                 Map<Long, List<ContractBaseInfo>> contractMap);

    List<AfterLeaseListExpandBO> getAfterLeaseListExpandBO(List<Long> planIds);
}
