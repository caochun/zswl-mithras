import { App, Table } from '@zswl/components'
import styles from '../../index.less'
import { amountFormat } from '@/utils'
import { saveServer } from '@/utils'

const WriteOffTable = ({ store }) => {
  const columns = [
    {
      title: '操作时间',
      width: 150,
      dataIndex: 'createTime',
    },
    {
      title: '操作人',
      width: 120,
      dataIndex: 'createBy',
    },
    {
      title: '被操作明细',
      width: 120,
      dataIndex: 'operateDetails',
    },
    {
      title: '操作',
      width: 130,
      dataIndex: 'operate',
    },
    {
      title: '单据状态',
      width: 130,
      dataIndex: 'status',
    },
  ]
  return (
    <>
      <Table         columnsFilter={'Components_RecordList_WriteOffTable'}
              onFilter={(key,val) => saveServer('Components_RecordList_WriteOffTable',val)} store={store.writeOffTable} columns={columns} />
    </>
  )
}

export default WriteOffTable
