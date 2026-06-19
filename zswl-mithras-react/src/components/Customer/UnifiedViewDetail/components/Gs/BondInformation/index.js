import { Table } from '@zswl/components'
import { Typography } from 'antd'
import { saveServer } from '@/utils'

export default function BondInformation({ store, id }) {
  const columns = [
    {
      title: '债券全称',
      dataIndex: 'bondFullName',
      key: 'bondFullName',
      width: 500,
      render: (text) => text || '-',
    },
    {
      title: '债券代码',
      dataIndex: 'bondCode',
      key: 'bondCode',
      width: 150,
      align: 'center',
      render: (text) => text || '-',
    },
    {
      title: '当前状态',
      dataIndex: 'bondCurrentStatus',
      key: 'bondCurrentStatus',
      width: 150,
      align: 'center',
      render: (text) => {
        if (!text) return '-'
        const statusMap = {
          1: '未到期',
          2: '已到期',
          3: '发行失败',
        }
        return statusMap[text] || '未知状态'
      },
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 200,
      align: 'center',
      render: (text) => text || '-',
    },
  ]

  const dataSource = [
    {
      key: '1',
      bondName: '泰州凤城河建设发展有限公司2014年中小企业私募债券',
      bondCode: '125284.SH',
      status: '未到期',
      updateTime: '2024-01-24 12:23:11',
    },
    {
      key: '2',
      bondName: '泰州华信商业投资有限公司',
      bondCode: '098089.IB',
      status: '未到期',
      updateTime: '2024-01-24 12:23:11',
    },
  ]

  return (
    <div id={id}>
      <Typography.Title level={5}>债券信息</Typography.Title>
      <div>
        <Table
                columnsFilter={'Gs_BondInformation_1'}
                onFilter={(key,val) => saveServer('Gs_BondInformation_1',val)}
          resizable
          store={store.bondInformationStore}
          rowKey="key"
          columns={columns}
          bordered
        />
      </div>
    </div>
  )
}
