package cn.zswltech.mithras.margin.service;

import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.margin.application.port.model.WarrantyPlannedReceivableCommand;
import cn.zswltech.mithras.margin.persistence.mapper.WarrantyBaseInfoMapper;
import cn.zswltech.mithras.margin.persistence.model.WarrantyBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @create: 2022-08-17
 **/
@Slf4j
@Service
public class WarrantyBaseInfoService extends ServiceImpl<WarrantyBaseInfoMapper, WarrantyBaseInfo> {

    public void savePlannedReceivable(WarrantyPlannedReceivableCommand command) {
        WarrantyBaseInfo info = this.getOne(Wrappers.<WarrantyBaseInfo>lambdaQuery()
                .eq(WarrantyBaseInfo::getContractId, command.getContractId())
                .last(StringUtil.mysqlLimitOne()));
        if (info == null) {
            info = new WarrantyBaseInfo();
            info.setContractId(command.getContractId());
            info.setContractCode(command.getContractCode());
            info.setClientId(command.getClientId());
            if (!command.isRecycle()) {
                info.setPlanWarrantyAmount(command.getAmount());
            }
            info.setTotalReceivableAmount(command.getTotalReceivableAmount());
            info.setPlanWarrantyDate(command.getPlanDate());
            info.setWarrantyCode(getCode(command.getContractCode()));
            info.setCollectionAmount(0L);
            this.save(info);
            return;
        }
        if (!command.isRecycle()) {
            info.setPlanWarrantyAmount(LongUtil.add(info.getPlanWarrantyAmount(), command.getAmount()));
        }
        info.setPlanWarrantyDate(command.getPlanDate());
        info.setTotalReceivableAmount(command.getTotalReceivableAmount());
        this.updateById(info);
    }

    private String getCode(String contractCode) {
        String year = contractCode.substring(contractCode.indexOf("【") + 1, contractCode.indexOf("】"));
        String code = contractCode.substring(contractCode.indexOf("(") + 1, contractCode.indexOf(")"));
        return year + code.substring(0, code.indexOf("-")) + code.substring(code.indexOf("-") + 1) + "-zbj";
    }
}
