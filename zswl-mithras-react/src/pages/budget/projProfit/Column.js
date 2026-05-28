import { hasValue, amountFormat, formatPercent } from '@/utils'
import { FounderSelect, OrgSelect } from '@/components'
import { AmountColumn, FiledFormat, MatchOptionColumn } from '@/components/Format'
import { history } from '@zswl/admin'

const ALL_COLUMNS = ({ pathname } = {}) => {
  return [
    // 列表
    {
      title: '月份',
      dataIndex: 'month',
      actions({ month, year, id }) {
        return [
          {
            name: year ? `${year}年${month}月` : '-',
            onClick: () => history.push(`${pathname}/detail/${id}`),
          },
        ]
      },
    },
    MatchOptionColumn({
      title: '状态',
      dataIndex: 'isConfirmed',
      matchOption: 'isConfirmedEnum',
    }),
    AmountColumn({ title: '当年累计收入', dataIndex: 'totalIncomeThisYear' }),
    AmountColumn({ title: '本年累计资金成本', dataIndex: 'totalCostThisYear' }),
    AmountColumn({ title: '本月风险金余额', dataIndex: 'totalRiskThisYear' }),
    AmountColumn({ title: '本年累计附加税', dataIndex: 'totalAdditionalTaxThisYear' }),
    AmountColumn({ title: '本年累计印花税', dataIndex: 'totalStampTaxThisYear' }),
    AmountColumn({
      title: '本年累计利润总额(扣除费用后)',
      dataIndex: 'totalProfitThisYear',
      width: 250,
    }),
    AmountColumn({ title: '本月收入', dataIndex: 'incomeThisMonth' }),
    AmountColumn({ title: '本月资金成本', dataIndex: 'costThisMonth' }),
    AmountColumn({ title: '本月风险金计提/冲抵', dataIndex: 'riskThisMonth' }),
    AmountColumn({ title: '利润-当期值', dataIndex: 'profitThisMonth' }),
    // 详情
    {
      title: '业务部门',
      dataIndex: 'bizDeptId',
      // fixed: 'left',
      render: (val, { bizDeptName }) => <FiledFormat title={bizDeptName} />,
      width: 140,
      editable: {
        element: <OrgSelect />,
        functionCode: 'budgetProjProfitOrgSelect',
      },
    },

    {
      title: '主办人员',
      dataIndex: 'sponsorUserId',
      render: (val, { sponsorUserName }) => <FiledFormat title={sponsorUserName} />,
      width: 130,
      editable: {
        element: <FounderSelect />,
        functionCode: 'budgetProjProfitFounderSelect',
      },
    },
    {
      title: '合同编号',
      width: 280,
      dataIndex: 'contractCode',
    },
    {
      title: '项目名称',
      dataIndex: 'projName',
      width: 280,
    },
    { title: '业务大类', dataIndex: 'bizType' },
    { title: '业务小类', dataIndex: 'bizSubType' },
    { title: '投放日', dataIndex: 'contractStartDate' },
    { title: '风控行业分类', dataIndex: 'riskControlIndustryClassify' },
    {
      title: '考核部门',
      dataIndex: 'bizDeptId',
      // fixed: 'left',
      render: (val, { bizDeptName }) => <FiledFormat title={bizDeptName} />,
      width: 200,
      editable: {
        element: <OrgSelect />,
        functionCode: 'budgetProjProfitOrgSelect',
      },
    },
    AmountColumn({ title: '本月利息收入', dataIndex: 'interestIncomeThisMonth' }),
    AmountColumn({ title: '本月其他收入', dataIndex: 'otherIncomeThisMonth' }),
    AmountColumn({ title: '本年累计收入', dataIndex: 'totalIncomeThisYear' }),
    AmountColumn({ title: '本月毛利', dataIndex: 'grossProfitThisMonth' }),
    AmountColumn({ title: '本年累计毛利', dataIndex: 'totalGrossProfitThisYear' }),
    AmountColumn({ title: '本月风险金', dataIndex: 'riskThisMonth' }),
    AmountColumn({ title: '本年累计附加税', dataIndex: 'totalAdditionalTaxThisYear' }),
    AmountColumn({ title: '本年累计利润总额', dataIndex: 'totalProfitThisYearBefore' }),
    AmountColumn({ title: '年初风险金余额', dataIndex: 'riskBalanceBeginYear' }),
    AmountColumn({ title: '累计风险金计提/冲抵', dataIndex: 'totalRiskBalanceThisYear' }),
    AmountColumn({ title: '费用比例', dataIndex: 'expenseRadio', suffix: '%' }),
    AmountColumn({ title: '本年累计利润总额（扣费后）', dataIndex: 'totalProfitThisYear' }),
  ]
}

export default ALL_COLUMNS
