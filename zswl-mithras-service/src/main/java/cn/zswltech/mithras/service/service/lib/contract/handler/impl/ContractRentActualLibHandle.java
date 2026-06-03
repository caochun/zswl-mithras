package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualRSP;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.versioning.handler.ContractLibAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    private CollectionBaseInfoService collectionBaseInfoService;


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
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getRentActualId, tableData.getId())
                .last("limit 1"));
        if (ObjectUtil.isNotEmpty(collectionBaseInfo)) {
            tableData.setReceived(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus()));
            tableData.setReceivedDate(collectionBaseInfo.getCollectionDate());
            tableData.setReceivedAmount(collectionBaseInfo.getCollectionAmount());
        } else {
            tableData.setReceived(false);
        }
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
