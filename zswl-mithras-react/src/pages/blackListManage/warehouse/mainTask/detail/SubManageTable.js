import { Table, Button, Upload } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Format } from '@/components/RiskActions'
import { Card, Popover } from 'antd'
import RetractModal from './RetractModal'
import styles from './styles.less'
import { saveServer } from '@/utils'

function SubManageTable({ type, store }) {
  const { rows } = store.subManageTable.getSelected()
  //附件Popover
  const getContent = (value) => {
    return value?.length > 0 ? <Upload.List value={value} /> : '暂无数据'
  }
  //退回
  const canOperation =
    rows.length >= 1 &&
    rows.every((item) => {
      return item.canRetract
    })
  //关闭
  const canClose =
    rows.length >= 1 &&
    rows.every((item) => {
      return item.canClose
    })
  return (
    <Card title="子任务列表" size={'small'} className={styles.detailTable}>
      <Table
        columnsFilter={'mainTask_detail_SubManageTable'}
                onFilter={(key,val) => saveServer('mainTask_detail_SubManageTable',val)}
        
        serial
        rowSelection={type}
        store={store.subManageTable}
        pagination={false}
        columns={[
          { title: '子任务编号', dataIndex: 'subTaskCode', width: 180 },
          {
            title: '下发时间',
            dataIndex: 'assignTime',
            width: 150,
          },
          { title: '所属机构', dataIndex: 'deptName', width: 100 },
          {
            title: '审批状态',
            dataIndex: 'auditStatus',
            render: (v, { auditStatus, latestMsg }) => (
              <Format.ApprovalStatus
                value={auditStatus}
                suggest={latestMsg}
                status="subTaskStatus"
              />
            ),
            width: 100,
          },
          {
            title: '当前处理人',
            dataIndex: 'currentOperator',
            render: (val) => <Format.User value={val} />,
            width: 120,
          },
          { title: '更新时间', dataIndex: 'gmtUpdate', width: 140 },
          {
            title: '操作',
            dataIndex: 'fileKeys',
            width: 80,
            render: (val) => {
              return (
                <Popover content={() => getContent(val)} placement="left" title="附件预览">
                  <div style={{ fontSize: '14px', color: '#2558E6' }}>预览</div>
                </Popover>
              )
            },
          },
        ]}
        actions={
          type && [
            <Button.Disable
              type="primary"
              key="withdraw"
              disabled={!canClose}
              onClick={store.subManageClose}
            >
              关闭
            </Button.Disable>,
            <Button.Reset key="back" disabled={!canOperation} onClick={store.retractModal.open}>
              退回
            </Button.Reset>,
          ]
        }
      />
      <RetractModal store={store} />
    </Card>
  )
}

export default observer(SubManageTable)
