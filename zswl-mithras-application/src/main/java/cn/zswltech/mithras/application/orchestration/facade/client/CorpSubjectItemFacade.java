package cn.zswltech.mithras.application.orchestration.facade.client;
import cn.zswltech.mithras.customer.enums.FinanceReportCashFlowItemCode;
import cn.zswltech.mithras.customer.enums.FinanceReportProfitItemCode;
import cn.zswltech.mithras.customer.enums.FinanceReportCapitalBalanceItemCode;
import cn.zswltech.mithras.customer.enums.SubjectReportType;
import cn.zswltech.mithras.customer.enums.SubjectItemType;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.customer.application.client.CorpSubjectItemApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListREQ;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemRemoveREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.customer.application.client.auth.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.enums.*;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpSubjectItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.customer.enums.SubjectItemType.BIZ_INDEX;
import static java.math.RoundingMode.HALF_UP;
import org.springframework.stereotype.Service;

/**
 * @author junke
 */
@Service
public class CorpSubjectItemFacade implements CorpSubjectItemApplicationService {
    @Resource
    private CorpSubjectItemService subjectItemService;
    @Autowired
    private HttpServletResponse response;

    @Override
    @DataAuthCheck(paramIndex = 1, checkerClass = ClientAddSubAuthCheckerNew.class, paramType = DataAuthCheck.ParamType.DIRECT, businessModule = "CLIENT")
    public R<Void> importSubjectItems(MultipartFile excelFile, Long clientId) {
        subjectItemService.importSubjectItemByExcel(excelFile, clientId);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<List<CorpSubjectItemListRSP>> list(CorpSubjectItemListREQ req) {
        if (!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), req.getSourceScene())) {
            return R.ok(Collections.emptyList());
        }
        if (SubjectReportType.ALL.name().equals(req.getReportType())) {
            req.setReportType(null);
        }
        if (isNull(req.getDecimalCount())) {
            req.setDecimalCount(0);
        }
        if (isNull(req.getUnit()) || ObjectUtil.equal(req.getSubjectType(), BIZ_INDEX.name())) {
            req.setUnit(1L);
        }
        List<CorpSubjectItemListRSP> list = subjectItemService.list(req);
        StringBuilder format = new StringBuilder("#,##0");
        if (req.getDecimalCount() > 0) {
            format.append(".");
            for (int i = 0; i < req.getDecimalCount(); i++) {
                format.append("0");
            }
        }
        Set<String> allCode = new HashSet<>();
        Map<String, String> nameMap = new HashMap<>();
        for (CorpSubjectItemListRSP rsp : list) {
            for (CorpSubjectItemListRSP.SubjectItem item : rsp.getItemList()) {
                item.setOrder(this.ensureOrderNum(rsp.getSubjectType(), item.getSubjectCode()));
                item.setSubjectValueStr(strValue(item.getSubjectValue(), String.valueOf(req.getUnit()), req.getDecimalCount(), format.toString()));
                item.setSubjectOverYearStr(strValue(item.getSubjectOverYear(), "0.01", req.getDecimalCount(), format.toString()));
                item.setSubjectPercentStr(strValue(item.getSubjectPercent(), "0.01", req.getDecimalCount(), format.toString()));
                allCode.add(item.getSubjectCode());
                if (StrUtil.isNotBlank(item.getSubjectName())) {
                    nameMap.put(item.getSubjectCode(), item.getSubjectName());
                }
            }
        }
        //补全所有code，便于前端展示
        for (CorpSubjectItemListRSP rsp : list) {
            Set<String> set = rsp.getItemList().stream().map(CorpSubjectItemListRSP.SubjectItem::getSubjectCode).collect(Collectors.toSet());
            for (String code : allCode) {
                if (!set.contains(code)) {
                    CorpSubjectItemListRSP.SubjectItem item = new CorpSubjectItemListRSP.SubjectItem();
                    item.setOrder(this.ensureOrderNum(rsp.getSubjectType(), item.getSubjectCode()));
                    item.setSubjectCode(code);
                    item.setSubjectName(nameMap.get(code));
                    rsp.getItemList().add(item);
                }
            }
            rsp.getItemList().sort((o1, o2) -> {
//                if (o1.getId() == null) {
//                    return -1;
//                }
//                if (o2.getId() == null) {
//                    return 1;
//                }
//                return o1.getId().compareTo(o2.getId());
                if (o1.getOrder() == null) {
                    return -1;
                }
                if (o2.getOrder() == null) {
                    return 1;
                }
                return o1.getOrder().compareTo(o2.getOrder());
            });
        }
        return R.ok(list);
    }

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "CLIENT")
    public R<Void> remove(CorpSubjectItemRemoveREQ req) {
        subjectItemService.remove(req);
        return R.ok();
    }


    private String strValue(Long subjectValue, String unit, int decimalCount, String format) {
        if (isNull(subjectValue)) {
            return null;
        }
        BigDecimal value = new BigDecimal(subjectValue);
        value = value.divide(BigDecimal.valueOf(10000), 4, HALF_UP);//转成元
        value = value.divide(new BigDecimal(unit), decimalCount, HALF_UP);//转单位
        StringBuilder strValue = new StringBuilder();
        if (subjectValue > 0 && value.compareTo(BigDecimal.ZERO) == 0) {
            if (decimalCount == 0) {
                strValue.append("<1");
            } else {
                strValue.append("<0.");
                for (int i = 0; i < decimalCount; i++) {
                    strValue.append("0");
                }
                strValue.replace(strValue.length() - 1, strValue.length(), "1");
            }
        } else {
            strValue = new StringBuilder(NumberUtil.decimalFormat(format, value));
        }
        return strValue.toString();
    }

    @Override
    public void downloadTemplate(Integer type) throws Exception {
        String filename = null;
        if (type == 1) {
            filename = "财报导入模板_企业法人.xlsx";

        } else if (type == 2) {
            filename = "财报导入模板_事业单位.xlsx";
        }
        String downloadFileName = URLEncoder.encode(filename, "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        subjectItemService.downloadTemplate(response.getOutputStream(), filename);

    }

    private int ensureOrderNum(String subjectType, String subjectCode) {
        if (Objects.equals(subjectType, SubjectItemType.CAPITAL_BALANCE.name())) {
            return FinanceReportCapitalBalanceItemCode.getOrderNumByName(subjectCode);
        }
        if (Objects.equals(subjectType, SubjectItemType.PROFIT.name())) {
            return FinanceReportProfitItemCode.getOrderNumByName(subjectCode);
        }
        if (Objects.equals(subjectType, SubjectItemType.CASH_FLOW.name())) {
            return FinanceReportCashFlowItemCode.getOrderNumByName(subjectCode);
        }
        return Integer.MAX_VALUE;
    }

}
