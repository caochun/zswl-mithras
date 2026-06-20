import { useMemo, useState } from 'react'
import { Table, Button, ModalStore } from '@zswl/components'
import { Checkbox, message, Space } from 'antd'
import { observer, history } from '@zswl/admin'
import ALL_COLUMNS from '../../../CheckPlanColumns'
import styles from '../index.less'
import { getTableColumns, getUserInfo, isAssetJon, saveServer } from '@/utils'
import EditCheckTime from './EditCheckTime'
import EditCheckWay from './EditCheckWay'
import api from '@/api/afterLease/checkPlanDetailApi'

const Index = ({
  dataSource,
  canEditFlag,
  detail,
  toCheckCount,
  bizDeptId,
  store,
  getList,
  canEditFlags,
}) => {
  const { onCuiBan } = store
  const [selectedList, setSelectedList] = useState([])
  const finishCount = dataSource.filter((item) => item.approvalStatus === 'APPROVAL_PASS').length
  const overtime = (item) =>
    moment().diff(moment(item.checkTime), 'days') > 10 &&
    ['CANCELED', 'UN_SUBMIT'].includes(item.approvalStatus) &&
    item.checkWay === 'SITE'
  const overtimeCount = dataSource.filter(overtime).length
  const jumpDetail = ({ id, planStatus, approvalStatus }) => {
    if (
      planStatus !== 'FINISH' ||
      (planStatus === 'FINISH' && approvalStatus === 'APPROVAL_PASS')
    ) {
      history.push(`/afterLease/checkPlan/template/${id}`)
    } else {
      message.warn('只有审批通过才能查看详情')
    }
  }

  const nameColumns = [
    '客户编号',
    {
      title: '客户名称',
      dataIndex: 'clientName',
      render: (value, record) => {
        return <a onClick={() => jumpDetail(record)}>{value}</a>
      },
    },
    '客户类型',
    '客户主办',
    '检查形式',
    '检查报告模版',
    '协查风控经理',
    { title: '检查日期', dataIndex: 'checkTime' },
    '审批状态',
  ]
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)

  const editCheckWayStore = useMemo(() => {
    return new ModalStore({
      onOpen: (data) => {
        return {
          ...data,
          riskManagerId: data?.riskManagerId && `${data.riskManagerId}`,
        }
      },
      onFinish: async (data) => {
        await api.postProjectModify(data).finally(() => {
          setSelectedList([])
        })
        message.success('修改成功')
        getList()
        editCheckWayStore.close()
      },
    })
  }, [])

  const editCheckTimeStore = useMemo(() => {
    return new ModalStore({
      onOpen: ({ checkTime, riskManagerId, ...rest }) => {
        return {
          checkTime: checkTime && moment(checkTime),
          riskManagerId: riskManagerId && `${riskManagerId}`,
          ...rest,
        }
      },
      onFinish: async (data) => {
        await api.postProjectModify(data)
        message.success('修改成功')
        getList()
        editCheckTimeStore.close()
      },
    })
  }, [])
  const [isOvertime, setIsOvertime] = useState(false)
  const onChange = (e) => {
    setIsOvertime(e.target.checked)
  }
  const tableData = useMemo(() => {
    if (!isOvertime) return dataSource
    return dataSource.filter(overtime)
  }, [isOvertime, dataSource])

  const handleTaskAdjust = () => {
    if (selectedList && selectedList.length === 1) {
      editCheckWayStore.open(selectedList[0])
    } else {
      message.info('需选中单条记录来处理')
    }
  }

  const hasAuthTaskAdjust = useMemo(() => {
    if (selectedList && selectedList.length > 0) {
      const isUnSubmit = selectedList.some((item) =>
        ['UN_SUBMIT', 'CANCELED'].includes(item.approvalStatus)
      )
      return isUnSubmit && isAssetJon()
    } else {
      return false
    }
  }, [selectedList])

  return (
    <>
      <div className={styles.subTitle}>
        <div className={styles.total}>
          已完成：
          {finishCount}
          个，未完成：
          {toCheckCount - finishCount}个， 已超时：
          {overtimeCount}个
        </div>
        <div className={styles.actionWrap}>
          <Space>
            <Checkbox onChange={onChange} checked={isOvertime}>
              超时未提交
            </Checkbox>
            <Button type="primary" onClick={handleTaskAdjust} disabled={!hasAuthTaskAdjust}>
              任务调整
            </Button>
            <Button type="primary" onClick={onCuiBan} disabled={!canEditFlag}>
              一键催办
            </Button>
            <Button
              onClick={(data) => {
                return store.handleDownBtn(data, {
                  selectedList: selectedList.map((item) => item.id),
                  deptId: bizDeptId,
                })
              }}
              items={[
                {
                  name: '下载选中项报告',
                  key: 'downDeptChooseReport',
                },
                detail.planType === 'QUARTER' && {
                  name: '下载该部门报告',
                  key: 'downDeptReport',
                },
              ].filter(Boolean)}
            >
              批量下载
            </Button>
          </Space>
        </div>
      </div>
      <Table
        resizable
        scroll={{
          x: 1800,
        }}
        columnWidth={180}
        columnsFilter={'Project_TableList_1'}
        onFilter={(key, val) => saveServer('Project_TableList_1', val)}
        dataSource={tableData}
        rowSelection={{
          type: 'checkbox',
          selectedRowKeys: selectedList.map((item) => item.id),
          onChange: (_, selectedRows) => {
            setSelectedList(selectedRows)
          },
        }}
        columns={[
          ...columns,
          canEditFlags === 'true' && {
            title: '操作',
            width: 120,
            fixed: 'right',
            isAction: true,
            render: (record) => {
              // 已取消、未提交
              const isUnSubmit = ['UN_SUBMIT', 'CANCELED'].includes(record.approvalStatus)
              const isRiskManager = +record.riskManagerId === +getUserInfo().id
              // 能编辑，未提交，资产管理岗位、风控经理，现场检查
              return (
                <Space>
                  <Button
                    type="link"
                    onClick={() => editCheckTimeStore.open(record)}
                    disabled={!isUnSubmit || !isRiskManager}
                  >
                    编辑
                  </Button>
                </Space>
              )
            },
          },
        ].filter(Boolean)}
      ></Table>
      <EditCheckTime store={editCheckTimeStore} />
      <EditCheckWay store={editCheckWayStore} />
    </>
  )
}

export default observer(Index)
