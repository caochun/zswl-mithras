package cn.zswltech.mithras.application.orchestration.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.text.ContractTextStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.text.SigningWayEnum;
import cn.zswltech.mithras.contract.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.IContractSignInfo;
import cn.zswltech.mithras.contract.mapper.contract.ContractSignInfoMapper;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSignInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/11/18
 * @description
 */
@Slf4j
@Service
public class ContractSignInfoService extends ServiceImpl<ContractSignInfoMapper, ContractSignInfo> {

    @Resource
    private ContractSignInfoService thisService;

    public Map<Long, List<ContractSignInfo>> getContractSignInfoMap(Long contractId) {
        LambdaQueryWrapper<ContractSignInfo> query = Wrappers.lambdaQuery();
        query.eq(ContractSignInfo::getContractId, contractId);
        List<ContractSignInfo> result = this.list(query);
        if (CollectionUtil.isEmpty(result)) {
            return Collections.emptyMap();
        }
        return result.stream().collect(Collectors.groupingBy(ContractSignInfo::getFileId));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveContractSignInfo(AbstractContractRender<?> service, Object o, Long contractId, Long fileId, Set<Long> signatories) {
        if (Objects.isNull(contractId)) {
            return;
        }
        if (CollectionUtil.isEmpty(signatories)) {
            return;
        }
        List<ContractSignInfo> toInserList = signatories.stream().map(e -> {
            ContractSignInfo contractSignInfo = new ContractSignInfo();
            contractSignInfo.setContractId(contractId);
            contractSignInfo.setFileId(fileId);
            if (e != 0) {
                // 签约方不是租赁公司的需要默认设置为线下签约
                contractSignInfo.setSignWay(SigningWayEnum.OFFLINE_SIGN.name());
            }
            if (service.isShowFile(o)) {
                contractSignInfo.setFaceSignShowFlag(YesOrNoNumberEnum.YES.getCode());
            } else {
                contractSignInfo.setFaceSignShowFlag(YesOrNoNumberEnum.NO.getCode());
            }
            contractSignInfo.setFileTemplateKey(service.getTemplateKey(o));
            contractSignInfo.setSignStatus(ContractTextStatusEnum.NO_SIGNED.name());
            contractSignInfo.setSignatory(e);
            return contractSignInfo;
        }).collect(Collectors.toList());
        thisService.saveBatch(toInserList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveContractSignInfoSettle(AbstractContractRender<?> service, Object o, Long contractId, Long fileId, Set<Long> signatories) {
        if (Objects.isNull(contractId)) {
            return;
        }
        if (CollectionUtil.isEmpty(signatories)) {
            return;
        }
        List<ContractSignInfo> toInserList = signatories.stream().map(e -> {
            ContractSignInfo contractSignInfo = new ContractSignInfo();
            contractSignInfo.setContractId(contractId);
            contractSignInfo.setFileId(fileId);
            // 租赁线上先签默认设置为租赁线上先签
            contractSignInfo.setSignWay(SigningWayEnum.LEASE_ONLINE_SIGN.name());
            if (service.isShowFile(o)) {
                contractSignInfo.setFaceSignShowFlag(YesOrNoNumberEnum.YES.getCode());
            } else {
                contractSignInfo.setFaceSignShowFlag(YesOrNoNumberEnum.NO.getCode());
            }
            contractSignInfo.setFileTemplateKey(service.getTemplateKey(o));
            contractSignInfo.setSignStatus(ContractTextStatusEnum.NO_SIGNED.name());
            contractSignInfo.setSignatory(e);
            return contractSignInfo;
        }).collect(Collectors.toList());
        thisService.saveBatch(toInserList);
    }
}
