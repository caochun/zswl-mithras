import { getSearchColumns, getTableColumns, saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { useEffect } from 'react'
import ALL_COLUMNS from './Column'
import styles from './index.less'
import store from './store'

// 表格列配置（序号列在 Column.js 中按 page/pageSize 计算）
const nameColumns = [
  '序号',
  '归档类型',
  '融资编号',
  '项目类别/业务类型',
  '产品名称/融资机构',
  '融资状态',
  '资金经理',
  '起息日',
  '到期日',
  '是否完成归档',
  '归档时间',
  '操作',
]

// 搜索表单配置
const formNameColumns = [
  '融资编号',
  '归档类型',
  '项目类别',
  '业务类型',
  '产品名称',
  '融资机构',
  '融资状态',
  '资金经理',
  '是否完成归档',
  '起息日',
  '到期日',
  '归档时间',
]

// 下载列表表格列配置
const downloadListColumns = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 80,
    render: (text, record, index) => index + 1,
  },
  {
    title: '文件名称',
    dataIndex: 'fileName',
    width: 250,
    render: (val) => val || '-',
  },
  {
    title: '下载时间',
    dataIndex: 'downloadTime',
    width: 180,
    render: (val) => val || '-',
  },
  {
    title: '下载状态',
    dataIndex: 'downloadStatus',
    width: 120,
    render: (val) => {
      const statusMap = {
        DOWNLOADING: '下载中',
        SUCCESS: '下载完成',
        FAILED: '下载失败',
      }
      return statusMap[val] || val || '-'
    },
  },
  {
    title: '操作',
    dataIndex: 'operation',
    width: 100,
    render: (val, record) => {
      // 当下载状态为"下载完成"时，展示"下载"超链接
      if (record.downloadStatus === '已下载') {
        return <a onClick={() => store.downloadFile(record)}>下载</a>
      }
      return null
    },
  },
]

const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getSearchColumns(ALL_COLUMNS, formNameColumns)

function Index({ query }) {
  useEffect(() => {
    // 设置导出列配置（排除操作列）
    store.setExportColumns(columns.filter((col) => col.dataIndex !== 'operation'))
  }, [])

  return (
    <div className={styles.customerWrap}>
      <Table
        columnsFilter={'archives_financial_tab'}
        onFilter={(key, val) => saveServer('archives_financial_tab', val)}
        selectable
        rowKey="filingMaterialsId"
        store={store.table}
        editable={false}
        searchbar={{
          labelCol: { span: 10 },
          items: formColumns,
        }}
        actions={[
          {
            name: '导出',
            type: 'primary',
            onClick: store.exportData,
          },
          {
            name: '批量下载文件',
            onClick: store.batchDownloadFile,
          },
          {
            name: '下载列表',
            onClick: store.openDownloadList,
          },
        ]}
        scroll={{
          x: 1600,
        }}
        columns={columns}
      />

      {/* 下载列表弹窗 */}
      <Modal title="下载列表" width={800} destroyOnClose store={store.downloadListModal} footer={null}>
        <Table store={store.downloadListTable} columns={downloadListColumns} pagination={{ pageSize: 10 }} />
      </Modal>
    </div>
  )
}

export default observer(Index)
