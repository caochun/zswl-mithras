package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentPolicyInfoMapper;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @create: 2023-03-09
 **/

@Component
public class PaymentPolicyCheckHandler extends FileModuleCheck {

    @Resource
    private PaymentPolicyInfoMapper paymentPolicyInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public void checkUpload(String moduleKey, Long mainId , String materialsType) {
        if (Objects.isNull(mainId)) {
            throw new AuthCheckException("id不能为空");
        }
        PaymentPolicyInfo paymentPolicyInfo = paymentPolicyInfoMapper.selectById(mainId);
        mainId = paymentPolicyInfo.getPaymentId();
//        super.checkUpload(BusinessModuleEnum.PAYMENT.name(), mainId, materialsType);
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(mainId);
        authCheck(paymentBaseInfo);
    }


    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {
    }


    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        // 再查找付款主数据
        PaymentPolicyInfo paymentPolicyInfo = paymentPolicyInfoMapper.selectById(materialsList.getBelongId());
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentPolicyInfo.getPaymentId());
        authCheck(paymentBaseInfo);
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds){
        if(ObjectUtil.isEmpty(fileIds)){
            return;
        }
        Set<Long> belongSet = materialsListService.getByIds(fileIds).stream().map(MaterialsList::getBelongId).collect(Collectors.toSet());
        List<PaymentPolicyInfo> paymentPolicyInfos = paymentPolicyInfoMapper.selectBatchIds(belongSet);
        for(PaymentPolicyInfo info : paymentPolicyInfos){
            // 再查找付款主数据
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(info.getPaymentId());
            authCheck(paymentBaseInfo);
        }
    }

    @Override
    public void checkList(String moduleKey, Long mainId){

    }

    @Override
    public String getModuleKey() {
        return "PAYMENTPOLICY";
    }
    private void authCheck(PaymentBaseInfo paymentBaseInfo) {
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(PaymentStatusEnum.CLOSED.name().equals(paymentBaseInfo.getPaymentStatus())){
            throw new MithrasException("该申请已关闭，不允许再修改有关信息");
        }
        if (!Objects.equals(AccountUtil.getLoginInfo().getId(), paymentBaseInfo.getCreateBy())) {
            throw new MithrasException("付款创建人才允许操作");
        }
    }
}
