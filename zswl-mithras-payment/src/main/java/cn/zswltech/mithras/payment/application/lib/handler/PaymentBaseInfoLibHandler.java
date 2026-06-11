package cn.zswltech.mithras.payment.application.lib.handler;

import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.payment.application.lib.PaymentLibAssembler;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfoLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class PaymentBaseInfoLibHandler
        extends PaymentAbstractHandler<PaymentBaseInfoLib, PaymentBaseInfo, PaymentDetailRsp> {
    @Resource
    private PaymentLibAssembler paymentLibAssembler;

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("paymentStatus");
        fields.add("paymentProcessStatus");
        fields.add("writeOffState");
        fields.add("writeOffUserIds");
        fields.add("paidInDate");
        fields.add("actualDetailCount");
        fields.add("conProjectType");
        fields.add("conApplyCreditAmount");
        fields.add("conBizDeptId");
        fields.add("conBizDeptLeaderId");
        fields.add("conRiskControlManagerId");
        fields.add("conBizDivisionLeaderId");
        return fields;
    }

    @Override
    protected PaymentBaseInfoLib entity2Lib(PaymentBaseInfo f) {
        return paymentLibAssembler.baseInfoEntity2Lib(f);
    }

    @Override
    protected PaymentBaseInfo lib2Entity(PaymentBaseInfoLib t) {
        return paymentLibAssembler.baseInfoLib2Entity(t);
    }

    @Override
    protected PaymentDetailRsp lib2Rsp(PaymentBaseInfoLib f) {
        PaymentDetailRsp rsp = paymentLibAssembler.baseInfoLib2DetailRsp(f);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public PaymentInfoModule getSubModule() {
        return PaymentInfoModule.BASE_INFO;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public boolean isMainTable() {
        return true;
    }

}
