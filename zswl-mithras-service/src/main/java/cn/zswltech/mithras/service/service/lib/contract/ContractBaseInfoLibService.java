package cn.zswltech.mithras.service.service.lib.contract;

import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.service.contract.impl.ContractPriceServiceImpl;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author vico
 * @description 合同基本信息表
 * @date 2022-08-22
 */
@Service
public class ContractBaseInfoLibService extends ServiceImpl<ContractBaseInfoLibMapper, ContractBaseInfoLib> {

    @Resource
    private ContractBaseInfoLibHandler baseInfoLibHandler;
    @Resource
    private ContractPriceServiceImpl contractPriceService;

    public ContractBaseInfoLib getLatest(Long contractId) {
        LambdaQueryWrapper<ContractBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ContractBaseInfoLib::getOriginId, contractId);
        query.eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        query.orderByDesc(ContractBaseInfoLib::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public ContractBaseInfoLib getByOriginIdVersion(Long originId, String version) {
        LambdaQueryWrapper<ContractBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ContractBaseInfoLib::getOriginId, originId);
        query.eq(ContractBaseInfoLib::getVersion, version);
        return this.getOne(query);
    }

    public List<ContractBaseInfoLib> listByContractIds(Collection<Long> contractIds) {
        LambdaQueryWrapper<ContractBaseInfoLib> query = Wrappers.lambdaQuery();
        query.in(ContractBaseInfoLib::getOriginId, contractIds);
        return this.list(query);
    }

    public ContractBaseInfoDetailRSP detail(Long id, String version) {
        ContractBaseInfoLib versionLib = baseMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery().eq(ContractBaseInfoLib::getOriginId, id)
                .eq(ContractBaseInfoLib::getVersion, version)
        );
        return Optional.ofNullable(versionLib).map(baseInfoLibHandler::actualLib2Rsp).orElse(new ContractBaseInfoDetailRSP());
    }


    /**
     * 统计指定客户的在租合同授信金额之和
     *
     * @param clientIds
     * @return
     */
    public BigDecimal newestContractAmountClientId(Set<Long> clientIds) {
        List<ContractBaseInfoLib> contractBaseInfoLibs = baseMapper.listNewestContractByClientIds(clientIds);
        Set<Long> contractIds = contractBaseInfoLibs.stream().map(ContractBaseInfoLib::getOriginId).collect(Collectors.toSet());
        Map<Long, Long> priceAmounts = contractPriceService.queryNewestContractAmount(contractIds);
        return priceAmounts.values().stream()
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}