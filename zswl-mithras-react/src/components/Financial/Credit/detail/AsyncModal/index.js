import { Upload, Space, Tooltip, Empty, message } from 'antd'
import { Modal, App, Table } from '@zswl/components'
import { saveServer } from '@/utils'

const { Dragger } = Upload

function AsyncModal({ modalStore, id, ...rest }) {
  const columns = [
    {
      title: '字段名称',
      dataIndex: 'name',
    },
    {
      title: '天眼查数据',
      dataIndex: 'dataF',
    },
    {
      title: '是否以天眼查为准',
      editable: {
        element: <Switch checkedChildren="是" unCheckedChildren="否" defaultChecked={false} />,
      },
    },
  ]
  return (
    <Modal title={'相关信息同步'} footer={null} store={modalStore} okText={'确定'} {...rest}>
      <Table         columnsFilter={'detail_AsyncModal_1'}
        onFilter={(key,val) => saveServer('detail_AsyncModal_1',val)} columns={columns}></Table>
    </Modal>
  )
}

export default AsyncModal
