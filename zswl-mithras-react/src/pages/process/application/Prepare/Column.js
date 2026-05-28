import { SearchBar } from '@zswl/components'
import ProcessTypeTree from '@/pages/process/components/ProcessTypeTree'
import { Tag, Space } from 'antd'
const { Item } = SearchBar

const ALL_COLUMNS = () => {
  return [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 120,
      fixed: 'left',
      actions(value) {
        if (['FinanceOverdue'].includes(value.processType)) {
          return [
            {
              name: (
                <Space>
                  {value.id}
                  {value.overtimeFlag === 1 && <Tag color="red">已超时</Tag>}
                </Space>
              ),
              to: `/budget/accountsReceivable?type=create&applicationId=${value.id}`,
            },
          ]
        }
        return [
          {
            name: (
              <Space>
                {value.id}
                {value.overtimeFlag === 1 && <Tag color="red">已超时</Tag>}
              </Space>
            ),
            to: `/process/application/detail/${value.id}?tab=prepare&typeId=approval`,
          },
        ]
      },
    },
    {
      title: '流程类型',
      dataIndex: 'processTypeName',
      width: 240,
      editable: {
        element: (
          <Item name="processTypeList" label="">
            <ProcessTypeTree></ProcessTypeTree>
          </Item>
        ),
      },
      render: (value, { processType, isAssetConfirm, overtimeFlag }) => {
        return processType === 'NewAfterLeaseCheckPlanPublishCreateFlow' ? (
          <Space>
            {value}
            {isAssetConfirm && <Tag color="#87d068">{isAssetConfirm}</Tag>}
          </Space>
        ) : (
          <span style={{ color: overtimeFlag == 1 ? 'red' : undefined }}>{value}</span>
        )
      },
    },
    {
      title: '表单名称',
      dataIndex: 'formName',
      width: 300,
      render: (val, record) => {
        return (
          <span style={{ color: record.overtimeFlag == 1 ? 'red' : undefined }}>{val}</span>
        )
      },
    },
    {
      title: '项目名称',
      dataIndex: 'projName',
      width: 220,
    },
    {
      title: '项目编号',
      dataIndex: 'projCode',
      width: 140,
    },
    {
      title: '客户名称',
      dataIndex: 'clientName',
      width: 200,
    },
    {
      title: '当前节点',
      dataIndex: 'currentNode',
    },
    {
      title: '当前审批人',
      dataIndex: 'currentAssigneeNames',
    },
    {
      title: '申请时间',
      dataIndex: 'applyTime',
    },
  ]
}

export default ALL_COLUMNS
