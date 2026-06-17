import { ClientSelect } from '@/components'
import { Select } from '@zswl/components'

export const COMMON_COLUMNS = [
  { title: '编号', width: 180, dataIndex: 'paymentApplyCode' },
  {
    title: '客户名称',
    width: 140,
    dataIndex: 'clientName',
  },
  {
    title: '审批状态',
    width: 130,
    dataIndex: 'approvalStatus',
    editable: {
      element: <Select options={'crApprovalStatus'} />,
    },
  },
  {
    title: '是否报送',
    width: 130,
    dataIndex: 'reportFlag',
    editable: {
      element: (
        <Select
          options={[
            {
              label: '是',
              value: 1,
            },
            {
              label: '否',
              value: 0,
            },
          ]}
        />
      ),
    },
  },
  {
    title: '五级分类',
    width: 130,
    dataIndex: 'fiveClass',
    editable: {
      element: <Select options={'crFiveClass'} />,
    },
  },
]

export const ClientEditable = (params = {}) => {
  const { required = true, disabled = true, ...rest } = params
  return {
    element: <ClientSelect canJump={false} />,
    functionCode: 'clientlist-groupCreditReview',
    ...rest,
  }
}
