package cn.zswltech.mithras.others.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.controller.datacompare.EditdataCompareController;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.MyStyleUtil;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/13 15:50
 */
public class CompareTest extends ApplicationTest{
    @Resource
    EditdataCompareController editdataCompareController;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private Id2NameService id2NameService;

    @Test
    public void ftpQuarterlyMonthPricingCompareTest(){
        FtpGuidanceIdReq req = new FtpGuidanceIdReq();
        req.setId(128L);
        R<List<Map<String, DiffValue>>> listR = editdataCompareController.ftpQuarterlyMonthPricingCompare(req);
        System.out.println();
    }

    @Test
    public void test() {
        LocalDate deadline = LocalDate.of(2022, 12, 31);
        List<TestExcelModel> testExcelModelList = new LinkedList<>();
        // 找到所有起租合同
        List<ContractBaseInfo> all = contractBaseInfoService.listAllStartRent();
        for (ContractBaseInfo contractBaseInfo : all) {
            // 找到该合同下的收款信息
            List<CollectionBaseInfo> rentList = collectionBaseInfoService.listBy(contractBaseInfo.getId(), CashFlowItemEnum.RENT);
            if (CollectionUtil.isEmpty(rentList)) {
                continue;
            }
            TestExcelModel testExcelModel = new TestExcelModel();
            long planTotal = 0L;
            long actualTotal = 0L;
            for (CollectionBaseInfo collectionBaseInfo : rentList) {
                if (Objects.isNull(collectionBaseInfo.getPrincipal())) {
                    collectionBaseInfo.setPrincipal(0L);
                }
                if (collectionBaseInfo.getPlanCollectionDate().isAfter(deadline)) {
                    planTotal = planTotal + collectionBaseInfo.getPrincipal();
                    actualTotal = actualTotal + collectionBaseInfo.getPrincipal();
                } else {
                    if (!Objects.equals(collectionBaseInfo.getWriteOffStatus(), CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())) {
                        actualTotal = actualTotal + collectionBaseInfo.getPrincipal();
                    }
                }
            }
            testExcelModel.setContractId(contractBaseInfo.getId());
            testExcelModel.setContractCode(contractBaseInfo.getContractCode());
            testExcelModel.setSponsorUserName(id2NameService.sysUserId2NameSingle(contractBaseInfo.getProjSponsorUserId()));
            testExcelModel.setActualStartData(contractBaseInfo.getActualLeaseDate());
            testExcelModel.setPlanRemainingPrinciple(BigDecimal.valueOf(planTotal).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            testExcelModel.setActualRemainingPrinciple(BigDecimal.valueOf(actualTotal).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            testExcelModelList.add(testExcelModel);
        }
        new TestExporter().exportExcel(testExcelModelList, FileUtil.getOutputStream("/Users/mockorz/result.xlsx"));
    }

    public static class TestExporter extends AbstractSimpleExcelExporter<TestExcelModel> {

        @Override
        protected LinkedHashMap<String, String> getHeaderAliasMap() {
            LinkedHashMap<String, String> header = new LinkedHashMap<>();
            header.put("contractCode", "合同编号");
            header.put("sponsorUserName", "项目主办");
            header.put("actualStartData", "实际起租日");
            header.put("planRemainingPrinciple", "应剩本金");
            header.put("actualRemainingPrinciple", "实剩本金");
            return header;
        }

        @Override
        protected Map<Integer, CellStyle> getColumnStyleMap(Workbook workbook) {
            Map<Integer, CellStyle> styleMap = new HashMap<>();
            styleMap.put(1, MyStyleUtil.createMyDateCellStyle(workbook));
            styleMap.put(2, MyStyleUtil.createMyMoneyCellStyle(workbook));
            styleMap.put(3, MyStyleUtil.createMyMoneyCellStyle(workbook));
            return styleMap;
        }

        @Override
        protected void customStrategy(Workbook workbook) {

        }

        @Override
        protected Class<TestExcelModel> modelClz() {
            return TestExcelModel.class;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TestExcelModel extends ExcelModel {
        private Long contractId;
        private String contractCode;
        private String sponsorUserName;
        private LocalDate actualStartData;
        private BigDecimal planRemainingPrinciple;
        private BigDecimal actualRemainingPrinciple;
    }
}
