package cn.zswltech.mithras.contract.mapper.contract;

import cn.zswltech.mithras.dto.incomeSharing.ReceiptConditionQuery;
import cn.zswltech.mithras.dto.incomeSharing.ReceiptConditionResult;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
@Repository
public interface ContractReceiptMapper extends CustomBaseMapper<ContractReceipt> {
    List<Long> listNewTenId();


    Page<ReceiptConditionResult> queryReceiptCondition(@Param("page") Page<ReceiptConditionResult> page,@Param("query") ReceiptConditionQuery query);

    List<ContractReceiptLib> queryNewestReceiptIrr(@Param("receiptIds") Set<Long> receiptIds);
}
