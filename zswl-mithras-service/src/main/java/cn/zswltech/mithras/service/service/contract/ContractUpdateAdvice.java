/*
package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.contract.ContractCanChangeRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.contract.impl.ContractBaseInfoServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;

import java.util.Collections;
import java.util.Objects;

*/
/**
 * @author
 * @description 合同变更其他后执行的切面操作 todo
 * @date 2022-07-19
 *//*

public interface ContractUpdateAdvice {

    default void saveCheck(Long contractId) {
        ContractBaseInfoService baseInfoService = SpringContextHolder.getBean(ContractBaseInfoServiceImpl.class);
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginUser)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        if (Objects.isNull(contractId)) {
            throw new MithrasException("合同信息不存在，不可修改数据");
        }
        ContractCanChangeRSP rsp = baseInfoService.canUpdateContractProcessStatus(Collections.singletonList(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name()), contractId);
        if (!rsp.getCanProcess()) {
            throw new MithrasException(rsp.getMessage());
        }
        //判断子类型
        if(ObjectUtils.isNotEmpty(rsp.getTwoStatus()) && !ContractChangeTypeEnum.OTHER.name().equals(rsp.getTwoStatus())){
            throw new MithrasException("合同变更处于" + ContractChangeTypeEnum.of(rsp.getTwoStatus()).display);
        }

    }

    default void recordStatus(Long contractId) {
        ContractBaseInfoService baseInfoService = SpringContextHolder.getBean(ContractBaseInfoServiceImpl.class);
        baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.OTHER.name(), contractId);
    }

}
*/
