package cn.zswltech.mithras.service.service.capital;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.receipt.ContractReceiptQueryActualTaxREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractReceiptComputeActualTaxRSP;
import cn.zswltech.mithras.service.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.service.mapper.model.nettingRefund.NettingRefund;
import cn.zswltech.mithras.service.mapper.nettingRefund.NettingRefundMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhouning
 * @date 2024/07/17
 * @description
 */


@Service
public class NettingRefundService extends ServiceImpl<NettingRefundMapper, NettingRefund> {

}
