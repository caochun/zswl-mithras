import { Table, Modal } from '@zswl/components'
import { history } from '@zswl/admin'
import { AmountColumn, InputColumn, MatchOptionColumn, AmountFormat } from '@/components/Format'
import { getTableColumns, getSearchColumns, amountFormat } from '@/utils'
import { message } from 'antd'
import Api from './api'
import { saveServer } from '@/utils'

const Index = ({ params: { id }, query: { processType, canEditFlag = 'true' } }) => {
  const canEdit = canEditFlag === 'true'
  // 还本付息计划确认流程
  const isFinancingRepayPlan = processType === 'FinancingRepayPlanConfirmFlow'
  const ALL_COLUMNS = [
    InputColumn({
      title: '融资编号',
      dataIndex: 'financingCode',
      width: 200,
      actions: ({ financingCode: name, financingType, financingId }) => [
        {
          name,
          onClick: () => {
            if (financingType === 'ZR') {
              history.push(`/financial/direct/detail/${financingId}`)
            } else {
              history.push(`/financial/fund/detail/${financingId}`)
            }
          },
        },
      ],
    }),
    InputColumn({
      title: '融资机构',
      dataIndex: 'organizationName',
      width: 200,
    }),
    AmountColumn({
      title: '融资金额(万元)',
      dataIndex: 'financingAmount',
      render: (value, { financingType }) => {
        return (
          <AmountFormat value={value} initFormat={financingType === 'ZR' ? 10000 : 10000 * 10000} />
        )
      },
    }),
    InputColumn({
      title: '应还日期',
      dataIndex: 'repayDate',
    }),
    AmountColumn({
      title: '应还金额(元)',
      dataIndex: 'repayAmount',
      // initFormat: 10000 * 10000,
    }),
    AmountColumn({
      title: '应还本金(元)',
      dataIndex: 'principleAmount',
      // initFormat: 10000 * 10000,
    }),
    AmountColumn({
      title: '应还利息(元)',
      dataIndex: 'interestAmount',
      // initFormat: 10000 * 10000,
    }),
    InputColumn({
      title: '还款银行',
      dataIndex: 'accountBank',
    }),
    InputColumn({
      title: '还款账号',
      dataIndex: 'accountNumber',
    }),
    MatchOptionColumn({
      title: '账户类别',
      dataIndex: 'fundFinancingAccountType',
      matchOption: 'fundFinancingAccountTypeEnum',
    }),
    isFinancingRepayPlan &&
    MatchOptionColumn({
      title: '确认状态',
      dataIndex: 'isConfirmed',
      matchOption: 'isConfirmedEnum',
    }),
    !isFinancingRepayPlan &&
    MatchOptionColumn({
      title: '还款状态',
      dataIndex: 'isPaid',
      matchOption: 'isConfirmedEnum',
    }),
  ].filter(Boolean)

  const columns = getTableColumns(ALL_COLUMNS)
  const table = Table.useStore(
    {
      request: (params) => {
        return Api.getList({
          id,
          ...params,
        })
      },
    },
    [id]
  )

  const handleConfirm = async (
    { financingId, financingType, id: recordId, financingRepayActualId },
    extraParams
  ) => {
    const commonParams = {
      financingId,
      financingType,
      financingRepayActualId,
      id: recordId,
      ...extraParams,
    }
    if (isFinancingRepayPlan) {
      Modal.confirm({
        title: '确认操作吗？',
        onOk: async () => {
          await Api.postPlanModify({ ...commonParams })
          message.success('操作成功')
          table.search()
        },
      })
      return
    }
    Modal.confirm({
      title: '确认操作吗？',
      onOk: async () => {
        await Api.postWriteOffModify({ ...commonParams })
        message.success('操作成功')
        table.search()
      },
    })
  }

  return (
    <Table
      columnsFilter={'Component_FinancingRepayPlanConfirmFlow_1'}
      onFilter={(key, val) => saveServer('Component_FinancingRepayPlanConfirmFlow_1', val)}
      scroll={{
        x: true,
      }}
      columns={[
        ...columns,
        canEdit && {
          title: '操作',
          dataIndex: 'action',
          actions: (record) => {
            const { isConfirmed, isPaid } = record
            return [
              !isFinancingRepayPlan &&
              isPaid === 0 && {
                name: '确认',
                onClick: () => {
                  handleConfirm(record, {
                    isPaid: 1,
                  })
                },
              },
              !isFinancingRepayPlan &&
              isPaid === 1 && {
                name: '取消',
                onClick: () => {
                  handleConfirm(record, {
                    isPaid: 0,
                  })
                },
              },
              isFinancingRepayPlan &&
              isConfirmed === 0 && {
                name: '确认',
                onClick: () => {
                  handleConfirm(record, {
                    isConfirmed: 1,
                  })
                },
              },
              isFinancingRepayPlan &&
              isConfirmed === 1 && {
                name: '取消',
                onClick: () => {
                  handleConfirm(record, {
                    isConfirmed: 0,
                  })
                },
              },
            ]
          },
        },
      ]}
      store={table}
    ></Table>
  )
}
export default Index
