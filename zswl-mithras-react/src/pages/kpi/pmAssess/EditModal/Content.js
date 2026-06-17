import { observer } from '@zswl/admin'
import { useMemo, useState, useEffect } from 'react'
import { TableStore } from '@zswl/components'
import BeautyTable from '@/pages/kpi/Component/Table'
import { message } from 'antd'
import mathjs from '@/utils/math'
import Api from '@/api/kpi/pmAssess'
import { TABLE_TITLE_DATA, render, editable } from './utils'
import styles from './index.less'

function Index({ businessKey, businessVersion, canEditFlags = 'true' }) {
  const canEdit = canEditFlags === 'true'
  const [editIndex, setEditIndex] = useState(-1)

  const $table = useMemo(() => {
    return new TableStore({
      request: async ({ id }) => {
        const data = await Api.getDetail({ id, businessVersion })
        const list = data?.projectManagerScoreList
        list.unshift(...TABLE_TITLE_DATA)
        return list
      },
      pagination: false,
    })
  }, [])

  const confirmEdit = async ({ record }) => {
    const { values } = await $table.submit()
    const editData = values[record.id]
    await Api.saveItem({
      id: record.id,
      ...editData,
    })
    message.success('保存成功')
    $table.search({ id: businessKey })
    setEditIndex(-1)
  }

  useEffect(() => {
    if (businessKey) {
      $table.search({ id: businessKey })
    }
  }, [businessKey])

  return (
    <div className={styles.beautyTable}>
      <BeautyTable
        scroll={{ x: 1300 }}
        autoRequest={false}
        store={$table}
        columnWidth={140}
        columns={[
          {
            title: '指标类型',
            width: 120,
            dataIndex: 'userName',
          },
          {
            title: '部门综合考评得分',
            dataIndex: 'deptPerformanceScore',
            width: 150,
            editable: (record, rowIndex) => {
              return editable(record, rowIndex, editIndex)
            },
            render,
          },
          {
            title: '任务性指标完成情况',
            width: 120,
            colSpan: 2,
            dataIndex: 'competentScore',
            editable: (record, rowIndex) => {
              return editable(record, rowIndex, editIndex)
            },
            render,
          },
          {
            title: '任务性指标完成情况',
            width: 120,
            dataIndex: 'marketingChannelScore',
            colSpan: 0,
            editable: (record, rowIndex) => {
              return editable(record, rowIndex, editIndex)
            },
            render,
          },
          {
            title: '质量与内控',
            width: 120,
            dataIndex: 'defectRateScore',
            colSpan: 0,
            editable: (record, rowIndex) => {
              return editable(record, rowIndex, editIndex)
            },
            render,
          },
          {
            title: '质量与内控',
            width: 120,
            dataIndex: 'overdueRateScore',
            colSpan: 0,
            editable: (record, rowIndex) => {
              return editable(record, rowIndex, editIndex)
            },
            render,
          },
          {
            title: '质量与内控',
            dataIndex: 'focusRadioScore',
            width: 190,
            colSpan: 0,
            editable: (record, rowIndex) => {
              return editable(record, rowIndex, editIndex)
            },
            render,
          },
          {
            title: '质量与内控',
            dataIndex: 'afterLeaseScore',
            colSpan: 4,
            width: 120,
            editable: (record, rowIndex) => {
              return editable(record, rowIndex, editIndex)
            },
            render,
          },
          {
            title: '合计',
            dataIndex: 'total',
            width: 100,
            render: (value, record, rowIndex) => {
              if (rowIndex === 1) return 100
              if (rowIndex > 1) {
                const {
                  deptPerformanceScore,
                  competentScore,
                  marketingChannelScore,
                  afterLeaseScore,
                  focusRadioScore,
                  overdueRateScore,
                  defectRateScore,
                } = record
                const result = mathjs
                  .chain(deptPerformanceScore ?? 0)
                  .add(competentScore ?? 0)
                  .add(marketingChannelScore ?? 0)
                  .add(afterLeaseScore ?? 0)
                  .add(focusRadioScore ?? 0)
                  .add(overdueRateScore ?? 0)
                  .add(defectRateScore ?? 0)
                  .done()
                return mathjs.format(result)
              }
              return ''
            },
          },
          {
            title: '操作',
            width: 120,
            actions(record, rowIndex) {
              return [
                editIndex !== rowIndex &&
                  rowIndex > 1 && {
                    name: '编辑',
                    key: 'edit',
                    disabled: !canEdit,
                    onClick: () => {
                      setEditIndex(rowIndex)
                    },
                  },
                editIndex === rowIndex && {
                  name: '取消',
                  key: 'cancel',
                  disabled: !canEdit,
                  onClick: () => {
                    setEditIndex(-1)
                  },
                },
                editIndex === rowIndex && {
                  name: '确定',
                  key: 'confirm',
                  disabled: !canEdit,
                  onClick: () => confirmEdit({ record, rowIndex }),
                },
              ].filter(Boolean)
            },
          },
        ]}
      />
    </div>
  )
}

export default observer(Index)
