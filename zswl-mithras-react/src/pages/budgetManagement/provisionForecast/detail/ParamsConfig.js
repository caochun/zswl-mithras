import { history, observer } from '@zswl/admin'
import { Page, Table } from '@zswl/components'

const ParamsConfig = ({ store }) => {
  const columns = [
    {
      title: '配置项名称',
      dataIndex: 'configName',
      actions: ({ configName: name, id }) => [
        {
          name,
          onClick: () => {
            history.push(
              `/budgetManagement/provisionForecast/detail/configDetail/${id}?view=readOnly`
            )
          },
        },
      ],
    },

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
              history.push(`/budgetManagement/provisionForecast/detail/configDetail/${record.id}`)
            },
            type: 'link',
          },
        ]
      },
    },
  ]
  return (
    <Table
      // columnsFilter="budget_provisioning_config"
      store={store.configTable}
      serial
      columnWidth={180}
      editable={false}
      scroll={{ x: 1000 }}
      columns={columns}
    />
  )
}
export default observer(ParamsConfig)
