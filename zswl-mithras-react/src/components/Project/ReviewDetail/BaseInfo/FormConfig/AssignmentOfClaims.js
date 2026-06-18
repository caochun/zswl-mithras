
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useMemo, useRef, useState } from 'react'
import {
  isShowProjRegionalDivision,
  onRegionalClassifyChange,
  onEvaluateMainChange,
} from './Context'
import UpdateButton from '@/components/UpdateRatingInfoButton'

function Index({ detail, store, saveData, isLog, canEdit = true, initEdit }) {
  const editDescRef = useRef({})

  const form = editDescRef.current?.form

  const nameColumns = [
    '项目名称',
    '项目编号',
    '业务类型',
    '转让类型',
    '风控行业分类',
    '资金用途',
    '项目来源',
    '评估主体',
    '评估主体区域',
    '行业分类',
    '地区分类',
    '评估主体评级',
    '债项评级参考额度（万元）',
    'FTP 行业分类',
    {
      title: '区域划分',
      span: 2,
    },
    '项目背景',
    '备注',
    '债权人',
    '债务人',
    '担保人',
    '抵押人',
    '质押人',
    '供应商',
    '项目主办',
    '项目协办',
    '业务部门',
    '业务部门负责人',
    '业务分管领导',
    '风控经理',
    // '法务经理',
  ].filter(Boolean)

  const columns = useMemo(() => {
    return getDescColumns(
      ALL_COLUMNS({
        detail,
        store,
        form,
        isShowProjRegionalDivision: isShowProjRegionalDivision(detail),
        onRegionalClassifyChange: () => onRegionalClassifyChange(form),
        onEvaluateMainChange: (value) => {
          onEvaluateMainChange(value, form)
        },
      }),
      nameColumns
    )
  }, [form, detail, store?.evaluationSubjectIdValue])

  return (
    <EditDescription
      ref={editDescRef}
      detail={detail}
      saveData={saveData}
      canEdit={canEdit}
      isLog={isLog}
      initEdit={initEdit}
      columns={columns}
      cancelExtra={<UpdateButton detail={detail} updateInfo={store?.updateInfo} />}
    />
  )
}

export default observer(Index)
