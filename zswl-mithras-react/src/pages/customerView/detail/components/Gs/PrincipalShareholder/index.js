import { Table } from '@zswl/components'
import { Typography } from 'antd'
import { history, observer } from '@zswl/admin'
import { saveServer } from '@/utils'

export default observer(function CreditInformation({ store, id }) {
  const columns = [
    {
      title: '股东名称',
      dataIndex: 'name',
      key: 'name',
      width: 300,
      render: (text, r) => {
        if (!text) return '-'
        if (r.type === 2) {
          return (
            <a
              onClick={(e) => {
                history.push(`/customerView/detail/?enterpriseName=${r.name}&uscc=${r.uscc}`)
              }}
            >
              {text}
            </a>
          )
        } else {
          return text
        }
      },
    },
    // {
    //   // yapy 无字段
    //   title: '股东类型',
    //   dataIndex: 'type',
    //   key: 'type',
    //   width: 120,
    //   align: 'center',
    // },
    {
      title: '持股比例',
      dataIndex: 'ratio',
      key: 'ratio',
      width: 120,
      align: 'center',
      render: (text) => text || '-',
    },
    {
      title: '持股数（股）',
      dataIndex: 'shares_holding',
      key: 'shares_holding',
      width: 160,
      align: 'center',
      render: (text) => text || '-',
    },
    {
      title: '股东类别',
      dataIndex: 'type',
      key: 'type',
      width: 160,
      align: 'center',
      render: (text) => {
        if (!text) return '-'
        if (text === 1) {
          return '自然人'
        } else if (text === 2) {
          return '企业、证券'
        } else {
          return '其他'
        }
      },
    },
  ]

  return (
    <div id={id}>
      <Typography.Title level={5}>主要股东（更新时间：2023-10-31）</Typography.Title>
      <div>
        <Table         columnsFilter={'Gs_PrincipalShareholder_1'}
                onFilter={(key,val) => saveServer('Gs_PrincipalShareholder_1',val)} resizable store={store.principalShareholderStore} columns={columns} bordered />
      </div>
    </div>
  )
})
