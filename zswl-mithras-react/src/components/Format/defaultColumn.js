import { rangePresets, rules, amountFormat, formatPercent, toHump } from '@/utils'
import { dateRangeTransform } from '@/utils/transform'
import AmountRange from '@/components/AmountRange'

import { ClientSelect, FounderSelect } from '@/components/Select'
import { AmountEditable, TextAreaEditable } from './editable'
import { FiledFormat, getValue } from './render'
import { App, Select, Button, Upload } from '@zswl/components'
import { DatePicker, message, Modal, Tag, Tooltip, Input } from 'antd'
import moment from 'moment'
import { cloneElement } from 'react'
import _ from 'lodash'
import DataUpload from '../DataUpload'
import commonApi from '@/utils/api/fileApi'
import FileList from './FileList'
import { create, all } from 'mathjs'
const mathjs = create(all)

// 1个月，3个月，半年，一年
const preset = [
  { label: '今天', value: moment() },
  { label: '1个月', value: moment().add(1, 'month') },
  { label: '3个月', value: moment().add(3, 'month') },
  { label: '半年', value: moment().add(6, 'month') },
  { label: '一年', value: moment().add(1, 'year') },
]
export const DatePickerWrap = ({ value, onChange, ...rest }) => {
  return (
    <DatePicker
      showToday={false}
      renderExtraFooter={(panelNode) => {
        return preset.map(({ label, value }) => {
          return (
            <Tag onClick={() => onChange?.(value)} style={{ cursor: 'pointer' }} color="blue">
              {label}
            </Tag>
          )
        })
      }}
      value={value}
      onChange={onChange}
      {...rest}
    />
  )
}
export const AmountColumn = ({
  title,
  dataIndex,
  search,
  editable = false,
  initFormat = 10000,
  wrapItemProps = {},
  precision,
  suffix,
  needSmallNumber,
  disabledFunc,
  ...rest
}) => {
  const defaultElement = {
    element: <AmountRange />,
    itemProps: {
      transform: (val) => {
        const [start, end] = val || []
        return {
          [dataIndex]: undefined,
          [`${dataIndex}From`]: start && start * initFormat,
          [`${dataIndex}To`]: end && end * initFormat,
        }
      },
    },
  }
  const render = (val) => (
    <FormAmount.Format
      value={val}
      initFormat={initFormat}
      precision={precision}
      suffix={suffix}
      needSmallNumber={needSmallNumber}
    />
  )

  return {
    _columnType: 'amount',
    title,
    dataIndex,
    editable:
      editable === true || disabledFunc
        ? ({ itemProps, ...val }, index) => {
            const disabled = disabledFunc?.(val)
            if (disabled) return false
            return AmountEditable(val, dataIndex, {
              disabled: false,
              index,
              initFormat,
              suffix,
              ...itemProps,
              ...wrapItemProps,
              precision,

              inputConfig: {
                addonAfter: suffix,
                ...(wrapItemProps?.inputConfig ?? {}),
              },
            })
          }
        : editable,
    render,
    excelRender: (val) => {
      const newValue = FormAmount.getNumber({ value: val, initFormat, precision, needSmallNumber })
      return suffix ? `${newValue}${suffix}` : newValue
    },
    search: search === true ? defaultElement : search,
    element: (
      <FormAmount
        initFormat={initFormat}
        suffix={suffix}
        precision={precision}
        {...(wrapItemProps?.inputConfig ?? {})}
      />
    ),

    initFormat,
    align: 'right',
    suffix,
    ...rest,
  }
}
export const AmountFormatter = ({
  value,
  initFormat = 10000,
  suffix = '',
  isHighlight = false,
}) => {
  const newValue = value?.value !== undefined ? value?.value : value
  const isChange = value?.isChange
  const title = amountFormat(formatPercent(newValue, initFormat))
  // 有高亮展示橙色 ，没有根据是否改变暂时红色
  const color = isHighlight ? '#ee6d32' : isChange ? 'red' : undefined
  return (
    <>
      <span style={{ color }}>{[null, undefined].includes(title) ? '-' : `${title}${suffix}`}</span>
    </>
  )
}

export const FounderColumn = ({
  title,
  dataIndex,
  renderField,
  requiredMark,
  params,
  search,
  mode,
  ...rest
}) => {
  const { functionCode } = rest || {}
  const defaultElement = {
    rules: requiredMark && [rules.required('请选择')],
    element: <FounderSelect params={params} style={{ width: '100%' }} mode={mode} />,
  }
  if (functionCode) {
    defaultElement.element = cloneElement(defaultElement.element, { functionCode })
  }

  return {
    title,
    dataIndex,
    editable: defaultElement,
    requiredMark,
    render: (val, record) => <FiledFormat title={record?.[renderField ?? dataIndex]} />,
    excelRender: (val, record) => getValue(record?.[renderField ?? dataIndex]),
    search: search === true ? defaultElement : search,
    ...rest,
  }
}

export const SelectColumn = ({
  title,
  dataIndex,
  options,
  search,
  fieldNames,
  editable,
  requiredMark,
  ...rest
}) => {
  const defaultElement = {
    element: <Select options={options} allowClear fieldNames={fieldNames} />,
  }
  return {
    title,
    dataIndex,
    editable:
      editable === true
        ? { ...defaultElement, requiredMark, rules: requiredMark && [rules.required('请选择')] }
        : editable,
    render: (val) => <FiledFormat value={val} />,
    excelRender: (val) => val,
    search: search === true ? defaultElement : search,
    requiredMark,
    ...rest,
  }
}

export const DateColumn = ({
  title,
  dataIndex,
  editable,
  dateFormat,
  search,
  itemProps = {},
  requiredMark,
  disabledDate,
  ...rest
}) => {
  const newDateFormat = dateFormat ?? 'yyyy-MM-DD'
  const defaultElement = {
    element: <DatePicker.RangePicker renderExtraFooter={(panelNode) => {}} ranges={rangePresets} />,
    itemProps: {
      transform: (val) =>
        dateRangeTransform(val, `${dataIndex}From`, `${dataIndex}To`, 'yyyy-MM-DD', dataIndex),
    },
  }
  const render = (val) => {
    if (!moment.isMoment(val) && !val) return '-'
    const isMoment = moment.isMoment(val)
    const newValue = _.isObject(val) && !isMoment ? val.value : val
    if (_.isObject(val) && val !== null) {
      if (newValue) val.value = moment(newValue).format(newDateFormat)
      return <FiledFormat value={val} />
    } else {
      const title = moment(newValue).format(newDateFormat)
      return newValue ? <Tooltip title={title}>{title}</Tooltip> : '-'
    }
  }
  return {
    _columnType: 'date',
    title,
    dataIndex,
    dateFormat: newDateFormat,
    requiredMark,
    search: search === true ? defaultElement : search,
    editable:
      editable === true
        ? (val) => {
            return {
              initialValue: (val[dataIndex] && moment(val[dataIndex])) || undefined,
              type: 'datePicker',
              element: (
                <DatePicker renderExtraFooter={(panelNode) => {}} disabledDate={disabledDate} />
              ),
              transform: (date) => ({ [dataIndex]: date && moment(date).format(newDateFormat) }),
              rules: requiredMark && [rules.required('请选择')],
              ...rest,
            }
          }
        : editable,
    element: <DatePicker style={{ width: '100%' }} />,
    transform: (val) => ({ [dataIndex]: val && moment(val).format(newDateFormat) }),
    itemProps: {
      transform: (val) => ({ [dataIndex]: val && moment(val).format(newDateFormat) }),
      ...itemProps,
    },
    render,
    excelRender: (val) => val && moment(val).format(newDateFormat),
    ...rest,
  }
}

export const TextAreaColumn = ({ title, dataIndex, editable, width, ...rest }) => ({
  title,
  dataIndex,
  span: 2,
  width,
  editable: editable === true ? TextAreaEditable(rest) : editable,
  element: <Input.TextArea row={4} />,
  render: (val) => <FiledFormat value={val} width={width} />,
  excelRender: (val) => getValue(val),
  ...rest,
})

export const InputColumn = ({ title, dataIndex, editable = false, requiredMark, ...rest }) => ({
  title,
  dataIndex,
  render: (val) => <FiledFormat value={val} />,
  excelRender: (val) => getValue(val),
  element: <Input />,
  editable:
    editable === true
      ? { requiredMark, element: <Input />, rules: requiredMark && [rules.required('请输入')] }
      : editable,
  requiredMark,
  ...rest,
})

export const MatchOptionColumn = ({
  title,
  dataIndex,
  matchOption,
  search,
  editable,
  element = true,
  requiredMark,
  mode,
  disabled,
  allowClear = true,
  ...rest
}) => {
  matchOption = matchOption ?? dataIndex
  const defaultElement = {
    element: (
      <Select
        options={matchOption}
        allowClear={allowClear}
        getPopupContainer={() => document.body}
        mode={mode}
        disabled={disabled}
      />
    ),
  }
  const render = (val) => {
    const newValue = App.matchOption(matchOption, val?.value ?? val).label
    const isChange = val?.isChange
    return <FiledFormat value={newValue} isChange={isChange} />
  }
  return {
    title,
    dataIndex,
    render,
    excelRender: (val) => App.matchOption(matchOption, val?.value ?? val).label,
    matchOption,
    editable:
      editable === true
        ? {
            ...defaultElement,
            requiredMark,
            rules: requiredMark && [rules.required('请选择')],
          }
        : editable,
    search: search === true ? defaultElement : search,
    element:
      element === true ? (
        <Select
          options={matchOption}
          allowClear={allowClear}
          getPopupContainer={() => document.body}
        />
      ) : (
        element
      ),
    requiredMark,
    mode,
    disabled,
    ...rest,
  }
}

export const UploadColumn = ({
  title,
  dataIndex,
  editable,
  element = true,
  requiredMark,
  params,
  ...rest
}) => {
  const defaultElement = (record, index) => ({
    element: (
      <DataUpload
        accept={'*'}
        api={async (file) => {
          try {
            const paramsData = _.isFunction(params) ? params(record) : params
            const newParams = {
              file,
              ...paramsData,
            }
            const functionCode = `${toHump(newParams.moduleType)}FileUpload`
            const res = await commonApi.postFileUpload(
              { materialsType: 'DEFAULT', ...newParams },
              functionCode
            )
            if (!res.success) {
              message.error(res.msg)
              throw res
            }
            return [{ name: file.name, ...(res?.data ?? {}) }]
          } catch (e) {
            throw e
          }
        }}
        maxCount={10}
        onRemove={(file) => {
          const name = file?.name ?? file.filename
          const fileId = file.id ?? file.fileId
          const paramsData = _.isFunction(params) ? params(record) : params
          return new Promise((resolve, reject) => {
            Modal.confirm({
              title: `您将删除文件 '${name}'，请确认！`,
              onOk: async () => {
                const functionCode = `${toHump(paramsData.moduleType)}FileBatchRemove`
                const res = await commonApi.postBatchRemove(
                  { ...paramsData, fileIds: [fileId] },
                  functionCode
                )
                const { code, msg } = res
                if (code === 200) {
                  message.success('删除成功')
                  resolve()
                } else {
                  message.info(msg)
                  reject()
                }
              },
              onCancel: () => {
                reject()
              },
            })
          })
        }}
      >
        <Button size="small" type="primary">
          上传
        </Button>
      </DataUpload>
    ),
  })
  return {
    title,
    dataIndex,
    width: 340,
    editable: editable === true ? defaultElement : editable,
    render: (val, record) => (
      <FileList value={val} params={_.isFunction(params) ? params(record) : params} />
    ),
    ...rest,
  }
}

export const DataUploadColumn = ({ title, dataIndex, editable, itemProps, ...rest }) => {
  const element = <Upload {...rest} />
  return {
    title,
    dataIndex,
    editable: editable === true ? { element } : editable,
    element,
    transform: (val) => ({ [dataIndex]: val?.map((item) => item.key).join('、') }),
    ...itemProps,
    ...rest,
  }
}
export const CustomColumn = ({
  title,
  dataIndex = 'client',
  params,
  search,
  editable,
  ...rest
}) => {
  const defaultElement = {
    element: <ClientSelect canJump={false} {...params} />,
    functionCode: 'policyClientList',
  }
  return {
    title,
    dataIndex: `${dataIndex}Id`,
    width: 300,
    search: search === true ? defaultElement : search,
    editable: editable === true ? defaultElement : editable,
    render: (val, record) => <FiledFormat title={record?.[`${dataIndex}Name`]} />,
    excelRender: (val, record) => getValue(record?.[`${dataIndex}Name`]),
    ...rest,
  }
}
