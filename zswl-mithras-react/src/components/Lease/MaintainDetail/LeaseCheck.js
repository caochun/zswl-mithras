import EditDescription from '@/components/Table/EditDescription'
import { DatePickerEditable } from '@/components/Format'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import Api from '@/api/lease/maintainApi'
import { hasPermission } from '@/utils'

function Index({ id, canEdit = true, isLog }) {
  const [detail, setDetail] = useState({})

  const getData = async () => {
    const res = await Api.postCheckLeaseDetail({ id })
    setDetail({
      checkRepeatDate: res?.repeatDate,
    })
  }
  const saveData = async (value) => {
    await Api.postCheckLeaseModify({ id, ...value })
    getData()
  }
  useEffect(() => {
    getData()
  }, [id])

  return (
    <EditDescription
      title="租赁物查重"
      detail={detail}
      saveData={saveData}
      canEdit={canEdit && hasPermission('ledgerDetailCheckRepeatSave')}
      isLog={isLog}
      columns={[
        {
          title: '中登网查重日期',
          dataIndex: 'checkRepeatDate',
          requiredMark: true,
          editable: (record, rowIndex) => {
            return DatePickerEditable(record, 'checkRepeatDate', {
              required: true,
            })
          },
          render: (value) => {
            return value ?? '-'
          },
        },
      ]}
    />
  )
}

export default observer(Index)
