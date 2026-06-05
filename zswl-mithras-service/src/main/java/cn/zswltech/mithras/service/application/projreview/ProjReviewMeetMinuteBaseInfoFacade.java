package cn.zswltech.mithras.service.application.projreview;

import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewMeetMinuteBaseInfoApplicationService;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projreview.meet.*;
import cn.zswltech.mithras.dto.trackEvent.TrackEventListRSP;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.MeetMinuteStatuesEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.VotingResultTypeEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewMeetMinuteBaseInfoService;
import cn.zswltech.mithras.service.service.trackEvent.TrackEventService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
* @description 项目评审会议纪要表
* @author vico
* @date 2025-03-18
*/
@Service
public class ProjReviewMeetMinuteBaseInfoFacade implements ProjReviewMeetMinuteBaseInfoApplicationService {

    @Resource
    private ProjReviewMeetMinuteBaseInfoService projReviewMeetMinuteBaseInfoService;
    @Resource
    private TrackEventService trackEventService;

    @Override
    public R<ProjReviewMeetMinuteBaseInfoDetailRSP> detail(@Valid ProjReviewMeetMinuteBaseInfoDetailREQ req) {
        return R.ok(projReviewMeetMinuteBaseInfoService.detail(req));
    }

    @Override
    public R<Void> modify(ProjReviewMeetMinuteBaseInfoModifyREQ req){
        req.setMeetMinuteStatus(MeetMinuteStatuesEnum.HOLD.name());
        projReviewMeetMinuteBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<Void> submit(@Valid ProjReviewMeetMinuteBaseInfoModifyREQ req) {
        req.setMeetMinuteStatus(MeetMinuteStatuesEnum.SUBMIT.name());
        if (!ObjectUtil.equals(req.getVotingResult(), VotingResultTypeEnum.VETO.name())) {
            projReviewMeetMinuteBaseInfoService.subCheck(req);
        }
        projReviewMeetMinuteBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<ClientInfo>> getRelatedCustomers(@Valid ProjReviewMeetMinuteRelatedCustomersREQ req) {
        return R.ok(projReviewMeetMinuteBaseInfoService.getRelatedCustomers(req));
    }

    @Override
    public R<ProjReviewMeetMinuteCreditDateCheckRSP> creditDateCheck(@Valid ProjReviewMeetMinuteBaseInfoDetailREQ req) {
        //付款填充项目评审ID
        if (ObjectUtil.isNotEmpty(req.getPaymentId())) {
            PaymentBaseInfo paymentBaseInfo = SpringContextHolder.getBean(PaymentBaseInfoService.class).getById(req.getPaymentId());
            if (ObjectUtil.isNotEmpty(paymentBaseInfo)) {
                ContractBaseInfo contractBaseInfo = SpringContextHolder.getBean(ContractBaseInfoService.class).getById(paymentBaseInfo.getContractId());
                if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                    req.setProjReviewId(contractBaseInfo.getProjReviewId());
                }
            }
        }
        ProjReviewMeetMinuteBaseInfoDetailRSP detail = projReviewMeetMinuteBaseInfoService.detail(req);
        ProjReviewMeetMinuteCreditDateCheckRSP creditDateCheckRSP = new ProjReviewMeetMinuteCreditDateCheckRSP();
        creditDateCheckRSP.setEffect(YesOrNoNumberEnum.NO.getCode());
        if (ObjectUtil.isNotEmpty(detail) && ObjectUtil.isNotEmpty(detail.getCreditExpirationDate())) {
            creditDateCheckRSP.setCreditExpirationDate(detail.getCreditExpirationDate());
            if(detail.getCreditExpirationDate().isBefore(LocalDate.now())) {
                creditDateCheckRSP.setEffect(YesOrNoNumberEnum.YES.getCode());
            }
        }
        return R.ok(creditDateCheckRSP);
    }

    @Override
    public R<List<TrackEventListRSP>> trackEventList(@Valid ProjReviewMeetMinuteBaseInfoDetailREQ req) {
        return R.ok(trackEventService.queryListByProjReviewMeetMinuteId(req.getId()));
    }


}