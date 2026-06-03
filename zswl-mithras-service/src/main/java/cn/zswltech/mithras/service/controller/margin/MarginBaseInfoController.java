package cn.zswltech.mithras.service.controller.margin;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.margin.MarginBaseInfoApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.margin.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewSubAuthCheckerNew;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginRecordInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.margin.service.MarginRecordService;
import cn.zswltech.mithras.margin.service.MarginWriteOffRecordService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @create: 2022-08-17
 **/
@RestController
@Slf4j
public class MarginBaseInfoController implements MarginBaseInfoApi {
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
//    @Resource
//    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private MarginRecordService marginRecordService;
//    @Resource
//    private MarginWriteOffRecordService marginWriteOffRecordService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private HttpServletResponse httpServletResponse;

//    @Override
//    public R<String> add(@Valid MarginBaseInfoAddREQ req) {
//        return R.ok(marginBaseInfoService.add(req));
//    }

    @Override
    public R<PageR<MarginBaseInfoListRSP>> list(@Valid MarginBaseInfoListREQ req) {
        return R.ok(marginBaseInfoService.list(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.MARGIN)
    public R<MarginBaseInfoRSP> detail(@Valid MarginBaseInfoDetailREQ req) {
        return R.ok(marginBaseInfoService.detail(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.MARGIN)
    public R<List<MarginRecordListRSP>> collectionList(@Valid MarginRecordListREQ req) {
        return R.ok(marginRecordService.list(req));
    }

//    @Override
//    public R<List<MarginwriteOffListRSP>> writeoffList(@Valid MarginwriteOffListREQ req) {
//        return R.ok(marginWriteOffRecordService.writeoffList(req));
//    }
    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.COLLECTION)
    public R<MarginRecordDetailRSP> collectionDetail(@Valid MarginRecordDetailREQ req) {
        return R.ok(marginRecordService.collectionDetail(req));
    }


    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewSubAuthCheckerNew.class,mapperClass = MarginRecordInfoMapper.class,businessModule = BusinessModuleEnum.MARGIN)
    public R<MarginRecordDetailRSP> recordDetail(@Valid MarginRecordDetailREQ req) {
        return R.ok(marginRecordService.detail(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewSubAuthCheckerNew.class,mapperClass = MarginRecordInfoMapper.class, businessModule = BusinessModuleEnum.MARGIN)
    public R<MarginDeductDetailRSP> deductDetail(@Valid MarginRecordDetailREQ req) {
        return R.ok(marginRecordService.deductDetail(req));
    }

//    @Override
//    public R<String> addRecord(@Valid MarginRecordAddREQ req, MultipartFile file) {
//        if (req.getCollectionAmount() == 0 || req.getCollectionAmount() == null){
//            return R.fail("实付金额不能为0！");
//        }
//        MarginRecordInfo info = new MarginRecordInfo();
//        BeanUtil.copyProperties(req,info);
//        info.setRecordType(RecordTypeEnum.COLLECTION.name());
//        info.setDataSource(String.valueOf(AccountUtil.getLoginInfo().getId()));
//        info.setSourceFlag(1);
//        marginRecordService.add(info,file);
//        return R.ok();
//    }

//    @Override
//    public R<String> addBackRecord(@Valid MarginRecordBackAddREQ req, MultipartFile file) {
//        MarginRecordInfo info = new MarginRecordInfo();
//        BeanUtil.copyProperties(req,info);
//        info.setRecordType(RecordTypeEnum.REFUND.name());
//        info.setDataSource(String.valueOf(AccountUtil.getLoginInfo().getId()));
//        info.setSourceFlag(1);
//        marginRecordService.add(info,file);
//        return R.ok();
//    }

//    @Override
//    public R<String> addDeductRecord(@Valid MarginRecordDeductAddREQ req) {
//        MarginBaseInfo info1 = marginBaseInfoMapper.selectById(req.getMarginId());
//        long collectionAmount = 0;
//        if (info1.getContractIsSettle() == 1){
//            collectionAmount = info1.getCollectionAmount();
//        }
//        if ((req.getDeductRent()+ req.getDeductPenaltyInterest()) > collectionAmount){
//            return R.fail("抵扣总金额大于可退金额！");
//        }
//        if (req.getDeductRent() != (req.getDeductPrincipal()+req.getDeductInterest())){
//            return R.fail("抵扣租金错误！");
//        }
//        MarginCashInfoREQ req1 = new MarginCashInfoREQ();
//        req1.setId(req.getCollectionId());
//        R<MarginCashInfoRSP> cashInfo = phaseInfo(req1);
//        if (cashInfo.getData() != null){
//            if (req.getDeductInterest() > cashInfo.getData().getLastDeductInterest()
//            || req.getDeductPrincipal() > cashInfo.getData().getLastDeductPrincipal()
//            || req.getDeductPenaltyInterest() > cashInfo.getData().getLastDeductPenaltyInterest()){
//                return R.fail("抵扣各款项需小于等于剩余可抵扣各个款项！");
//            }
//        }else {
//            R.fail("获取现金流信息失败！");
//        }
//        MarginRecordInfo info = new MarginRecordInfo();
//        BeanUtil.copyProperties(req,info);
//        info.setRecordType(RecordTypeEnum.REFUND.name());
//        info.setDataSource(String.valueOf(AccountUtil.getLoginInfo().getId()));
//        info.setSourceFlag(1);
//        info.setCollectionAmount(req.getDeductRent() + req.getDeductPenaltyInterest());
//        marginRecordService.add(info,null);
//        return R.ok();
//    }

//    @Override
//    public R<String> updateRecord(@Valid MarginRecordUpdateREQ req) {
//        return marginRecordService.update(req);
//    }

//    @Override
//    public R<List<MarginCashSelectRSP>> phaseSelect(@Valid CollectionSelectREQ req) {
//
//        List<CollectionBaseInfo> infoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
//                .eq(CollectionBaseInfo::getContractId, req.getContractId()).ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED)
//        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()).isNotNull(CollectionBaseInfo::getPaymentCode));
//        Map<String, List<CollectionBaseInfo>> codeMap = infoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getPaymentCode));
//        List<MarginCashSelectRSP> rsps = new LinkedList<>();
//        for (String o : codeMap.keySet()) {
//            MarginCashSelectRSP rsp = new MarginCashSelectRSP();
//            rsp.setLabel(o);
//            rsp.setValue(o);
//            List<SelectRSP> phases = new LinkedList<>();
//            for (CollectionBaseInfo info : codeMap.get(o)){
//                SelectRSP collectionInfo = new SelectRSP();
//                collectionInfo.setLabel(String.valueOf(info.getPhase()));
//                collectionInfo.setValue(String.valueOf(info.getId()));
//                phases.add(collectionInfo);
//            }
//            rsp.setChildren(phases);
//            rsps.add(rsp);
//        }
//        return R.ok(rsps);
//    }

//    @Override
//    public R<MarginCashInfoRSP> phaseInfo(MarginCashInfoREQ req) {
//        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(req.getId());
//        MarginCashInfoRSP rsp = new MarginCashInfoRSP();
//        rsp.setCode(baseInfo.getCode());
//        rsp.setCollectionId(baseInfo.getId());
//        rsp.setPlanCollectionAmount(baseInfo.getPlanCollectionAmount());
//        rsp.setPlanCollectionDate(baseInfo.getPlanCollectionDate());
//        rsp.setCashFlowItem(Optional.ofNullable(baseInfo.getCashFlowItem()).map(CashFlowItemEnum::of).map(c -> c.display).orElse(""));
//        rsp.setCashFlowAmount(baseInfo.getCashFlowAmount());
//        rsp.setPrincipal(baseInfo.getPrincipal());
//        rsp.setInterest(baseInfo.getInterest());
//        rsp.setPenaltyInterest(baseInfo.getPenaltyInterest());
//        rsp.setLastDeductPrincipal(LongUtil.null2zero(baseInfo.getPrincipal()) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
//        rsp.setLastDeductInterest(LongUtil.null2zero(baseInfo.getInterest()) - LongUtil.null2zero(baseInfo.getCollectionInterest()));
//        rsp.setLastDeductPenaltyInterest(LongUtil.null2zero(baseInfo.getPenaltyInterestAmount()) - LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()));
//        rsp.setLastDeductAmount(rsp.getLastDeductInterest()+ rsp.getLastDeductPenaltyInterest()+rsp.getLastDeductPrincipal());
//        return R.ok(rsp);
//    }

    @Override
    public R<Void> exportMarginList(@Valid MarginBaseInfoListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("保证金列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            marginBaseInfoService.exportList(req, httpServletResponse.getOutputStream());
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出保证金列表发生未知异常", e);
            return R.fail("导出保证金列表发生未知异常");
        }
    }
}
