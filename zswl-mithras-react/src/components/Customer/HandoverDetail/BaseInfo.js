import { EditDescription } from '@/components/Table'
import { FiledFormat, TextAreaEditable, DatePickerEditable } from '@/components/Format'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useRef, forwardRef, useImperativeHandle } from 'react'
import moment from 'moment'

const ALL_COLUMNS = [
  {
    title: '原所属主办',
    dataIndex: 'belongSponsorName',
    editable: false,
  },
  {
    title: '客户当前所属部门',
    dataIndex: 'belongDeptName',
    editable: false,
  },
  {
    title: '正式移交日期',
    dataIndex: 'transferDate',
    editable: (val) =>
      DatePickerEditable(val, 'transferDate', {
        required: true,
        disabled: false,
        disabledDate: (current) => {
          return current && current < moment().startOf('day')
        },
      }),
    requiredMark: true,
    span: 2,
  },
  {
    title: '说明',
    dataIndex: 'description',
    span: 2,
    requiredMark: true,
    editable: TextAreaEditable({ required: true }),
    render: (val) => <FiledFormat title={val} hasToolTip={false} />,
  },
]

function CustomerHandoverBaseInfo({ store, canEdit }, ref) {
  const columns = getDescColumns(ALL_COLUMNS)
  const detail = store.page.getData()
  const { isFormApproval } = store.page.getParams()
  const editDescRef = useRef()

  useImperativeHandle(ref, () => ({
    getEditDescForm: () => {
      return editDescRef.current?.form
    },
  }))

  return (
    <EditDescription
      title=" "
      ref={editDescRef}
      initEdit={!isFormApproval}
      detail={detail}
      canEdit={isFormApproval && canEdit}
      columns={columns}
      saveData={store.saveData}
    />
  )
}

export default observer(forwardRef(CustomerHandoverBaseInfo))
