import { Access, Button, Form, Modal, ModalStore, Page, Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns, rules } from '@/utils'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import { Card, DatePicker, Input, Tooltip, message } from 'antd'
import moment from 'moment'
import EditModal from '../../FinancingCostEditModal/BudgetFinancingCostEditModal'
import { saveServer } from '@/utils'
import financingCostsApi from '@/api/budget/pricing/ftp/financingCostsApi'

function Index({ path, canEdit, params }) {
  const reload = async () => {
    await financingCostsApi.postDraftPricingFlash(params)
    message.success('刷新成功')
    table.search()
  }

  const hasValuation = Access.validate('newFtpFinancingCostPricingModify')
  const columns = useMemo(() => {
    const nameColumns = getTableColumns(ALL_COLUMNS, [
      {
        title: '时间',
        dataIndex: 'month',
        render: (value) => {
          return value ? moment(value).format('YYYY-MM') : '-'
        },
      },
      {
        title: '月均值',
        children: [
          { title: '一年期当月均值(%)', rename: '1年期(含）' },
          { title: '1-3年期当月均值(%)', rename: '1-3年期(含)' },
          { title: '3-5年期当月均值(%)', rename: '3年以上' },
        ],
      },
      {
        title: '季度均值',
        children: [
          { title: '一年期季度均值(%)', rename: '1年期(含）' },
          { title: '1-3年期季度均值(%)', rename: '1-3年期(含)' },
          { title: '3-5年期季度均值(%)', rename: '3年以上' },
        ],
      },
      {
        title: '当年平均值',
        children: [
          { title: '一年期当年均值(%)', rename: '1年期(含）' },
          { title: '1-3年期当年均值(%)', rename: '1-3年期(含)' },
          { title: '3-5年期当年均值(%)', rename: '3年以上' },
        ],
      },
    ])

    return [
      ...nameColumns,
      canEdit && {
        title: '操作',
        dataIndex: 'action',
        fixed: 'right',
        width: 160,
        actions: (record) => [
          {
            name: '编辑',
            onClick: () => modal.open(record),
          },
        ],
      },
    ].filter(Boolean)
  }, [canEdit])
  const table = useMemo(() => {
    return new TableStore({
      request: async (restParams) => {
        const { list = [], ...rest } =
          (await financingCostsApi.postDraftDetail({
            ...restParams,
            ...params,
          })) ?? {}
        const newData = list.map(({ month, bodyMap }) => {
          const newObj = {}
          Object.entries(bodyMap).forEach(([key, value]) => {
            newObj[`${key}_currentAverage`] = value.currentAverage
            newObj[`${key}_annualAverage`] = value.annualAverage
            newObj[`${key}_currentQuarterAverage`] = value.currentQuarterAverage
          })
          return {
            month: moment(month).format('YYYY-MM-DD'),
            ...newObj,
          }
        })
        return { list: newData, ...rest }
      },
    })
  }, [])

  const modal = useMemo(() => {
    return new ModalStore({
      onOpen: async (values) => {
        if (!values) return {}
        const {
          month,
          ONE_YEAR_annualAverage,
          ONE_YEAR_currentAverage,
          ONE_TO_THREE_YEARS_annualAverage,
          ONE_TO_THREE_YEARS_currentAverage,
          MORE_THAN_THREE_YEARS_annualAverage,
          MORE_THAN_THREE_YEARS_currentAverage,
          ONE_YEAR_currentQuarterAverage,
          ONE_TO_THREE_YEARS_currentQuarterAverage,
          MORE_THAN_THREE_YEARS_currentQuarterAverage,
        } = values
        return {
          ...values,
          oneCurrentQuarterAverage: ONE_YEAR_currentQuarterAverage,
          oneCurrentAverage: ONE_YEAR_currentAverage,
          oneAnnualAverage: ONE_YEAR_annualAverage,
          threeCurrentAverage: ONE_TO_THREE_YEARS_currentAverage,
          threeCurrentQuarterAverage: ONE_TO_THREE_YEARS_currentQuarterAverage,
          threeAnnualAverage: ONE_TO_THREE_YEARS_annualAverage,
          fiveCurrentAverage: MORE_THAN_THREE_YEARS_currentAverage,
          fiveCurrentQuarterAverage: MORE_THAN_THREE_YEARS_currentQuarterAverage,
          fiveAnnualAverage: MORE_THAN_THREE_YEARS_annualAverage,
          month: month && moment(month),
        }
      },
      onFinish: async (values) => {
        await financingCostsApi.postDraftPricingModify({ ...values, ...params })
        message.success('修改成功')
        modal.close()
        table.search()
      },
    })
  }, [params])
  return (
    <Card
      title="融资成本"
      style={{ marginTop: 12 }}
      extra={
        canEdit && (
          <Tooltip title="刷新重新计算最近一个结束月均值">
            <Button type="primary" onClick={reload} access={'newFtpFinancingCostPricingFlash'}>
              刷新
            </Button>
          </Tooltip>
        )
      }
    >
      <Table
        columnsFilter="detail_BaseSet_Financing"
                onFilter={(key,val) => saveServer('detail_BaseSet_Financing',val)}

        store={table}
        editable={false}
        scroll={{
          x: 'auto',
        }}
        columns={columns}
      />
      <EditModal store={modal} />
    </Card>
  )
}

export default observer(Index)
