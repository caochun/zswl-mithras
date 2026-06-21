import { getQuery, observer } from '@zswl/admin'
import { App, Table, TableStore } from '@zswl/components'
import { DateColumn, MatchOptionColumn, TextAreaColumn } from '@/components/Format'
import { Input, Radio, Tooltip } from 'antd'
import { options } from '@/utils'
import { useEffect, useMemo } from 'react'
import styles from './styles.less'
import { FormAmount } from '@/components/Form'
import { saveServer } from '@/utils'

const { approvalStatus } = options

const CustomerRatDetailQuantitativeReport = ({ list = [], ratingScoreRSP = {}, canApproval, store, isZX, hymx }) => {
  const isApproval = getQuery('typeId') == 'approval'
  const hasApprovalOption = list.some((item) => item?.approvalStatus)
  const isShow = isApproval && (canApproval ? true : hasApprovalOption)
  const columns = [
    { title: '指标字段名称', dataIndex: 'fieldComment', editable: false },
    {
      title: '指标字段值',
      dataIndex: 'value',
      editable: false,
      render: (value, record) => {
        let { isChange, unit } = record
        if (record.dataType == 'number' && !unit) unit = '万元'
        const title = ![null, undefined].includes(value) ? `${value} ${unit ?? ''}` : '-'
        return (
          <Tooltip title={title}>
            <div style={{ color: isChange ? 'red' : undefined, textAlign: 'right' }}>{title}</div>
          </Tooltip>
        )
      },
    },
    DateColumn({
      title: '数据时点',
      dataIndex: 'date',
      editable: false,
    }),
    isShow && {
      title: '审批意见',
      dataIndex: 'approvalStatus',
      editable: (record, index) => {
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
          return {
            element: (
              <Input.TextArea
                maxLength={200}
                rows={2}
                onBlur={(e) => store.saveApprovalInfo(record, e.target.value, 'approvalOpinion')}
              />
            ),
            style: { margin: '12px 0' },
          }
        },
      }),
  ].filter(Boolean)

  const approvalColumns = [
    { title: '指标名称', dataIndex: 'fieldComment' },
    // { title: '计算表达式', dataIndex: 'executeExpress' },
    hymx ?
    {
      title: '取数方式',
      width: 120,
      dataIndex: 'fetchMethod',
      matchOption: 'ratingFetchMethodEnum',
      editable: false,
      render: (value) => App.matchOption('ratingFetchMethodEnum', value)?.label ?? '手工录入',
    } : MatchOptionColumn({
      title: '取数方式',
      dataIndex: 'fetchMethod',
      matchOption: 'ratingFetchMethodEnum',
    }),
    {
      title: hymx ? '指标值/指标档位' : '指标值',
      dataIndex: 'value',
      render: (value, record) => {
        let { isChange, unit } = record
        if (value === null || value === -9999999999999) {
          return <div style={{ textAlign: 'right' }}>-</div>
        }
        if (record.dataType == 'number') {
          return (
            <FormAmount.Format
              value={{ value, isChange }}
              suffix={unit}
              initFormat={1}
              style={{ textAlign: 'right' }}
            />
          )
        }
        return (
          <div style={{ textAlign: 'right', color: isChange ? 'red' : undefined }}>
            {value} {unit ?? ''}
          </div>
        )
      },
    },
    hymx && {
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
    },isShow && hymx && {
      title: '审批意见',
      dataIndex: 'approvalStatus',
      editable: (record, index) => {
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

    isShow && hymx &&
      TextAreaColumn({
        title: '审批说明',
        dataIndex: 'approvalOpinion',
        width: 200,
        editable: (record) => {
          return {
            element: (
              <Input.TextArea
                maxLength={200}
                rows={2}
                onBlur={(e) => store.saveApprovalInfo(record, e.target.value, 'approvalOpinion')}
              />
            ),
            style: { margin: '12px 0' },
          }
        },
      }),
    !hymx && DateColumn({
      title: '数据时点',
      dataIndex: 'date',
      editable: false,
    }),
    // { title: '指标得分', dataIndex: 'fieldScore' },
  ].filter(Boolean)

  const reportSource = (list ?? []).filter((v) => v.fetchMethod === 'IMPORT')
  const reportTable = useMemo(() => new TableStore({ request: () => reportSource }), [list])
  const approvalTable = useMemo(() => new TableStore({ request: () => list }), [list])
  const title = hymx ? '客户综合' : isZX ? '区域' : '定量'
  const score = hymx ? ratingScoreRSP?.custScore : isZX ? ratingScoreRSP?.areaScore : ratingScoreRSP?.quantitativeScore
  return (
    <div>
      {!isZX && !!reportSource.length && (
        <>
          <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>
            定量指标填报 ({reportSource.length} / {reportSource.length})
          </div>
          <Table
            columnsFilter={'customerRat_detail_QuantitativeReport_1'}
            onFilter={(key,val) => saveServer('customerRat_detail_QuantitativeReport_1',val)}
            columns={columns}
            serial
            resizable
            store={reportTable}
            rowKey={'fieldName'}
            pagination={false}
            columnWidth={100}
            editable={canApproval}
          ></Table>
        </>
      )}
      <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>
        {title}得分
        <span className={styles.score}>
          {title}指标得分
          <span className={styles.num}>{score}</span>
        </span>
      </div>
      <Table
        columnsFilter={'customerRat_detail_QuantitativeReport_2'}
        onFilter={(key,val) => saveServer('customerRat_detail_QuantitativeReport_2',val)}
        columns={approvalColumns}
        serial
        rowKey={'fieldName'}
        resizable
        store={approvalTable}
        pagination={false}
      ></Table>
    </div>
  )
}

export default observer(CustomerRatDetailQuantitativeReport)
