package cn.zswltech.mithras.contract.overdue.application.service;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.dto.persistence.OcContractListDto;
import cn.zswltech.mithras.contract.overdue.application.collection.CollectionContractQueryService;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class CollectionContractExportService {
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CollectionContractQueryService collectionContractQueryService;
    @Autowired
    private HttpServletResponse response;

    public void contractListExport(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        try {
            List<OcContractListDto> originalList = collectionContractQueryService.ocContractList(clientId);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ExcelWriter w = ExcelUtil.getWriter(true);
            w.writeHeadRow(ListUtil.of("合同编号", "项目名称", "业务类型", "合同金额",
                    "当前逾期天数", "逾期金额", "逾期罚息", "剩余本金", "剩余保证金", "风险敞口", "合同状态", "项目主办", "业务部门"));
            for (OcContractListDto c : originalList) {
                w.writeRow(ListUtil.of(
                        c.getContractCode(),
                        c.getProjName(),
                        ProjectBizType.valueOf(c.getBizType()).display,
                        c.getContractAmount() == null ? 0 : BigDecimal.valueOf(c.getContractAmount()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getOverdueDays(),
                        c.getOverdueRent() == null ? 0 : BigDecimal.valueOf(c.getOverdueRent()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getLateCharge() == null ? 0 : BigDecimal.valueOf(c.getLateCharge()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getRemainPrincipal() == null ? 0 : BigDecimal.valueOf(c.getRemainPrincipal()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getRemainDeposit() == null ? 0 : BigDecimal.valueOf(c.getRemainDeposit()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getRiskExposure() == null ? 0 : BigDecimal.valueOf(c.getRiskExposure()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        ContractStatus.valueOf(c.getContractStatus()).display,
                        c.getProjSponsorName(),
                        c.getBizDeptName()
                ));
            }
            w.flush(bos, true);
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(
                    client.getClientName() + "_合同信息" + GlobalConstants.OFFICE_EXCEL_SUFFIX,
                    StandardCharsets.UTF_8.name()));
            outputStream.write(bos.toByteArray());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出合同发生未知异常", e);
            throw new MithrasException("导出合同发生未知异常");
        }
    }
}
