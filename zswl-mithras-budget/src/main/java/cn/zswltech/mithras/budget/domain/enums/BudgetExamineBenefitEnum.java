package cn.zswltech.mithras.budget.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public enum BudgetExamineBenefitEnum implements PullDown {
	OPERATING_REVENUE("营业收入", "营业收入", 10, null, 2, 0, false),
	FINANCING_LEASE_INCOME("其中：融资租赁收入", "融资租赁收入", 20, "主营业务收入_租赁收入_内部_售后回租收入_利息收入@本年累计@贷方金额+主营业务收入_租赁收入_内部_融资租赁收入_利息收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_售后回租收入_利息收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_售后回租收入_服务费收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_融资租赁收入_利息收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_融资租赁收入_服务费收入@本年累计@贷方金额", 3, 0, true),
	OPERATING_LEASE_INCOME("经营租赁收入", "经营租赁收入", 30, "主营业务收入_租赁收入_内部_经营租赁收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_经营租赁收入@本年累计@贷方金额", 3, 0, true),
	OTHER_INCOME("", "其它收入", 35, "主营业务收入_其他收入_内部@本年累计@贷方金额+主营业务收入_其他收入_外部@本年累计@贷方金额",3, 0, true),
	OPERATING_COST("营业成本", "营业成本", 40, "主营业务成本_其他成本@本年累计@借方金额+主营业务成本_租赁成本_经营租赁成本_保险费@本年累计@借方金额+主营业务成本_租赁成本_经营租赁成本_其他@本年累计@借方金额+主营业务成本_租赁成本_经营租赁成本_折旧费@本年累计@借方金额+主营业务成本_租赁成本_融资性售后回租成本@本年累计@借方金额+主营业务成本_租赁成本_融资租赁成本@本年累计@借方金额", 2, 0, true),
	PRICE_DIFFERENCE("差价", "差价", 50, null,2, 0, false),
	DEDUCTION_TAX_AND_SURCHARGE("减：税金及附加", "减：税金及附加", 60, "税金及附加_印花税@本年累计@借方金额+税金及附加_地方教育费附加@本年累计@借方金额+税金及附加_城市维护建设税@本年累计@借方金额+税金及附加_教育费附加@本年累计@借方金额+税金及附加_车船使用税@本年累计@借方金额", 2, 0, true),
	DEDUCTION_SELLING_EXPENSES("减：销售费用", "减：销售费用", 70, "销售费用_员工费用_企业年金@本年累计@借方金额+销售费用_员工费用_住房公积金@本年累计@借方金额+销售费用_员工费用_保险费_企业年金（补充养老险）@本年累计@借方金额+销售费用_员工费用_保险费_养老保险@本年累计@借方金额+销售费用_员工费用_保险费_医疗保险@本年累计@借方金额+销售费用_员工费用_保险费_失业保险@本年累计@借方金额+销售费用_员工费用_保险费_工伤保险@本年累计@借方金额+销售费用_员工费用_保险费_意外保险及其他商业险@本年累计@借方金额+销售费用_员工费用_保险费_残疾人保障金@本年累计@借方金额+销售费用_员工费用_劳务费@本年累计@借方金额+销售费用_员工费用_工会经费@本年累计@借方金额+销售费用_员工费用_工资@本年累计@借方金额+销售费用_员工费用_残疾人保障金@本年累计@借方金额+销售费用_员工费用_职工教育经费@本年累计@借方金额+销售费用_员工费用_职工福利@本年累计@借方金额+销售费用_广告宣传费@本年累计@借方金额", 2, 0, true),
	DEDUCTION_ADMINISTRATIVE_EXPENSES("减：管理费用", "减：管理费用", 80, "管理费用_业务招待费@本年累计@借方金额+管理费用_中介机构费_专家咨询费@本年累计@借方金额+管理费用_中介机构费_会计师费@本年累计@借方金额+管理费用_中介机构费_其他中介费用@本年累计@借方金额+管理费用_中介机构费_律师费@本年累计@借方金额+管理费用_中介机构费_税务师费@本年累计@借方金额+管理费用_中介机构费_评估费@本年累计@借方金额+管理费用_人才工作专项费用_其他人才专项奖励费用_其他人才补助@本年累计@借方金额+管理费用_会员费@本年累计@借方金额+管理费用_" +
			"会议费@本年累计@借方金额+管理费用_保全费@本年累计@借方金额+管理费用_保险费@本年累计@借方金额+管理费用_信息化费@本年累计@借方金额+管理费用_党团活动费@本年累计@借方金额+管理费用_办公场所使用费_其他@本年累计@借方金额+管理费用_办公场所使用费_办公区域保洁费@本年累计@借方金额+管理费用_办公场所使用费_办公区域绿化费@本年累计@借方金额+管理费用_办公场所使用费_办公场所租赁费@本年累计@借方金额+管理费用_办公场所使用费_水电费@本年累计@借方金额+管理费用_办公场所使用费_物业管理费@本年累计@借方金额+管理费用_办公场所使用费_" +
			"装修费@本年累计@借方金额+管理费用_办公费_其他@本年累计@借方金额+管理费用_办公费_办公用品及耗材@本年累计@借方金额+管理费用_办公费_办公设施维修@本年累计@借方金额+管理费用_办公费_印刷费@本年累计@借方金额+管理费用_办公费_报刊杂志及技术资料@本年累计@借方金额+管理费用_劳动保护费_其他劳动保护费用@本年累计@借方金额+管理费用_劳动保护费_工作服@本年累计@借方金额+管理费用_员工费用_企业年金@本年累计@借方金额+管理费用_员工费用_住房公积金@本年累计@借方金额+管理费用_员工费用_保险费_" +
			"企业年金（补充养老险）@本年累计@借方金额+管理费用_员工费用_保险费_养老保险@本年累计@借方金额+管理费用_员工费用_保险费_医疗保险@本年累计@借方金额+管理费用_员工费用_保险费_失业保险@本年累计@借方金额+管理费用_员工费用_保险费_工伤保险@本年累计@借方金额+管理费用_员工费用_保险费_意外保险及其他商业险@本年累计@借方金额+管理费用_员工费用_保险费_残疾人保障金@本年累计@借方金额+管理费用_员工费用_劳务费@本年累计@借方金额+管理费用_员工费用_工会经费@本年累计@借方金额+管理费用_员工费用_工资@本年累计@借方金额+管理费用_员工费用_职工教育经费@本年累计@借方金额+管理费用_员工费用_职工福利@本年累计@借方金额+管理费用_员工费用_辞退福利@本年累计@借方金额+管理费用_安全费用@本年累计@借方金额+管理费用_差旅费@本年累计@借方金额+管理费用_市内交通费@本年累计@借方金额+管理费用_折旧费@本年累计@借方金额+管理费用_无形资产摊销_软件使用权@本年累计@借方金额+管理费用_诉讼费@本年累计@借方金额+管理费用_车辆使用费_停车及过路过桥费@本年累计@借方金额+管理费用_车辆使用费_其他@本年累计@借方金额+管理费用_车辆使用费_汽车规费@本年累计@借方金额+管理费用_车辆使用费_油料费@本年累计@借方金额+管理费用_车辆使用费_维修费@本年累计@借方金额+管理费用_车辆使用费_车辆租赁费@本年累计@借方金额+管理费用_邮电费@本年累计@借方金额", 2, 0, true),
	DEDUCTION_FINANCIAL_EXPENSES("减：财务费用", "减：财务费用", 90, null, 2, 0, false),
	INTEREST_INCOME("其中： 利息收入", "利息收入", 100, "财务费用_利息收入_定期_外部@本年累计@借方金额+财务费用_利息收入_活期_内部@本年累计@借方金额+财务费用_利息收入_活期_外部@本年累计@借方金额+财务费用_利息收入_定期_外部@本年累计@借方金额", 3, 0, true),
	INTEREST_EXPENSE("利息支出", "利息支出", 110, "财务费用_利息支出_其他@本年累计@借方金额+财务费用_利息支出_贴现@本年累计@借方金额", 3, 0, true),
	SERVICE_FEE("手续费", "手续费", 120, "财务费用_手续费@本年累计@借方金额", 3, 0, true),
	FINANCIAL_OTHER("其他1", "其他", 130, "财务费用_其他@本年累计@借方金额", 3, 0, true),
	DEDUCTION_ASSET_IMPAIRMENT_LOSS("减：资产减值损失", "减：资产减值损失", 140, "资产减值损失_其他减值损失@本年累计@借方金额", 2, 0, true),
	RISK_PROVISION("其中： 风险准备金", "风险准备金", 150, null, 3, 0, true), // 以上来自苍穹
	BEGINNING_BALANCE("期（年）初余额", "期初余额", 160, null, 3, 0, true),
//	BEGINNING_BALANCE_YEAR_TOTAL("", "期初余额-本年累计", 0, null, 3, 0, true),//不展示
	ENDING_BALANCE("期（年）末余额", "期末余额", 170, null, 3, 0, true),
	ADDITION_ASSET_DISPOSAL_INCOME("加：资产处置收益", "加：资产处置收益", 180, "资产处置损益@本年累计@贷方金额", 2, 0, true),
	ADDITION_OTHER_INCOME("加：其他收益", "加：其他收益", 190, "其他收益_其他@本年累计@贷方金额+其他收益_手续费返还@本年累计@贷方金额+其他收益_政府补助@本年累计@贷方金额", 2, 0, true),
	ADDITION_NON_OPERATING_INCOME("加：营业外收入", "加：营业外收入", 200, "营业外收入_其他@本年累计@贷方金额", 2, 0, true),
	DEDUCTION_NON_OPERATING_EXPENSE("减：营业外支出", "减：营业外支出", 210, "营业外支出_处置非流动资产损失_处置固定资产净损失@本年累计@借方金额", 2, 0, true),
	TOTAL_PROFIT("利润总额", "利润总额", 220, null, 2, 0, false),
	ASSESSMENT_ADJUSTMENT_ITEM("考核调整项", "考核调整项（增加利润用+号，减少利润用-号）", 230, null, 2, 0, false),
	COST_TYPE_FTP_COST("A.成本类： FTP成本", "成本类： FTP成本", 240, null, 3, 0, false),
	COST_TYPE_OTHER_COST_AMORTIZATION("C.成本类：其他成本摊销", "成本类：其他成本摊销", 250, null, 3, 1, false),
	EXPENSE_TYPE_UNACCRUED_PERFORMANCE_SALARY("D.费用类：本年度应计未计提绩效工资", "费用类：本年度应计未计提绩效工资", 260, null, 3, 1, false),
	EXPENSE_TYPE_OTHER_EXPENSE_AMORTIZATION("E.费用类：其他费用摊销", "费用类：其他费用摊销", 270, null, 3, 1, false),
	TAX_TYPE_SUPPLEMENTAL_URBAN_CONSTRUCTION_TAX("F.税金类：补提城建教育", "税金类：补提城建教育", 280, null, 3, 1, false),
//	TAX_TYPE_SUPPLEMENTAL_STAMP_TAX("G.税金类：补提印花", "税金类：补提印花", 280, null, 3, 1, false),
	TAX_TYPE_OTHER("H.税金类：其他", "税金类：其他", 290, null, 3, 1, false),
	FUND_INCOME("资金收益", "资金收益", 300, null, 3, 1, false),
	GENERAL_OTHER("I.其他", "其他", 310, null, 3, 1, false),
	ASSESSMENT_TOTAL_PROFIT("考核利润总额", "考核利润总额", 320, null, 2, 0, false),
	INCOME_TAX("所得税", "所得税", 330, null,2, 0, false),
	ASSESSMENT_NET_PROFIT("考核净利润", "考核净利润", 340, null, 2, 0, false),
	BUSINESS_INVESTMENT_SCALE("业务投放规模（万元）", "业务投放规模（万元）", 350, null,2, 0, false),
	END_OF_MONTH_ASSET_TOTAL("月末资产规模(万元)", "月末资产总额（万元）", 360, null,2, 0, false),
	;

	private final String excelName;

	private final String display;

	private final int sort;

	//指标名称，用于获取辅助核算值
	private final String riskName;

	private final int level;

	private final Integer readFlag;

	// 是否年累计值，用于上层业务判断是否需要倒减来得到本月值
	private final boolean yearMetric;

	private static Map<String, BudgetExamineBenefitEnum> map;


	static {
		map = Stream.of(BudgetExamineBenefitEnum.values()).collect(Collectors.toMap(BudgetExamineBenefitEnum::name, e -> e, (k1, k2)->k1));
	}


	public static BudgetExamineBenefitEnum of(String name) {
		return map.get(name);
	}

	public static BudgetExamineBenefitEnum findByExcelName(String excelName) {
		for (BudgetExamineBenefitEnum item : values()) {
			if (Objects.equals(item.getExcelName(), excelName)) {
				return item;
			}
		}
		return null;
	}

	public static BudgetExamineBenefitEnum findByDisplay(String display) {
		for (BudgetExamineBenefitEnum item : values()) {
			if (Objects.equals(item.display(), display)) {
				return item;
			}
		}
		return null;
	}
	public int getSort() {
		return sort;
	}

	public String getRiskName() {
		return riskName;
	}

	public String getExcelName() {
		return excelName;
	}

//	BudgetExamineBenefitEnum(String excelName, String display, int sort, String riskName, int level, Integer readFlag, boolean isYearMetric) {
//		this.excelName = excelName;
//		this.display = display;
//		this.sort = sort;
//		this.riskName = riskName;
//		this.level = level;
//		this.readFlag = readFlag;
//		this.isYearMetric = isYearMetric;
//	}
//
//	BudgetExamineBenefitEnum(String display, int sort, String riskName, int level, Integer readFlag) {
//		this(null, display, sort, riskName, level, readFlag, false);
//	}


	@Override
	public String display() {
		return display;
	}
	@Override
	public int level() {
		return level;
	}

	@Override
	public Integer state() {
		return readFlag;
	}

	public boolean isYearMetric() {
		return yearMetric;
	}
}