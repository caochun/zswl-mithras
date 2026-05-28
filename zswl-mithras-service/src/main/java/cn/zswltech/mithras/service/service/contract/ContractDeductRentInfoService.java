package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.mapper.model.contract.ContractDeductRentInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractRetreatInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author vico
 * @description 合同抵扣租金信息
 * @date 2022-08-12
 */
public interface ContractDeductRentInfoService extends IService<ContractDeductRentInfo> {
    /**
     * 根据现金流编号对 付款计划加锁
     */
    void lockByCode(String code);

    /**
     * 根据现金流编号对 付款计划解锁
     */
    void unlockByCode(String code);

    /**
     * 判断合同下是否有流程中的保证金退抵流程
     */
    boolean checkByContractId(String contractId);

    /**
     * 判断退抵流程审批通过的部分，之前有退回保证金但未发送通知，校验租金是否核销完毕，是则发送通知到角色“出纳”
     */
    void checkRentAndSend(ContractRetreatInfo contractRetreatInfo);

    void getHeadUserIds(OrgDO userDept, Map<String, Object> varMap);

    /**
     * 结清流程发起的时候自动复制 退抵审批通过的附件
     * */
    void sendFileToSettle(Long contractId, String operation);
}