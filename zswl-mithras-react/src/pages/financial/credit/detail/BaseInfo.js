import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '../Column'
import { getDescColumns, rules } from '@/utils'
import { observer } from '@zswl/admin'
import { useMemo, useRef } from 'react'
import { AmountColumn, MatchOptionColumn } from '@/components/Format'
import { App, Select } from '@zswl/components'

function Index({ detail, saveData, isLog, canEdit = true, newProject }) {
  const initEdit = newProject === 'true'
  const isUsed = detail.usedTotalCreditAmount > 0

  const nameColumns = [
    // '授信项目',
    '授信编号',
    '授信机构',
    {
      title: '银行联行号',
      rename: (
        <div>
          <div>银行联行号</div>
          <div>统一社会信用代码</div>
        </div>
      ),
      dataIndex: 'organizationCode',
    },
    AmountColumn({
      title: '总授信额度（元）',
      dataIndex: 'totalCreditLimit',
      rename: '授信额度(元)',
      editable: true,
      wrapItemProps: {
        required: true,
        disabled: isUsed,
      },
    }),
    {
      title: '增信方式',
      dataIndex: 'enhanceCreditMethod',
      matchOption: 'enhanceCreditMethod',
      requiredMark: true,
      editable: {
        element: (
          <Select options={'enhanceCreditMethod'} mode="multiple" allowClear disabled={isUsed} />
        ),
        rules: [{ required: true, message: '请选择增信方式' }],
      },
      render: (val) => {
        return val?.map((v) => App.matchOption('enhanceCreditMethod', v).label).join('、')
      },
    },
    MatchOptionColumn({
      title: '额度是否可循环',
      dataIndex: 'recyclable',
      matchOption: 'yesOrNo',
      disabled: isUsed,
      requiredMark: true,
      editable: true,
    }),
    // '资金用途',
    { title: '担保方' },
    '授信生效时间',
    '备注',
    { title: '资金经理', editable: false },
    { title: '部门', editable: false },
    { title: '部门负责人', editable: false },
    { title: '分管领导', editable: false },
  ]
  const columns = getDescColumns(ALL_COLUMNS, nameColumns)

  const ref = useRef()
  return (
    <EditDescription
      detail={{
        ...detail,
        organizationId: {
          label: detail?.organizationName,
          value: detail?.organizationId,
        },
      }}
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
