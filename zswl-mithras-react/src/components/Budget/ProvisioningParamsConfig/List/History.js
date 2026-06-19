import { observer, history } from '@zswl/admin'
import { Form, Modal, Table } from '@zswl/components'

const { Item } = Form

const Index = ({ store }) => {
  const columns = [
    {
      title: '配置名称',
      dataIndex: 'configName',

      render: (text, record) => {
        return (
          <a
            onClick={() => {
              store.historyModal.close()
              history.push(`/budget/provisioning/paramsConfig/detail/${record.id}?view=version`)
            }}
          >
            {text}
          </a>
        )
      },
    },
    { title: '版本时间', dataIndex: 'versionTime' },
    // {
    //   title: '配置模块',
    //   dataIndex: 'configModule',
    //   width: 100,
    // },
    // {
    //   title: '配置编码',
    //   dataIndex: 'configCode',
    // },
    // {
    //   title: '配置值',
    //   dataIndex: 'configValue',
    //   width: 600,
    // },
  ]
  return (
    <Modal title={'历史版本'} store={store.historyModal} destroyOnClose footer={null} width={800}>
      <Table
        pagination={false}
        serial
        columnWidth={200}
        scroll={{ x: 'auto' }}
        store={store.$historyTable}
        columns={columns}
      />
    </Modal>
  )
}

export default observer(Index)
