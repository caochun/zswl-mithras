package cn.zswltech.mithras.service.service.third.financial;

import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;

import java.util.List;


/**
 * @ClassName FinancialManagerService
 * @Description 对接苍穹管理接口
 * @Author jackerhe
 * @Date 2022/10/28 2:05 下午
 * @Version 1.0
 **/
public interface FinancialManagerService {

    void cqReceiveExec(List<CollectionBaseInfo> reqs, ProcessModelTypeEnum processModelTypeEnum);

    void cqPaymentExec(Long paymentId);

    //合同结清是调用，退保证金
    void earnestRecord(Long contractId);

    //罚息收款  -- 暂定不传了
    void collectionExec(List<CollectionBaseInfo> reqs);

}
