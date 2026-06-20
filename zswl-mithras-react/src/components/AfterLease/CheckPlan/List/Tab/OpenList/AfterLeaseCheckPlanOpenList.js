import { useMemo } from 'react'
import { Table, Form, App } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getKeyOptionsLabelMapPlus, saveServer } from '@/utils'
import moment from 'moment'
import store from './store'

const date = new Date()
const year = date.getFullYear()
const mouth = date.getMonth()

function Index() {
  const { staticInfo, $table } = store
  const { optionsType } = App.getData()
  const columns = useMemo(() => {
    return [
      {
        title: '客户名称',
        dataIndex: 'clientName',
        width: 160,
        actions({ clientName, id }) {
          return [
            {
              name: clientName,
              to: `/afterLease/checkPlan/externalDetail/${id}`,
            },
          ]
        },
      },
      {
        title: '合同最终到期日',
        width: 140,
        dataIndex: 'deadline',
        render: (v) => {
          return v || '-'
        },
      },
      {
        title: '审批通过时间',
        width: 140,
        dataIndex: 'approvalPassTime',
        render: (v) => {
          return v || '-'
        },
      },
      {
        title: '状态',
        width: 140,
        dataIndex: 'approvalStatus',
        render: (v) => {
          return getKeyOptionsLabelMapPlus('externalQueryStatus')[v] || '-'
        },
      },
    ]
  }, [])

  return (
    <div>
      <Table
        columnsFilter="afterLeaseCheckOpenList"
        onFilter={(key, val) => saveServer('afterLeaseCheckOpenList', val)}
        resizable
        title={() => {
          return (
            <div>
              当前在租客户：{staticInfo.totalCount}个，完成报告：{staticInfo.doneCount}个
            </div>
          )
        }}
        store={$table}
        autoRequest={true}
        searchbar={{
          initialValues: {
            targetMonth: moment().year(year).month(mouth),
          },
          items: [
            {
              label: '查询月份',
              name: 'targetMonth',
              type: 'datePicker',
              picker: 'month',
              allowClear: false,
            },
            {
              label: '客户名称',
              name: 'clientName',
            },
            {
              label: '审批状态',
              name: 'approvalStatus',
              type: 'select',
              options: optionsType.externalQueryStatus,
            },
          ],
        }}
        actions={[]}
        columns={columns}
      />
    </div>
  )
}

export default observer(Index)
