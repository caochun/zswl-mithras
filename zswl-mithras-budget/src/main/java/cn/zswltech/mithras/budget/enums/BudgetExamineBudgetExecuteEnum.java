package cn.zswltech.mithras.budget.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//预算执行情况表
@AllArgsConstructor
public enum BudgetExamineBudgetExecuteEnum implements PullDown {
	NEW_INVESTMENT_AMOUNT("本年新增投放额", "新增投放额", 10, null, 2),
	INCLUDING_OTHER_INDUSTRY_CATEGORY("其中：产业类", "其中：其他产业类", 20, null, 3),
	PUBLIC_UTILITIES_CATEGORY("公用事业类", "公用事业类", 30, null, 3),
	STATE_OWNED_INDUSTRY_CATEGORY("", "国有产业类", 40, null, 3),
	PEOPLE_LIVELIHOOD_CONSUMPTION_CATEGORY("", "民生消费类", 50, null, 3),
	OPERATING_REVENUE("营业收入", "营业收入", 60, null, 2),  // 若需与前序"营业收入"区分可补充场景词（如NEW_OPERATING_REVENUE）
	INCLUDING_CONSULTING_SERVICE_INCOME("其中：手续费收入", "其中：咨询服务费收入", 70, "主营业务收入_租赁收入_外部_售后回租收入_服务费收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_融资租赁收入_服务费收入@本年累计@借方金额", 3),
	INTEREST_INCOME("利息收入", "利息收入", 80, "主营业务收入_租赁收入_内部_售后回租收入_利息收入@本年累计@贷方金额+主营业务收入_租赁收入_内部_融资租赁收入_利息收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_售后回租收入_利息收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_融资租赁收入_利息收入@本年累计@贷方金额", 3),  // 若需与前序"利息收入"区分可补充场景词（如NEW_INTEREST_INCOME）
	OPERATING_EXPENSES("经营费用", "经营费用", 90, "管理费用+销售费用+财务费用",2),
	INCLUDING_TRAVEL_EXPENSES("其中：差旅费", "其中：差旅费", 100, "管理费用_差旅费@本年累计@借方金额", 3),
	BUSINESS_ENTERTAINMENT_EXPENSES("业务招待费", "业务招待费", 110, "管理费用_业务招待费@本年累计@借方金额+销售费用_业务招待费@本年累计@借方金额", 3),
	CITY_TRANSPORTATION_EXPENSES("市内交通费", "市内交通费", 115, "管理费用_市内交通费@本年累计@借方金额", 3),
	ASSESSMENT_PROFIT("考核利润", "考核利润", 120, null, 2),
	ASSESSMENT_PROFIT_BEFORE_ALLOWANCE("考核利润（拨备前）", "考核利润（拨备前）", 130, null, 2),
	END_OF_PERIOD_BUSINESS_SCALE("期末业务规模", "期末业务规模", 140, null, 2),
	EXPENSE_LEVEL_RATE("费用水平率", "费用水平率（%）", 150, null, 2),
	CUMULATIVE_PROJECT_APPROVAL_COUNT("累计立项个数", "累计立项个数", 160, null, 2),
	EXPENSE_EFFICIENCY("费用效率", "费用效率", 170, null, 2),
	//REMARKS("备注", 180, null, 2),
	; //代办

	private final String excelName;

	private final String display;

	private final int sort;

	//指标名称，用于获取辅助核算值
	private final String riskName;

	private final int level;

	private static Map<String, BudgetExamineBudgetExecuteEnum> map;


	static {
		map = Stream.of(BudgetExamineBudgetExecuteEnum.values()).collect(Collectors.toMap(BudgetExamineBudgetExecuteEnum::name, e -> e, (k1, k2)->k1));
	}

	public static BudgetExamineBudgetExecuteEnum findByExcelName(String excelName) {
		for (BudgetExamineBudgetExecuteEnum item : values()) {
			if (Objects.equals(item.excelName, excelName)) {
				return item;
			}
		}
		return null;
	}

	public static BudgetExamineBudgetExecuteEnum of(String name) {
		return map.get(name);
	}

	public int getSort() {
		return sort;
	}

	public String getRiskName() {
		return riskName;
	}

//	BudgetExamineBudgetExecuteEnum(String display, int sort, String riskName, int level) {
//		this.display = display;
//		this.sort = sort;
//		this.riskName = riskName;
//		this.level = level;
//	}


	@Override
	public String display() {
		return display;
	}

	@Override
	public int level() {
		return level;
	}

}