package cn.zswltech.mithras.application.orchestration.adapter.basedata;

import cn.zswltech.mithras.basedata.application.bankaccount.BaseDataBankAccountProjectPort;
import cn.zswltech.mithras.dto.basedata.ContractAccountPayListREQ;
import cn.zswltech.mithras.dto.basedata.ContractAccountPayListRSP;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BaseDataBankAccountProjectPortAdapter implements BaseDataBankAccountProjectPort {

    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    @Override
    public List<ContractAccountPayListRSP> nameList(ContractAccountPayListREQ req) {
        List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name())
                .eq(ProjReviewBaseInfo::getProjCode, req.getProjCode())
                .orderByDesc(ProjReviewBaseInfo::getId));
        if (projReviewBaseInfoList == null || projReviewBaseInfoList.isEmpty()) {
            return new ArrayList<>();
        }
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoList.get(0);
        ProjReviewBaseInfoDetailRSP rsp = reviewBaseInfoService.detail(projReviewBaseInfo.getId(), null);
        if (rsp == null) {
            return new ArrayList<>();
        }
        Set<String> clientNames = new HashSet<>();
        if (rsp.getLesseeInfo() != null) {
            for (ClientInfo clientInfo : rsp.getLesseeInfo()) {
                if (StringUtils.isNotBlank(clientInfo.getClientName())) {
                    clientNames.add(clientInfo.getClientName());
                }
            }
        }
        List<ContractAccountPayListRSP> res = new ArrayList<>();
        for (String accountName : clientNames) {
            ContractAccountPayListRSP accountPayListRSP = new ContractAccountPayListRSP();
            accountPayListRSP.setAccountName(accountName);
            res.add(accountPayListRSP);
        }
        return res;
    }
}
