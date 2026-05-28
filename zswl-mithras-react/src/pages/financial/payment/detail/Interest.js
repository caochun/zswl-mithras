import { observer } from '@zswl/admin'
import { EditTable } from '@/components'
import ALL_COLUMNS from '../Column'
import { getTableColumns, hasPermission } from '@/utils'
import fundReceiptRepayCashFlowApi from '@/api/financial/fundReceiptRepayCashFlowApi'
import styles from './index.less'
import { useCallback, useState } from 'react'

const nameColumns = [
  '现金流编号',
  { title: '期项', dataIndex: 'phase' },
  '计划还款日',
  { title: '本金（元）', dataIndex: 'principleAmount' },
  { title: '利息（元）', dataIndex: 'interestAmount' },
  '核销状态',
  '备注',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns) //
const Index = ({ id: receiptRepayId, businessVersion, canEdit, isFormApproval, detail }) => {
  const [init, setInit] = useState(true)
  const getList = useCallback(
    async (params) => {
      if (detail) return Promise.resolve({ list: detail })
      const listFunc = isFormApproval
        ? fundReceiptRepayCashFlowApi.postFlowListCompare
        : fundReceiptRepayCashFlowApi.postFlowList
      if (init) {
        setInit(false)
      }
      return await listFunc({
        ...params,
        page: init ? -1 : params.page,
        receiptRepayId,
        version: isFormApproval ? businessVersion : undefined,
      })
    },
    [init]
  )
  const saveData = async (data, values) => {
    const params = Object.entries(values).map(([key, value]) => ({
      id: key,
      ...value,
    }))
    return await fundReceiptRepayCashFlowApi.postFlowModify(params)
  }
  return (
    <EditTable
      title={'本金和利息一览表'}
      tableApi={getList}
      saveData={saveData}
      canEdit={canEdit && hasPermission('fundReceiptRepayCashFlowModify')}
      columns={columns}
      rowClassName={(record) => {
        const { isRed } = record
        const isRedValue = record.isRed?.value ?? record.isRed
        return isRedValue ? styles.isRed : ''
      }}
    />
  )
}
export default observer(Index)
