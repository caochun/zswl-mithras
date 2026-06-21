import { observer, history } from '@zswl/admin'
import { Table, Page } from '@zswl/components'
import History from './History'
import store from './store'
import { useEffect } from 'react'

const BudgetProvisioningParamsConfigList = ({ pathname, query }) => {
  const { type } = query || {}
  useEffect(() => {
    if (type === 'reload') {
      history.replace('/budget/provisioning/paramsConfig')
    }
  }, [type])

  const columns = [
    {
      title: '配置项名称',
      dataIndex: 'configName',
      actions: ({ configName: name, id }) => [
        {
          name,
          onClick: () => {
            history.push(`/budget/provisioning/paramsConfig/detail/${id}?view=readOnly`)
          },
        },
      ],
    },
    { title: '版本时间', dataIndex: 'versionTime' },

    {
      title: '操作',
      dataIndex: 'configOption',
      fixed: 'right',
      width: 120,
      actions: (record) => {
        return [
          {
            name: '修改',
            onClick: () => {
              history.push(`/budget/provisioning/paramsConfig/detail/${record.id}`)
            },
            type: 'link',
          },
          {
            name: '历史版本',
            onClick: async () => store.historyModal.open(record),
            type: 'link',
          },
        ]
      },
    },
  ]
  return (
    <Page store={store} params={{ pathname }}>
      <Table
        // columnsFilter="budget_provisioning_config"
        store={store.$table}
        serial
        columnWidth={180}
        editable={false}
        scroll={{
          x: 1000,
        }}
        columns={columns}
      />
      <History store={store} />
    </Page>
  )
}
export default observer(BudgetProvisioningParamsConfigList)
