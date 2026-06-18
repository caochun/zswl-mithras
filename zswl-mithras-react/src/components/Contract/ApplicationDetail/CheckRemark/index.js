import { EditDescription } from '@/components'
import { Input } from 'antd'

const items = [
  {
    title: '变更明细',
    dataIndex: 'changeRemark',
    requiredMark: true,
    editable: {
      element: <Input.TextArea rows={4} />,
      rules: [{ required: true, message: '请输入变更明细' }],
    },
  },
]
function CheckRemark(props) {
  const { detail, saveData, canEdit, initEdit, isFormApproval } = props
  return (
    <>
      {isFormApproval ? (
        <EditDescription
          title={'变更详情'}
          detail={detail.newDetail}
          saveData={saveData}
          canEdit={canEdit}
          isLog={detail.isLog}
          initEdit={initEdit}
          columns={items}
        />
      ) : (
        <EditDescription
          title={'变更详情'}
          detail={detail.detail}
          saveData={saveData}
          canEdit={canEdit}
          isLog={detail.isLog}
          initEdit={initEdit}
          columns={items}
        />
      )}
    </>
  )
}

export default CheckRemark
