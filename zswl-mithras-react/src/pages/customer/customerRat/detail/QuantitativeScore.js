import { observer } from '@zswl/admin'
import { App, Table } from '@zswl/components'
import { useEffect, useRef, useState } from 'react'
import {
  AmountEditable,
  DateColumn,
  DatePickerEditable,
  MatchOptionColumn,
  TextAreaColumn,
} from '@/components/Format'
import { options } from '@/utils'
import { Tooltip } from 'antd'
const { approvalStatus } = options
import customerRatApi from '@/api/customer/customerRat/customerRatApi'
import { EditTable } from '@/components'

const FiledEditable = (record, id, isZX) => {
  const addonAfter = !!record.unit ? `${record.unit}` : undefined
  const zxDisabled = !record.canEdit || !record.bizId
  const disabled = record.fetchMethod === 'SYSTEM'
  if (isZX && zxDisabled) return false
  if (!isZX && disabled) return false
  if (['time', 'date'].includes(record.dataType)) {
    const format = record.dataType === 'date' ? 'YYYY-MM-DD' : 'YYYY-MM-DD HH:mm:ss'
    return DatePickerEditable(record, 'fieldValue', { format })
  }
  if (record.dataType === 'NUMBER') {
    1
    return AmountEditable(record, 'fieldValue', {
      disabled: false,
      required: true,
      initFormat: 1,
      precision: 4,
      inputConfig: { addonAfter, min: -Infinity },
    })
  }
  return {
    type: 'input',
    required: true,
    inputConfig: { addonAfter },
  }
}
const Index = ({ auth, id, store, isZX, paramInfo }) => {
  const { info = {}, ratingParam } = paramInfo ?? {}
  const tableRef = useRef(null)
  const { hasApprovalOption, setHasApprovalOption } = store
  if (isZX && tableRef.current) store.quantitativeTable = tableRef.current?.table

  const columns = [
    { title: '指标字段名称', dataIndex: 'fieldComment', editable: false },

    {
      title: (
        <div>
          <span style={{ color: 'red' }}>*</span>指标字段值
        </div>
      ),
      dataIndex: 'fieldValue',
      editable: (record) => FiledEditable(record, id, isZX),
      render: (text, { unit, isChange }) => {
        return (
          <div style={{ textAlign: 'right', color: isChange ? 'red' : undefined }}>
            {![undefined, null].includes(text) ? `${text ?? ''}${unit ?? ''}` : '-'}
          </div>
        )
      },
    },
    MatchOptionColumn({
      title: '取数方式',
      dataIndex: 'fetchMethod',
      matchOption: 'ratingFetchMethodEnum',
      editable: false,
    }),
    DateColumn({ title: '数据时点', dataIndex: 'date', editable: false }),
    hasApprovalOption && {
      title: '审批意见',
      dataIndex: 'approvalStatus',
      render: (value, record) => {
        const title = App.matchOption(approvalStatus, value)?.label ?? '-'
        return <Tooltip title={title}>{title}</Tooltip>
      },
      editable: false,
    },

    hasApprovalOption &&
      TextAreaColumn({ title: '审批说明', dataIndex: 'approvalOpinion', editable: false }),
  ].filter(Boolean)
  const formatInfo = (info) => {
    const result = []
    Object.entries(info?.定量指标 ?? {}).forEach(([key, value]) => {
      result.push(...value)
    })
    return result
  }
  const getList = async (params) => {
    const paramInfo = await customerRatApi.postClientParamInfo({ id })
    const newList = formatInfo(paramInfo?.info ?? {})
    if (newList.length) {
      const hasOption = newList.some((item) => item?.approvalStatus)
      setHasApprovalOption(hasOption)
    }
    return { list: newList }
  }

  const saveData = async (list, values) => {
    const indicatorValueList = list.map((item) => ({
      fieldName: item.fieldName,
      ratingClientId: id,
      bizId: item.bizId,
      indicatorValue: item.fieldValue,
    }))
    const res = await customerRatApi.postAreaIndicatorBatchmodify({
      ratingClientId: id,
      indicatorValueList,
    })
  }
  const noZXFormatInfo = (info) => {
    const result = []
    Object.entries(info?.定量指标 ?? {}).forEach(([key, value]) => {
      const newValue = value.map((v) => {
        const find = ratingParam?.find((item) => item.fieldName === v?.fieldName) ?? {}
        const ratingValue = find.dataType === 'number' ? Number(find?.fieldValue) : find?.fieldValue

        return {
          ...v,
          date: find?.date,
          fieldValue: v.fetchMethod === 'SYSTEM' ? v.fieldValue : ratingValue,
        }
      })

      result.push(...newValue)
    })
    console.log('result: ', result)
    return result
  }

  useEffect(() => {
    const newList = noZXFormatInfo(info)
    if (newList.length) {
      const hasOption = newList.some((item) => item?.approvalStatus)
      setHasApprovalOption(hasOption)
      setTimeout(() => {
        store.quantitativeTable?.setList(newList)
      }, 0)
    }
  }, [JSON.stringify(info)])
  return (
    <div>
      {isZX ? (
        <EditTable
          title={
            <div style={{ color: '#0058f9', padding: '12px 0' }}>1. 请填写下列指标字段的数值</div>
          }
          columns={columns}
          serial
          resizable
          locale={{
            Empty: {
              description: '不需要填写定量指标',
            },
          }}
          ref={tableRef}
          tableApi={getList}
          rowKey={'fieldName'}
          canEdit={auth}
          saveData={saveData}
          pagination={false}
        />
      ) : (
        <Table
          columns={columns}
          serial
          rowKey={'fieldName'}
          resizable
          locale={{
            Empty: {
              description: '不需要填写定量指标',
            },
          }}
          store={store.quantitativeTable}
          pagination={false}
          editable={auth}
        />
      )}
    </div>
  )
}

export default observer(Index)
