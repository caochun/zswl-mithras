import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '@/components/Financial/FundColumns'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useEffect, useMemo, useRef, useState } from 'react'
import Api from '@/api/financial/fundApi'
import { message } from 'antd'
import { Page, PageStore } from '@zswl/components'

const nameColumns = [
  // { title: '融资机构', editable: false },
  { title: '融资编号', editable: false },
  { title: '总授信额度(元)', editable: false },
  { title: '剩余授信额度(元)', editable: false },
  { title: '融资期限类型', editable: false },
  { title: '业务类型', editable: false },
  { title: '资金用途', editable: false },
  { title: '增信方式：担保', editable: false },
  { title: '备注', editable: true },
  { title: '担保方', editable: false },
  { title: '是否期初一次性收息', editable: false },
  { title: '资金经理(基本信息)', rename: '资金经理', editable: false },
  { title: '所属部门', editable: false },
  { title: '部门负责人', editable: false },
  { title: '分管领导', editable: false },
]

const columns = getDescColumns(ALL_COLUMNS, nameColumns)

function Index({ detail, saveData, isLog, canEdit = true, initEdit }) {
  const ref = useRef()

  return (
    <EditDescription
      detail={detail}
      saveData={saveData}
      isLog={isLog}
      initEdit={initEdit}
      columns={columns}
      canEdit={canEdit}
      ref={ref}
      style={{ marginBottom: 12 }}
    />
  )
}

export default observer(Index)
