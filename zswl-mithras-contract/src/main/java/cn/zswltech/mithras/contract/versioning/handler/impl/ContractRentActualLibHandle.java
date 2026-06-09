package cn.zswltech.mithras.contract.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.contract.versioning.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.contract.versioning.application.CollectionRentActualReceiptStatus;
import cn.zswltech.mithras.contract.versioning.application.CollectionRentActualReceiptStatusResolver;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @ClassName ContractRentEstimateLibHandle
 * @Description
 * @Author jackerhe
 * @Date 2022/8/23 7:26 下午
 * @Version 1.0
 **/

@Service
public class ContractRentActualLibHandle
        extends ContractLibAbstractHandler<ContractRentActualLib, ContractRentActual, ContractRentActualRSP> {

    @Lazy
    @Resource
    private CollectionRentActualReceiptStatusResolver collectionRentActualReceiptStatusResolver;


    @Override
    protected ContractRentActualLib entity2Lib(ContractRentActual f) {

        return BeanUtil.copyProperties(f, ContractRentActualLib.class);
    }

    @Override
    protected ContractRentActual lib2Entity(ContractRentActualLib t) {
        return BeanUtil.copyProperties(t, ContractRentActual.class);
    }

    @Override
    protected ContractRentActualRSP lib2Rsp(ContractRentActualLib f) {
        ContractRentActualRSP tableData = BeanUtil.copyProperties(f, ContractRentActualRSP.class);
        tableData.setDate(LocalDateTimeUtil.format(f.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
        tableData.setPhase(f.getCashFlowPhase());
        tableData.setId(f.getOriginId());
        CollectionRentActualReceiptStatus status = collectionRentActualReceiptStatusResolver.resolve(tableData.getId());
        tableData.setReceived(status.getReceived());
        tableData.setReceivedDate(status.getReceivedDate());
        tableData.setReceivedAmount(status.getReceivedAmount());
        return tableData;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.ACTUAL_ESTIMATE_ITEM;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
