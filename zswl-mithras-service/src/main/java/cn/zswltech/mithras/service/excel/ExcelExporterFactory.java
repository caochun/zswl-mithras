package cn.zswltech.mithras.service.excel;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.excel.exporter.*;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.ContractRentActualExcelModel;
import cn.zswltech.mithras.service.service.bo.ProjPricingCashFlowExporterBO;
import cn.zswltech.mithras.service.service.bo.ProjReviewCashFlowExporterBO;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
public class ExcelExporterFactory {
    public static SimpleExcelExporter<CashFlowExcelModel> getProjReviewRentExcelExporter(ProjectBizType projectBizType) {
        switch (projectBizType) {
            case BL: {
                return SpringUtil.getBean(ProjReviewRentBLExcelExporter.class);
            }
            case ZR: {
                return SpringUtil.getBean(ProjReviewRentZRExcelExporter.class);
            }
            default: {
                return SpringUtil.getBean(ProjReviewRentZLExcelExporter.class);
            }
        }
    }

    public static AbstractCashFlowExcelExporter<ProjReviewCashFlowExporterBO> getProjReviewCashFlowExcelExporter(ProjectBizType projectBizType) {
        switch (projectBizType) {
            case BL: {
                return SpringUtil.getBean(ProjReviewCashFlowBLExcelExporter.class);
            }
            case ZR: {
                return SpringUtil.getBean(ProjReviewCashFlowZRExcelExporter.class);
            }
            default: {
                return SpringUtil.getBean(ProjReviewCashFlowZLExcelExporter.class);
            }
        }
    }


    public static SimpleExcelExporter<CashFlowExcelModel> getProjPricingRentExcelExporter(ProjectBizType projectBizType) {
        switch (projectBizType) {
            case BL: {
                return SpringUtil.getBean(ProjPricingRentBLExcelExporter.class);
            }
            case ZR: {
                return SpringUtil.getBean(ProjPricingRentZRExcelExporter.class);
            }
            default: {
                return SpringUtil.getBean(ProjPricingRentZLExcelExporter.class);
            }
        }
    }

    public static AbstractCashFlowExcelExporter<ProjPricingCashFlowExporterBO> getProjPricingCashFlowExcelExporter(ProjectBizType projectBizType) {
        switch (projectBizType) {
            case BL: {
                return SpringUtil.getBean(ProjPricingCashFlowBLExcelExporter.class);
            }
            case ZR: {
                return SpringUtil.getBean(ProjPricingCashFlowZRExcelExporter.class);
            }
            default: {
                return SpringUtil.getBean(ProjPricingCashFlowZLExcelExporter.class);
            }
        }
    }

    public static SimpleExcelExporter<CashFlowExcelModel> getContractEstimateRentExcelExporter(ProjectBizType projectBizType) {
        switch (projectBizType) {
            case BL: {
                return SpringUtil.getBean(ContractEstimateRentBLExcelExporter.class);
            }
            case ZR: {
                return SpringUtil.getBean(ContractEstimateRentZRExcelExporter.class);
            }
            default: {
                return SpringUtil.getBean(ContractEstimateRentZLExcelExporter.class);
            }
        }
    }

    public static AbstractContractEstimateCashFlowExcelExporter getContractEstimateCashFlowExcelExporter(ProjectBizType projectBizType) {
        switch (projectBizType) {
            case BL: {
                return SpringUtil.getBean(ContractEstimateCashFlowBLExcelExporter.class);
            }
            case ZR: {
                return SpringUtil.getBean(ContractEstimateCashFlowZRExcelExporter.class);
            }
            default: {
                return SpringUtil.getBean(ContractEstimateCashFlowZLExcelExporter.class);
            }
        }
    }

    public static SimpleExcelExporter<ContractRentActualExcelModel> getContractActualRentExcelExporter(ProjectBizType projectBizType) {
        switch (projectBizType) {
            case BL: {
                return SpringUtil.getBean(ContractActualRentBLExcelExporter.class);
            }
            case ZR: {
                return SpringUtil.getBean(ContractActualRentZRExcelExporter.class);
            }
            default: {
                return SpringUtil.getBean(ContractActualRentZLExcelExporter.class);
            }
        }
    }

    public static AbstractContractActualCashFlowExcelExporter getContractActualCashFlowExcelExporter(ProjectBizType projectBizType) {
        switch (projectBizType) {
            case BL: {
                return SpringUtil.getBean(ContractActualCashFlowBLExcelExporter.class);
            }
            case ZR: {
                return SpringUtil.getBean(ContractActualCashFlowZRExcelExporter.class);
            }
            default: {
                return SpringUtil.getBean(ContractActualCashFlowZLExcelExporter.class);
            }
        }
    }
}
