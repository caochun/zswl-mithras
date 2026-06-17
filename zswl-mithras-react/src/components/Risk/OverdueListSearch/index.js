import { observer } from '@zswl/admin'
import { Table, Button, Modal, Page } from '@zswl/components'
import { useState } from 'react'
import styles from './index.less'
import ImportModal from './components/ImportModal'
import overdueListController from '@/api/riskControl/overdueListController'
import { DatePicker, message } from 'antd'
import { DateColumn } from '@/components/Format'
import DataUpload from '@/components/DataUpload'
import { saveServer } from '@/utils'

const TableList = ({ params }) => {
  const isTodoList = params.type === 'todoList'
  const table = Table.useStore(
    {
      request: (params) => {
        const func = isTodoList
          ? overdueListController.getOverdueListListTodo
          : overdueListController.postOverdueListList
        return func(params)
      },
    },
    [isTodoList]
  )
  const columns = [
    {
      title: '企业名称',
      dataIndex: 'orgName',
      width: 200,
      search: true,
    },
    {
      title: '统一社会信用代码',
      dataIndex: 'orgCode',
      width: 200,
    },
    {
      title: '持续逾期开始时间',
      dataIndex: 'overdueStartDate',
      width: 150,
    },
    DateColumn({
      title: '截至时间',
      dataIndex: 'busiDate',
      width: 150,
      search: {
        title: '名单截至',
        element: <DatePicker />,
        itemProps: {
          transform: (value) => value && value.format('YYYY-MM-DD'),
        },
      },
    }),
  ]

  const modal = Modal.useStore(
    {
      onFinish: async ({ busiDate, file }) => {
        const { fileList } = DataUpload.classify(file)
        const func = isTodoList
          ? overdueListController.getOverdueListImportTodo
          : overdueListController.getOverdueListImport
        await func({ busiDate, multipartFile: fileList[0] })
        table.search()
        modal.close()
        message.success('导入成功')
      },
    },
    []
  )
  return (
    <Page className={!isTodoList && styles.container} noStyle={isTodoList}>
      <Table
        columnsFilter={'pages_overdueListSearch_1'}
        onFilter={(key, val) => saveServer('pages_overdueListSearch_1', val)}
        columns={columns}
        serial
        store={table}
        actions={[
          <Button type="primary" className={styles.importButton} onClick={() => modal.open()}>
            名单导入
          </Button>,
        ]}
      />
      <ImportModal store={modal} />
    </Page>
  )
}

export default observer(TableList)
