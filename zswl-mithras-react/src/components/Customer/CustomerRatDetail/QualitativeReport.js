import { getQuery, observer } from '@zswl/admin'
import { App, Table, TableStore } from '@zswl/components'
import { AmountColumn, MatchOptionColumn, TextAreaColumn } from '@/components/Format'
import { Input, Radio, Tooltip } from 'antd'
import { options } from '@/utils'
import { useMemo } from 'react'
import styles from './styles.less'
import { FormAmount } from '@/components/Form'
import { saveServer } from '@/utils'

const { approvalStatus } = options

const Index = ({ list = [], canApproval, ratingScoreRSP = {}, store, isZX, hymx }) => {
  const isApproval = getQuery('typeId') == 'approval'
  const reportTable = useMemo(() => new TableStore({ request: () => list }), [list])
  const hasApprovalOption = list.some((item) => item?.approvalStatus)
  const isShow = isApproval && (canApproval ? true : hasApprovalOption)
  const columns = [
    { title: '指标名称', dataIndex: 'fieldComment', editable: false, width: 120 },

    (isZX || hymx) && {
      title: '取数方式',
      width: 120,
      dataIndex: 'fetchMethod',
      matchOption: 'ratingFetchMethodEnum',
      editable: false,
      render: (value) => App.matchOption('ratingFetchMethodEnum', value)?.label ?? '手工录入',
    },
    {
      title: (isZX || hymx) ? '指标值/指标档位' : '指标档位',
      width: 90,
      dataIndex: 'value',
      editable: false,
      render: (value, record) => {
        let { isChange, unit } = record
        const title = ![null, undefined].includes(value) ? `${value} ${unit ?? ''}` : '-'
        if (value === -9999999999999) return '-'
        if (record.dataType == 'number') {
          return (
            <FormAmount.Format
              value={value}
              suffix={unit}
              initFormat={1}
              style={{ textAlign: 'right' }}
            />
          )
        }
        return (
          <Tooltip title={title}>
            <div style={{ color: isChange ? 'red' : undefined, textAlign: 'right' }}> {title}</div>
          </Tooltip>
        )
      },
    },
    {
      title: '档位描述',
      dataIndex: 'value',
      editable: false,
      width: 300,
      render: (value, record) => {
        const { enumList, isChange } = record
        const text = App.matchOption(enumList, value)?.label
        const title = (
          <div style={{ color: isChange ? 'red' : undefined, width: 300 }} className="z-ellipsis">
            {text}
          </div>
        )

        return <Tooltip title={text}>{title}</Tooltip>
      },
    },

    isShow && {
      title: '审批意见',
      dataIndex: 'approvalStatus',
      width: 150,
      editable: (record, index) => {
        if (record.fetchMethod === 'SYSTEM') return false
        return {
          element: (
            <Radio.Group
              options={approvalStatus}
              onChange={(e) => store.saveApprovalInfo(record, e.target.value, 'approvalStatus')}
            />
          ),
        }
      },
      render: (value, record) => {
        const title = App.matchOption(approvalStatus, value)?.label
        return <Tooltip title={title}>{title}</Tooltip>
      },
    },

    isShow &&
      TextAreaColumn({
        title: '审批说明',
        dataIndex: 'approvalOpinion',
        width: 200,
        editable: (record) => {
          if (record.fetchMethod === 'SYSTEM') return false
          return {
            element: (
              <Input.TextArea
                maxLength={200}
                style={{ width: 200 }}
                rows={2}
                onBlur={(e) => store.saveApprovalInfo(record, e.target.value, 'approvalOpinion')}
              />
            ),
            style: { margin: '12px 0' },
          }
        },
      }),
  ]

  const title = hymx? '标的物' : isZX ? '主体' : '定性'
  const mainTitle = hymx? '标的物得分' : isZX ? '主体得分' : '定性部分'
  const score = hymx ? ratingScoreRSP?.subjectVesselScore : isZX ? ratingScoreRSP?.subjectScore : ratingScoreRSP?.qualitativeScore
  return (
    <>
      <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>
        {mainTitle}
        <span className={styles.score}>
          {title}指标得分
          <span className={styles.num}>{score}</span>
        </span>
      </div>
      <Table
        columnsFilter={'customerRat_detail_QualitativeReport'}
        onFilter={(key,val) => saveServer('customerRat_detail_QualitativeReport',val)}
        columns={columns}
        serial
        rowKey={'fieldName'}
        resizable
        store={reportTable}
        pagination={false}
        editable={!!canApproval}
        scroll={{ x: 1200 }}
      ></Table>
    </>
  )
}

export default observer(Index)
