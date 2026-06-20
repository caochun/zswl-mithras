import { getFormColumns, getTableColumns, saveServer } from '@/utils'
import { history } from '@zswl/admin'
import { Modal, Table, TableStore } from '@zswl/components'
import { message } from 'antd'
import { useMemo } from 'react'
import ALL_COLUMNS from './Column'
import Api from '@/api/process/application/myProcessApi'
import { contractCheckIrr as checkIrr } from '@/components/Contract/StartRentCheckEntries'

const formNameColumns = ['表单名称', '流程类型']
const nameColumns = [
  'ID',
  '流程类型',
  '表单名称',
  '项目名称',
  '项目编号',
  '客户名称',
  '当前节点',
  '当前审批人',
  '申请时间',
]
const Index = () => {
  const columns = getTableColumns(ALL_COLUMNS(), nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS(), formNameColumns)

  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const data = await Api.getPrepareList(params)
        return data
      },
    })
  }, [])

  const cancelProcess = async (record) => {
    Modal.confirm({
      title: '确认取消吗？',
      onOk: async () => {
        await Api.discardPrepare({ id: record.id })
        $table.search()
        message.success('取消成功！')
      },
    })
  }

  const submitProcess = async (record) => {
    if (['RatingClientUpdateFlow'].includes(record.processType)) {
      history.push(`/process/application/detail/${record.id}?tab=prepare&typeId=approval`)
      return
    }
    if (['ProjectProfitSharingFlow'].includes(record.processType)) {
      Modal.confirm({
        title: '确认提交吗？',
        onOk: async () => {
          await Api.submitProjectDistribution({ projectDistributionId: record.businessId })
          message.success('提交成功！')
          history.push(`/process/application?t=${Date.now()}`)
        },
      })
      return
    }
    if (['FundFilingMaterialsApplyFlow'].includes(record.processType)) {
      const validate = await Api.checkFilingMaterialFile({ id: record.businessId })
      if (validate) {
        Modal.confirm({
          title: (
            <>
              当前已上传材料为
              <span style={{ fontWeight: 'bold' }}>最终归档内容</span>
              ，该流程提交后再上传的资料将不再纳入归档，请确认！
            </>
          ),
          okText: '继续提交',
          onOk: async () => {
            await Api.commitPrepare({ id: record.id })
            message.success('提交成功！')
            history.push(`/process/application?t=${Date.now()}`)
          },
        })
      }
      return
    }
    if (['ContractStartRentAutoFlow'].includes(record.processType)) {
      const valid = await checkIrr({ commitPrepareId: record.id },false)
    }
    Modal.confirm({
      title: '确认提交吗？',
      onOk: async () => {
        await Api.commitPrepare({ id: record.id })
        $table.search()
        message.success('提交成功！')
      },
    })
  }

  return (
    <Table
      columnsFilter="processApplicationWait"
      onFilter={(key, val) => saveServer('processApplicationWait', val)}
      columnWidth={180}
      store={$table}
      columns={[
        ...columns,
        {
          title: '操作',
          width: 200,
          fixed: 'right',
          actions (record) {
            const submitDisabled = ['FinanceOverdue'].includes(record.processType)
            const closeDisabled = ['DirectFinancingRecordFlow', 'FinanceOverdue'].includes(
              record.processType
            )

            return [
              {
                name: '提交审批',
                access: 'process_prepare_commit',
                disabled: submitDisabled,
                onClick: () => submitProcess(record),
              },
              {
                name: '关闭流程',
                access: 'process_prepare_discard',
                disabled: closeDisabled,
                onClick: () => cancelProcess(record),
              },
            ]
          },
        },
      ]}
      editable={false}
      searchbar={{
        items: formColumns,
      }}
      scroll={{ x: 1200 }}
    />
  )
}
export default Index
