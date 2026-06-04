package cn.zswltech.mithras.service.job;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.creditreport.enums.CreditSearchStatusEnum;
import cn.zswltech.mithras.creditreport.mapper.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportClientItem;
import cn.zswltech.mithras.creditreport.service.CreditReportApiService;
import cn.zswltech.mithras.creditreport.service.CreditReportBaseInfoService;
import cn.zswltech.mithras.creditreport.service.CreditReportClientItemService;
import cn.zswltech.mithras.creditreport.service.resp.CreditReportObtainResultPDFResp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @date 2024/10/14
 * @description 征信报告解析查询接口
 */
@Component
@Slf4j
public class CreditReportJob {

    @Resource
    private CreditReportClientItemService creditReportClientItemService;
    @Resource
    private CreditReportBaseInfoService creditReportBaseInfoService;
    @Resource
    private CreditReportApiService creditReportApiService;


    /**
     * 执行查询任务，查询征信结果
     */
    @XxlJob("creditReportQueryResult")
    @Transactional(rollbackFor = Throwable.class)
    public void creditReportQueryResult() {
        try {
            List<CreditReportClientItem> clientItems = creditReportClientItemService.list(Wrappers.<CreditReportClientItem>lambdaQuery()
                    .in(CreditReportClientItem::getSelectStatus, CreditSearchStatusEnum.SEARCHING.getName(), CreditSearchStatusEnum.FAIL.name()));
            if (ObjectUtil.isEmpty(clientItems)) {
                return;
            }
            List<Long> clientItemIds = new ArrayList<>();
            List<Long> errorItemIds = new ArrayList<>();
            clientItems.forEach(clientItem -> {
                //解析json
                XJCreditReportJsonDTO xjCreditReportJsonDTO = creditReportApiService.resultJSON(clientItem.getId());
                boolean saveJson = creditReportBaseInfoService.importByXJCreditReportJsonDTO(xjCreditReportJsonDTO, clientItem.getCreditReportBaseInfoId(), clientItem.getId());
                //保存pdf
                CreditReportObtainResultPDFResp creditReportObtainResultPDFResp = creditReportApiService.resultPDF(clientItem.getId());
                boolean savePDf = creditReportBaseInfoService.importByXJCreditReportObtainResultPDFResp(creditReportObtainResultPDFResp, clientItem.getId());
                if (saveJson && savePDf) {
                    clientItemIds.add(clientItem.getId());
                } else {
                    errorItemIds.add(clientItem.getId());
                }
            });
            //回调状态
            creditReportBaseInfoService.modifyClientItemStatus(clientItemIds, CreditSearchStatusEnum.SUCCESS.name());
            creditReportBaseInfoService.modifyClientItemStatus(errorItemIds, CreditSearchStatusEnum.FAIL.name());
        } catch (Exception e) {
            log.error("查询征信结果任务执行异常", e);
        }
    }


}
