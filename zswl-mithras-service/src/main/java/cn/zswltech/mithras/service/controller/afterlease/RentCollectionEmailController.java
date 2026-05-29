package cn.zswltech.mithras.service.controller.afterlease;
import cn.zswltech.mithras.common.constant.ResultMsg;

import cn.zswltech.mithras.api.afterlease.RentCollectionEmailApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailDetailREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailDetailRSP;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailGenREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailSendREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.service.genhtml.PaymentNoticeHtmlRender;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.impl.RentCollectionEmailServiceImpl;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static cn.zswltech.mithras.common.constant.ResultMsg.CONCURRENT_OPERATION;

/**
 * 租金催收发送邮件
 *
 * @author wangchuanhao
 * @date 2022/11/18 2:30 PM
 */
@RestController
public class RentCollectionEmailController implements RentCollectionEmailApi {

    @Resource
    private RentCollectionEmailServiceImpl rentCollectionEmailService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private PaymentNoticeHtmlRender paymentNoticeHtmlRender;

    @Override
    @DataAuthCheck(keyFieldName = "collectionId", checkerClass = CommonModifyMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.COLLECTION)
    public R<Void> sendEmail(RentCollectionEmailSendREQ req) {
        String lockKey = CacheEnum.RENT_COLLECTION_SEND_EMAIL_LOCK.buildKey(req.getCollectionId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            rentCollectionEmailService.sendEmail(req);
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<Long> genEmail(RentCollectionEmailGenREQ req) {
        return R.ok(rentCollectionEmailService.genEmail(req.getCollectionId(), req.getComment(), req.getBankId()));
    }

    @Override
    public R<String> genEmailHtml(RentCollectionEmailGenREQ req) {
        return R.ok(rentCollectionEmailService.genEmailHtml(req.getCollectionId(), req.getComment(), req.getBankId()));
    }

    @Override
    public R<RentCollectionEmailDetailRSP> detail(RentCollectionEmailDetailREQ req) {
        return R.ok(rentCollectionEmailService.detail(req.getCollectionId()));
    }

    @Override
    public byte[] htmlPreview(String htmlKey) {
        return rentCollectionEmailService.htmlPreview(htmlKey);
    }

    @Override
    @SneakyThrows
    public R<Void> down(Long collectionId, String accountName, String accountBank, String accountNumber) {
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租金支付通知书.docx", StandardCharsets.UTF_8.name()));
        paymentNoticeHtmlRender.downDocx(httpServletResponse.getOutputStream(), collectionId, accountName, accountBank, accountNumber);
        return R.ok();
    }

}
