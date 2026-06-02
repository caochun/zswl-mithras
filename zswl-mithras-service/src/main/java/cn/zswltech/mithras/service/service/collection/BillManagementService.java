package cn.zswltech.mithras.service.service.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.collection.BillManagementAddREQ;
import cn.zswltech.mithras.dto.collection.BillManagementListREQ;
import cn.zswltech.mithras.dto.collection.BillManagementModifyREQ;
import cn.zswltech.mithras.dto.collection.BillManagementRemoveREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.collection.BillTypeEnum;
import cn.zswltech.mithras.service.mapper.collection.BillManagementMapper;
import cn.zswltech.mithras.service.mapper.model.collection.BillManagement;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.ftp.oldftp.bo.BillFtpBO;
import cn.zswltech.mithras.ftp.oldftp.bo.CashFtpInfluenceBO;
import cn.zswltech.mithras.service.service.newftp.service.FtpService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @description 票据管理表
* @author vico
* @date 2023-06-05
*/
@Service
public class BillManagementService extends ServiceImpl<BillManagementMapper, BillManagement> {

    @Resource
    private BillManagementMapper billManagementMapper;
    @Resource
    private FtpService ftpService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(BillManagementAddREQ req) {
        BillManagement info = BeanUtil.copyProperties(req, BillManagement.class);
        if(YesOrNoNumberEnum.YES.getCode().equals(info.getBillBuyRateType()) && ObjectUtil.isNotEmpty(req.getContractId())) {
            CashFtpInfluenceBO cashFtpInfluenceBO = ftpService.getCashFtpInfluence(req.getContractId(), LocalDate.now());
            Set<Long> contractIds = new HashSet<>();
            contractIds.add(req.getContractId());
            info.setBillBuyRate(Long.valueOf(LongUtil.null2zero(ftpService.getCashFtp(cashFtpInfluenceBO, contractIds))));
        }
        billManagementMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(BillManagementModifyREQ req) {
        BillManagement originalInfo = billManagementMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        BillManagement info = BeanUtil.copyProperties(req, BillManagement.class);
        if(YesOrNoNumberEnum.YES.getCode().equals(info.getBillBuyRateType()) && ObjectUtil.isNotEmpty(req.getContractId())) {
            CashFtpInfluenceBO cashFtpInfluenceBO = ftpService.getCashFtpInfluence(req.getContractId(), LocalDate.now());
            Set<Long> contractIds = new HashSet<>();
            contractIds.add(req.getContractId());
            info.setBillBuyRate(Long.valueOf(LongUtil.null2zero(ftpService.getCashFtp(cashFtpInfluenceBO, contractIds))));
        }
        billManagementMapper.updateById(info);
    }

    public Page<BillManagement> list(BillManagementListREQ req) {
        return billManagementMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<BillManagement>lambdaQuery()
        .eq(BillManagement::getBillType, req.getBillType())
        .eq(BillManagement::getMainId, req.getMainId()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(BillManagementRemoveREQ req) {
        BillManagement originalInfo = billManagementMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        billManagementMapper.deleteById(req.getId());
    }

    public List<BillManagement> listByMainIdAndType(Long mainId, String type) {
        LambdaQueryWrapper<BillManagement> query = Wrappers.lambdaQuery();
        query.eq(BillManagement::getMainId, mainId);
        query.eq(BillManagement::getBillType, type);
        return this.list(query);
    }

    public void initAfterCqNotify(Long mainId, String type) {
        // 生成默认票据并填充最新的买入价
        BillFtpBO billFtpBO = ftpService.getBillFtp(LocalDate.now());
        BillManagement billManagement = new BillManagement();
        billManagement.setMainId(mainId);
        billManagement.setBillType(type);
        if (NumberUtil.isNumber(billFtpBO.getBillFtpBuy())) {
            billManagement.setBillBuyRateType(YesOrNoNumberEnum.NO.getCode());
        } else {
            billManagement.setBillBuyRateType(YesOrNoNumberEnum.YES.getCode());
        }
        this.save(billManagement);
    }

    public List<BillManagement> listByReceiptId(Long receiptId) {
        List<BillManagement> result = new LinkedList<>();
        // 付款票据
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(receiptId);
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<PaymentActualDetail> paymentQuery = Wrappers.lambdaQuery();
            paymentQuery.in(PaymentActualDetail::getPaymentId, paymentIds);
            paymentQuery.eq(PaymentActualDetail::getPaymentMethod, GlobalConstants.CQ_PAYMENT_METHOD_PJ);
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.list(paymentQuery);
            if (CollectionUtil.isNotEmpty(paymentActualDetailList)) {
                Set<Long> ids = paymentActualDetailList.stream().map(PaymentActualDetail::getId).collect(Collectors.toSet());
                LambdaQueryWrapper<BillManagement> query = Wrappers.lambdaQuery();
                query.in(BillManagement::getMainId, ids);
                query.eq(BillManagement::getBillType, BillTypeEnum.PAYMENT.name());
                List<BillManagement> list = this.list(query);
                if (CollectionUtil.isNotEmpty(list)) {
                    result.addAll(list);
                }
            }
        }
        // 收款票据
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listRentByReceiptId(receiptId);
        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            Set<Long> collectionIds = collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<CollectionRecordInfo> collectQuery = Wrappers.lambdaQuery();
            collectQuery.in(CollectionRecordInfo::getCollectionId, collectionIds);
            collectQuery.eq(CollectionRecordInfo::getCollectionType, GlobalConstants.CQ_PAYMENT_METHOD_PJ);
            List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoService.listByCollectionIds(collectionIds);
            if (CollectionUtil.isNotEmpty(collectionRecordInfoList)) {
                Set<Long> ids = collectionRecordInfoList.stream().map(CollectionRecordInfo::getId).collect(Collectors.toSet());
                LambdaQueryWrapper<BillManagement> query = Wrappers.lambdaQuery();
                query.in(BillManagement::getMainId, ids);
                query.eq(BillManagement::getBillType, BillTypeEnum.COLLECTION.name());
                List<BillManagement> list = this.list(query);
                if (CollectionUtil.isNotEmpty(list)) {
                    result.addAll(list);
                }
            }
        }
        return result;
    }
}