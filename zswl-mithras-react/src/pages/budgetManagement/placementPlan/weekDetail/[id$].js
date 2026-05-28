import React, { useState, useEffect, useMemo } from 'react'
import { getQuery, observer } from '@zswl/admin'
import { Page, Table, Checkbox, Button, Modal, Form, Select, Access } from '@zswl/components'
import { Row, Col, Space, DatePicker } from 'antd'
import DepartmentSelector from '../components/DepartmentSelector'
import styles from './style.less'
import Store from './store.ts'
import { ProjectReviewSelect } from '@/components'
import TableExport from '@/components/Actions/TableExport'
import ProjectMonthTable from './ProjectMonthTable'
import StatisticCard from '../components/StatisticCard'
import deliveryPlanDetailApi from '@/api/budgetManagement/deliveryPlanDetailApi'

const PlacementPlanDetail = observer(({ params, query }) => {
  const { id, taskActivityId, processName, deptId } = params
  const store = useMemo(() => new Store({ id }), [id])
  const { dateFrom, dateTo, planStatus } = store.page.getData() ?? {}

  const { departmentList, statistics } = store
  const [selectedDepartment, setSelectedDepartment] = useState('')
  const { keys } = store?.projectTable?.getSelected()
  const canEditStatus = ['COLLECTING', 'CONFIRM'].includes(planStatus)
  const canEditTaskActivityId = ['userTask_projectmanager', 'userTask_deptMaster'].includes(
    taskActivityId
  )
  const isRevocation = getQuery('tab') == 'revocation'

  const isFormApproval = getQuery('typeId') == 'approval'
  const canEdit = isFormApproval
    ? (canEditStatus && canEditTaskActivityId) || isRevocation
    : canEditStatus

  const handleDepartmentChange = (value) => {
    setSelectedDepartment(value)
    store.projectTable.setParams({ deptId: value })
    store.projectTable.search()
  }
  const deleteDisabled = keys?.length === 0

  return (
    <Page store={store} params={{ id }} bodyStyle={{ position: 'relative' }}>
      <div className={styles.header}>
        <Space>
          {canEdit && (
            <Button type="primary" onClick={store.addItem}>
              新建项目
            </Button>
          )}

          {canEdit && (
            <Button onClick={store?.itemDelete} disabled={deleteDisabled}>
              删除项目
            </Button>
          )}
        </Space>
        <TableExport table={store?.projectTable} otherExcelProps={{ fileName: '投放计划列表' }} />
      </div>
      <div className={styles.container}>
        {/* 提示信息 */}
        <div className={styles.reportPeriod}>
          <img className={styles.icon} src="/public/assets/budgetManage/time.png" />
          填报期间: {dateFrom} ~ {dateTo}
        </div>

        {/* 统计数据 */}
        <Access value="budgetplanweeklyreportdetailstatistics">
          <div className={styles.statisticsRow}>
            {store.statistics.map((item, index) => (
              <StatisticCard
                title={item.title}
                value={item.value}
                unit={item.unit}
                initFormat={item.initFormat}
              />
            ))}
          </div>
        </Access>

        {/* 部门选择器 */}
        <DepartmentSelector
          value={selectedDepartment}
          onChange={handleDepartmentChange}
          departments={departmentList}
          needLeader={false}
          needBusinessHead={!deptId}
        />

        <ProjectMonthTable store={store} canEdit={canEdit} />
        <Modal title="新建项目" store={store?.editModal} destroyOnClose>
          <Form>
            <Form.Item label="项目名称" name="projReviewId">
              <Select
                options={() =>
                  deliveryPlanDetailApi.postProjreviewList({ belongDeptId: deptId }).then((res) =>
                    res.map((item) => ({
                      label: item.key,
                      value: item.value,
                    }))
                  )
                }
                getPopupContainer={() => document.body}
              />
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </Page>
  )
})

export default PlacementPlanDetail
