import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useRef } from 'react'

function Index({ detail, saveData, isLog, canEdit = true, initEdit, isDetail }) {
  const nameColumns = [
    '合同编号',
    isDetail ? '客户名称' : '本次申请限额(元)',
    '项目名称',
    '项目编号',
    '业务类型',
    '保理类型',
    '风控行业分类',
    '项目分类',
    '项目来源',
    '资金用途',
    '项目背景',
    '备注',
    '项目主办',
    '项目协办',
    '业务部门',
    '业务部门负责人',
    '业务分管领导',
  ]
  const columns = getDescColumns(ALL_COLUMNS(), nameColumns)
  const ref = useRef()
  return (
    <EditDescription
      detail={detail}
      saveData={saveData}
      canEdit={canEdit}
      isLog={isLog}
      initEdit={initEdit}
      columns={columns}
      ref={ref}
    />
  )
}

export default observer(Index)
