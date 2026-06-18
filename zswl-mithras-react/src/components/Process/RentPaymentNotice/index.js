import { Table, TableStore, Button } from '@zswl/components'
import { useMemo, useState } from 'react'
import { getTableColumns, getFormColumns } from '@/utils'
import { Drawer, message } from 'antd'
import ALL_COLUMNS from './Column'
import { compareTableData } from '@/utils'
import Api from './api'
import { saveServer } from '@/utils'

const formNameColumns = []
const nameColumns = [
  '主办',
  '合同编号',
  '期数',
  '租金支付日',
  '租金',
  '租赁成本',
  '租赁利息',
  '户名',
  '开户行',
  '帐号',
]
const Index = ({ id, processStatus, taskActivityId, taskStatus }) => {
  // 新增资金经理节点，只有这个节点才能编辑 银行信息,taskStatus ，处于当前节点
  const canEdit = taskStatus === '1' && taskActivityId === 'userTask_moneymanager'
  const [current, setCurrent] = useState({})
  const [open, setOpen] = useState(false)
  const [editableKey, setEditableKey] = useState(null)

  const [previewUrl, setPreviewUrl] = useState(false)
  const columns = getTableColumns(ALL_COLUMNS(), nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS(), formNameColumns)

  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const data = await Api.getList({ id, ...params })
        return {
          ...data,
          list: compareTableData(data.list).newDetail,
        }
      },
    })
  }, [id])

  // 批量下载文件
  const downLoadFiles = async () => {
    await Api.downLoadFiles({ id })
  }
  // 单个下载文件
  const downloadSingleFile = async () => {
    await Api.downLoadFile({ id: current.id })
  }
  // 下载全部清单
  const downLoadJson = async () => {
    await Api.downLoadList({ id })
  }

  const getNoticeFile = async (record) => {
    const htmlRes = await Api.previewFile({ id: record.id })
    setPreviewUrl(htmlRes)
    setOpen(true)
    setCurrent(record)
  }

  const refresh = async () => {
    await Api.refresh({ id })
    message.success('操作成功')
    $table.search()
  }

  const save = async () => {
    const { values } = await $table.submit()
    await Api.update({
      id: editableKey,
      ...values[editableKey],
    })
    message.success('操作成功')
    setEditableKey(null)
    $table.search()
  }

  return (
    <div>
      <Table
        columnsFilter={'Detail_RentPaymentNotice_1'}
        onFilter={(key, val) => saveServer('Detail_RentPaymentNotice_1', val)}
        columnWidth={180}
        editable={(record) => record.id === editableKey}
        scroll={{ x: 1500 }}
        store={$table}
        columns={[
          {
            title: '客户名称',
            dataIndex: 'clientName',
            render: (value, record) => <a onClick={() => getNoticeFile(record)}>{value}</a>,
            editable: false,
          },
          ...columns,
          canEdit && {
            title: '操作',
            fixed: 'right',
            width: 120,
            actions({ id }) {
              return [
                {
                  name: '编辑',
                  hidden: id === editableKey,
                  onClick() {
                    setEditableKey(id)
                  },
                },
                {
                  name: '保存',
                  hidden: id !== editableKey,
                  onClick: save,
                },
                {
                  name: '取消',
                  hidden: id !== editableKey,
                  onClick: () => {
                    setEditableKey(null)
                  },
                },
              ]
            },
          },
        ]}
        extra={[
          // 审批中就有
          processStatus !== '2' && {
            access: 'rentnotify_process_prepare_refresh',
            name: '刷新租金信息',
            type: 'primary',
            onClick: refresh,
          },
          processStatus === '2' && {
            access: 'rentnotify_process_prepare_detail_batch_download',
            name: '下载文件',
            type: 'primary',
            onClick: downLoadFiles,
          },
          {
            access: 'rentnotify_process_prepare_detail_list_download',
            name: '下载',
            onClick: downLoadJson,
          },
        ].filter(Boolean)}
        searchbar={{
          items: formColumns,
        }}
      />
      <Drawer
        title="租⾦⽀付通知书"
        onClose={() => setOpen(false)}
        open={open}
        size="large"
        extra={
          <Button
            type="primary"
            onClick={downloadSingleFile}
            access={'rentnotify_process_prepare_detail_single_download'}
          >
            下载
          </Button>
        }
      >
        <div dangerouslySetInnerHTML={{ __html: previewUrl }}></div>
      </Drawer>
    </div>
  )
}
export default Index
