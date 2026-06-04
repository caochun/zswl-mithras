package cn.zswltech.mithras.service.excel.model;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/8/16
 * @description 合同相关实体物导入Excel数据模型（租赁物、抵押物共用）
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractEntityPledgeItemExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "序号")
    private Integer sequence;

    @SimpleExcelHeader(headerName = "种类")
    private String category;

    @SimpleExcelHeader(headerName = "识别号类型")
    private String uniqueIdentifyCodeType;

    @SimpleExcelHeader(headerName = "唯一识别号")
    private String uniqueIdentifyCode;

    @SimpleExcelHeader(headerName = "名称")
    private String name;

    @SimpleExcelHeader(headerName = "供应商")
    private String supplier;

    @SimpleExcelHeader(headerName = "数量")
    private String quantity;

    @SimpleExcelHeader(headerName = "计量单位")
    private String unit;

    @SimpleExcelHeader(headerName = "购置日期")
    private String purchaseDate;

    @SimpleExcelHeader(headerName = "账面原值（元）")
    private BigDecimal originalBookValue;

    @SimpleExcelHeader(headerName = "账面净值（元）")
    private BigDecimal originalBookNetValue;

    @SimpleExcelHeader(headerName = "评估原值（元）")
    private BigDecimal assessedValue;

    @SimpleExcelHeader(headerName = "评估净值（元）")
    private BigDecimal assessedNetValue;

    @SimpleExcelHeader(headerName = "发票号")
    private String invoiceCode;

    @SimpleExcelHeader(headerName = "存放地点")
    private String storagePlace;

    public void checkLeaseItem() {
        String prefix = "租赁物清单";
        Assert.notNull(assessedNetValue, () -> MithrasException.newException(prefix + "-评估净值不能为空"));
    }

    public void checkMortgageItem() {
        String prefix = "抵押物清单";
        this.checkBase(prefix);
        Assert.notBlank(uniqueIdentifyCodeType, () -> MithrasException.newException(prefix + "-识别号类型不能为空"));
        Assert.notBlank(uniqueIdentifyCode, () -> MithrasException.newException(prefix + "-唯一识别号不能为空"));
        Assert.notNull(assessedNetValue, () -> MithrasException.newException(prefix + "-评估净值不能为空"));
    }

    public void checkPledgeItem() {
        String prefix = "质押物清单";
        this.checkBase(prefix);
        Assert.notNull(assessedValue, () -> MithrasException.newException(prefix + "-评估价值不能为空"));
    }

    public void checkBase(String prefix) {
        Assert.notNull(sequence, () -> MithrasException.newException(prefix + "-序号不能为空"));
        Assert.notNull(category, () -> MithrasException.newException(prefix + "-种类不能为空"));
        Assert.isTrue(CategoryEnum.existByDisplay(category.trim()), () -> MithrasException.newException(prefix + "-种类必须符合下拉选项中的值"));
    }

    @AllArgsConstructor
    @Getter
    public enum CategoryEnum {
        /**
         * 模版中种类的枚举
         */
        CASH("现金及其等价物"),
        BILL("票据"),
        POLICY("保单"),
        BOND("债券"),
        EQUITY("股权"),
        FUND("基金"),
        PRECIOUS_METAL("贵金属"),
        ACCOUNTS("应收账款"),
        CURRENT_ASSETS("流动资产"),
        INTANGIBLE_ASSETS("无形资产"),
        AGRICULTURAL_SUBSTANCES("涉农质物"),
        OTHER("其他");

        private final String display;

        public static boolean existByDisplay(String s) {
            for (CategoryEnum item : values()) {
                if (Objects.equals(item.display, s)) {
                    return true;
                }
            }
            return false;
        }
    }
}
