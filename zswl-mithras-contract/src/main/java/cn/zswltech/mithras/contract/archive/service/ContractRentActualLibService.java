package cn.zswltech.mithras.contract.archive.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentActualLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author vico
 * @description 合同明细-实际租金
 * @date 2022-08-22
 */
@Service
public class ContractRentActualLibService extends ServiceImpl<ContractRentActualLibMapper, ContractRentActualLib> {

    public List<ContractRentActualLib> listByReceipt(Long receiptId) {
        LambdaQueryWrapper<ContractRentActualLib> query = Wrappers.lambdaQuery();
        query.eq(ContractRentActualLib::getReceiptId, receiptId);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        return this.list(query);
    }

    public List<ContractRentActual> listByReceiptVersion(Long receiptId, String version) {
        LambdaQueryWrapper<ContractRentActualLib> query = Wrappers.lambdaQuery();
        query.eq(ContractRentActualLib::getReceiptId, receiptId);
        query.eq(ContractRentActualLib::getVersion, version);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        return this.list(query).stream().map(this::lib2Entity).collect(Collectors.toList());
    }

    public List<ContractRentActualLib> listLibByReceiptVersion(Long receiptId, String version) {
        LambdaQueryWrapper<ContractRentActualLib> query = Wrappers.lambdaQuery();
        query.eq(ContractRentActualLib::getReceiptId, receiptId);
        query.eq(ContractRentActualLib::getVersion, version);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        return this.list(query);
    }

    public List<ContractRentActual> listByContractVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractRentActualLib> query = Wrappers.lambdaQuery();
        query.eq(ContractRentActualLib::getContractId, contractId);
        query.eq(ContractRentActualLib::getVersion, version);
        query.orderByAsc(ContractRentActual::getReceiptId);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        return this.list(query).stream().map(this::lib2Entity).collect(Collectors.toList());
    }

    public List<ContractRentActualLib> listLibByContractVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractRentActualLib> query = Wrappers.lambdaQuery();
        query.eq(ContractRentActualLib::getContractId, contractId);
        query.eq(ContractRentActualLib::getVersion, version);
        query.orderByAsc(ContractRentActual::getReceiptId);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        return this.list(query);
    }

    public List<ContractRentActualLib> listByContractReceiptVersion(Long contractId, Set<Long> receiptIds, List<String> versions) {
        LambdaQueryWrapper<ContractRentActualLib> query = Wrappers.lambdaQuery();
        query.eq(ContractRentActualLib::getContractId, contractId);
        query.in(ObjectUtil.isNotEmpty(versions), ContractRentActualLib::getVersion, versions);
        query.in(ObjectUtil.isNotEmpty(receiptIds), ContractRentActualLib::getReceiptId, receiptIds);
        query.isNotNull(ContractRentActualLib::getCashFlowCode);
        query.ne(ContractRentActualLib::getCashFlowCode, "");
        query.orderByAsc(ContractRentActual::getReceiptId);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        return this.list(query);
    }

    private ContractRentActual lib2Entity(ContractRentActualLib lib) {
        return BeanUtil.copyProperties(lib, ContractRentActual.class);
    }

}
