import { observer } from '@zswl/admin'
import { AmountColumn, DateColumn, MatchOptionColumn } from '@/components/Format'
import { App, Table, TableStore } from '@zswl/components'
import { FormAmount } from '@/components/Form'
import { Space } from 'antd'
import { saveServer } from '@/utils'

const INIT_FORMAT = 10000 * 10000
const PARENT_FORMAT = 10000

const ProjectTable = observer(({ store }) => {
  const columns = [
    { title: '部门名称', dataIndex: 'belongDeptName' },
    {
      title: '客户名称',
      dataIndex: 'clientName',
      actions: ({ clientName, id }) => [
        {
          name: clientName,
          onClick: () => {
            store.detailModal.open({ id })
          },
        },
      ],
    },
    MatchOptionColumn({
      title: '行业分类',
      dataIndex: 'ftpIndustryCategory',
      matchOption: 'ftpIndustryCategoryEnum',
    }),
    MatchOptionColumn({ title: '业务类型', dataIndex: 'leaseType' }),
    { title: '项目地区', dataIndex: 'areaName', width: 180 },
    { title: '项目主办', dataIndex: 'sponsorUserName' },
    AmountColumn({
      title: '项目金额（万元）',
      dataIndex: 'projectAmount',
      initFormat: INIT_FORMAT,
      width: 160,
    }),
    DateColumn({ title: '投放日', dataIndex: 'payDate' }),
    MatchOptionColumn({
      title: '还款频率',
      dataIndex: 'repayFrequency',
      matchOption: 'repaymentFrequencyEnum',
    }),
    AmountColumn({
      title: '租赁期限（月）',
      dataIndex: 'termMonth',
      initFormat: 1,
    }),
    AmountColumn({
      title: '首期租金率（%）',
      dataIndex: 'firstRentRate',
      initFormat: PARENT_FORMAT,
      width: 180,
    }),
    AmountColumn({
      title: '保证金率（%）',
      dataIndex: 'depositRate',
      initFormat: PARENT_FORMAT,
    }),

    AmountColumn({
      title: '咨询费率（%）',
      dataIndex: 'consultingFeeRate',
      initFormat: PARENT_FORMAT,
    }),
    AmountColumn({
      title: '手续费率（%）',
      dataIndex: 'commissionRate',
      initFormat: PARENT_FORMAT,
    }),
    {
      title: '合同利率',
      dataIndex: 'contractInterestRate',
      render: (val, record) => {
        const { contractInterestRateType, contractInterestRate } = record

        return (
          <Space>
            <span>{App.matchOption('rateType', contractInterestRateType)?.label}</span>
            <FormAmount.Format value={contractInterestRate} suffix="%" />
          </Space>
        )
      },
    },

    AmountColumn({
      title: 'IRR（%）',
      dataIndex: 'irr',
      initFormat: PARENT_FORMAT,
      width: 100,
    }),
    AmountColumn({
      title: 'FTP(%)',
      dataIndex: 'ftp',
      initFormat: PARENT_FORMAT,
      suffix: '%',
    }),
    AmountColumn({
      title: '营业收入-不含税（万元）',
      dataIndex: 'incomeWithoutTax',
      initFormat: INIT_FORMAT,
      width: 200,
    }),
    AmountColumn({
      title: '营业成本（万元）',
      dataIndex: 'costWithoutTax',
      initFormat: INIT_FORMAT,
      width: 200,
    }),
    AmountColumn({
      title: '税金及附加（万元）',
      dataIndex: 'taxAndOther',
      initFormat: INIT_FORMAT,
      width: 200,
    }),
    AmountColumn({
      title: '风险准备金（万元）',
      dataIndex: 'riskReserve',
      initFormat: INIT_FORMAT,
      width: 200,
    }),
    AmountColumn({
      title: '考核利润（万元）',
      dataIndex: 'profit',
      initFormat: INIT_FORMAT,
      width: 200,
    }),
    AmountColumn({
      title: '费用（万元）',
      dataIndex: 'expense',
      initFormat: INIT_FORMAT,
    }),
    AmountColumn({
      title: '扣费后利润（万元）',
      dataIndex: 'profitWithoutExpense',
      initFormat: INIT_FORMAT,
      width: 200,
    }),
    // MatchOptionColumn({ title: '支付方式', dataIndex: 'payType' }),
    // AmountColumn({
    //   title: '名义价款（万元）',
    //   dataIndex: 'nominalPrice',
    //   initFormat: INIT_FORMAT,
    //   width: 160,
    // }),
    // MatchOptionColumn({
    //   title: '利息计算方式',
    //   dataIndex: 'interestCalculateWay',
    //   matchOption: 'repayCalcType',
    // }),
  ]

  return (
    <>
      <Table
        selectable
        columnWidth={140}
        autoRequest={false}
        columnsFilter="placement_plan_year"
        onFilter={(key, val) => saveServer('placement_plan_year', val)}
        columns={columns}
        store={store?.projectTable}
        scroll={{ x: 2000, y: 500 }}
        rowKey="id"
      />
    </>
  )
})

export default ProjectTable
