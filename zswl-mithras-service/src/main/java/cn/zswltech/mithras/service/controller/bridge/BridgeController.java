package cn.zswltech.mithras.service.controller.bridge;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.app.AppApi;
import cn.zswltech.mithras.api.bridge.BridgeApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.*;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListRSP;
import cn.zswltech.mithras.dto.app.*;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.dto.collection.*;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoListREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoListRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualListRSP;
import cn.zswltech.mithras.dto.contractcp.ContractInfoReceiptRSP;
import cn.zswltech.mithras.dto.contractcp.ContractcpContractReceiptDetailREQ;
import cn.zswltech.mithras.dto.file.AppFileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessListREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.utils.CashFlowGenerationIrrREQ;
import cn.zswltech.mithras.service.controller.contract.ContractRentController;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.afterlease.domain.enums.RentCollectionIndexFilterConditionType;
import cn.zswltech.mithras.payment.domain.enums.app.AppPaymentStatus;
import cn.zswltech.mithras.service.enums.app.AppProjStageStatus;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.PayType;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.impl.RentCollectionIndexServiceImpl;
import cn.zswltech.mithras.service.service.app.AppService;
import cn.zswltech.mithras.service.service.bo.CashFlowBO;
import cn.zswltech.mithras.service.service.bo.CashFlowCalculateBO;
import cn.zswltech.mithras.service.service.bo.CashFlowIRRBO;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.lib.contractcp.ContractCollectionPaymentService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishLeasePriceService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.util.DashboardHelpUtil;
import cn.zswltech.mithras.service.util.HttpClientUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.bean.BeanUtil.copyToList;
import static cn.zswltech.mithras.contract.enums.contract.RepayRateEnum.of;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static cn.zswltech.mithras.service.util.FinancialUtil.calcCashFlow;
import static cn.zswltech.mithras.service.util.FinancialUtil.calculateIRR;

/**
 * @author luyi
 */
@Slf4j
@RestController
public class BridgeController implements BridgeApi {

    @Value("${bridge.service.authUrl}")
    private String authUrl;

    @Value("${bridge.service.loginUrl}")
    private String loginUrl;

    private static String tdToken;
    private static String csrfToken;
    private static Long lastTime;


    @Override
    public R<SystemUserAuthCodeRSP> getAuthCode(SystemUserAuthCodeREQ req) {
        Map<String, Object> param = new HashMap<>();
        param.put("account", req.getAccount());
        param.put("password", req.getPassword());
        String result = HttpClientUtil.connectPostHttps(authUrl, param);
        if (StringUtils.isBlank(result)) {
            throw new MithrasException("authcode返回为空");
        }
        SystemUserAuthCodeRSP obj;
        ObjectMapper mapper = new ObjectMapper();
        try {
            obj = mapper.readValue(result, SystemUserAuthCodeRSP.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return R.ok(obj);
    }

    @Override
    public R<SystemUserLoginRSP> login(SystemUserLoginREQ req) {
        if (StringUtils.isBlank(req.getTempRandom())) {
            throw new MithrasException("authCode为空");
        }
        SystemUserLoginRSP rsp = new SystemUserLoginRSP();
        if (StringUtils.isNotBlank(tdToken) && StringUtils.isNotBlank(csrfToken) && lastTime != null) {
            long currentTimeMillis = System.currentTimeMillis();
            long oneHourInMillis = TimeUnit.HOURS.toMillis(1);
            if (currentTimeMillis - lastTime < oneHourInMillis) {
                rsp.setTdToken(tdToken);
                rsp.setCsrfToken(csrfToken);
                return R.ok(rsp);
            }
        }
        Map<String, Object> param = new HashMap<>();
        param.put("account", req.getAccount());
        param.put("password", req.getPassword());
        param.put("tempRandom", req.getTempRandom());
        String result = HttpClientUtil.connectPostHttps(loginUrl, param);
        if (StringUtils.isBlank(result)) {
            throw new MithrasException("userLogin返回为空");
        }
        SystemUserLoginObj obj;
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            obj = mapper.readValue(result, SystemUserLoginObj.class);
            obj = JSONUtil.toBean(result, SystemUserLoginObj.class);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        if (obj != null && obj.getData() != null) {
            tdToken = obj.getData().getTdToken();
            csrfToken = obj.getData().getCsrfToken();
            lastTime = System.currentTimeMillis();
            rsp.setCsrfToken(obj.getData().getCsrfToken());
            rsp.setTdToken(obj.getData().getTdToken());
        }
        return R.ok(rsp);
    }
}
