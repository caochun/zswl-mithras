import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from './Column'
import { getDescColumns, hasValue } from '@/utils'
import { observer } from '@zswl/admin'
import moment from 'moment'
import { message } from 'antd'
import { useRef } from 'react'

function Index({ detail, saveData, isLog, canEdit = true, initEdit }) {
  const editRef = useRef()
  const nameColumns = [
    {
      title: '原到期日',
      editable: false,
    },
    '申请结清日',
    '到期未付租金(元)',
    '未到期本金(元)',
    '未到期利息(元)',
    '提前终止补偿金(元)',
    '违约金(元)',
    '保证金余额(元)',
    '保证金是否内扣',
    '名义价款(元)',
    '申请减免金额(元)',
    {
      title: '合计金额(元)-提前结清',
      rename: '合计金额(元)',
    },
    '提前结清说明',
  ]

  const calcLoss = () => {
    const formValues = editRef.current.form.getFieldsValue(true)
    const { applySettleDate } = formValues
    const { beforeMaturityPrincipal, originalDeadline } = detail

    let getLoss = ''
    if (originalDeadline && applySettleDate && hasValue(beforeMaturityPrincipal)) {
      const diffDays = moment(originalDeadline).diff(applySettleDate, 'days')
      getLoss = ((((beforeMaturityPrincipal / 10000) * 0.02) / 360) * diffDays).toFixed(2)
      editRef.current.form.setFieldsValue({
        loss: getLoss,
      })
    } else {
      message.info('请先填写申请结清日、未到期本金')
    }
  }
  const columns = getDescColumns(ALL_COLUMNS({ detail, calcLoss }), nameColumns)
  return (
    <EditDescription
      ref={editRef}
      title="结清方案"
      detail={detail}
      saveData={saveData}
      canEdit={canEdit}
      isLog={isLog}
      initEdit={initEdit}
      columns={columns}
    />
  )
}

export default observer(Index)
