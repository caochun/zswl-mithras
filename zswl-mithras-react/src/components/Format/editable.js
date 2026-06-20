import { rangePresets, rules, hasValue } from '@/utils'
import { DatePicker, Input, InputNumber, Mentions } from 'antd'
import moment from 'moment'
import Amount from './Amount'
import { ACCEPT_CODE } from './config'
import numeral from 'numeral'
import mathjs from '@/utils/math'

const { getMentions } = Mentions
const { RangePicker } = DatePicker

export const InputNumberEditable = (params = {}) => {
  const { required = true, precision, min = 0, max = 9999, ...rest } = params

  const validateLimit = (r, value) => {
    if (hasValue(value)) {
      if (value < min) {
        return Promise.reject(`输入值不能小于${min}`)
      }
      if (value > max) {
        return Promise.reject(`输入值不能大于${max}`)
      }
    }
    return Promise.resolve()
  }

  return {
    element: <InputNumber precision={precision ?? 0} {...rest} />,
    required,
    rules: [required && rules.required('请输入'), { validator: validateLimit }].filter(Boolean),
  }
}

export const InputEditable = (params = {}) => {
  const { required = true, disabled = true, ...rest } = params
  return {
    element: <Input disabled={disabled} />,
    required,
    rules: required && [rules.required('请输入')],
    ...rest,
  }
}
export const AmountEditable = (dataSource, dataIndex, params) => {
  const data = dataSource?.[dataIndex]
  const {
    required = false,
    disabled = true,
    stringMode = true,
    type = 'number',
    inputConfig = {},
    index,
    initFormat = 10000,
    transform: _transform,
    precision = 2,
    suffix,
    ...rest
  } = params
  const transform = (value) => {
    const resValue = hasValue(value)
      ? mathjs.multiply(mathjs.bignumber(value), initFormat).toString()
      : undefined
    return _transform
      ? _transform(resValue, value)
      : {
          [dataIndex]: resValue,
        }
  }

  const { onBlur, onChange, rowIndex, checkPlanPayAmount, ...restInputConfig } = inputConfig
  return {
    element: (
      <InputNumber
        stringMode
        style={{ width: '100%' }}
        disabled={disabled}
        precision={precision}
        formatter={(value) => {
          return value && numeral(value).format(`0,0.[${'0'.repeat(precision)}]`)
          // 导致小数点后千分位
          // return `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
        }}
        placeholder="请输入"
        parser={(value) => value.replace(/\$\s?|(,*)/g, '')}
        onBlur={(e) => {
          onBlur && onBlur(e, { dataSource, dataIndex, index, rowIndex })
          onChange && onChange(e.target.value, { dataSource, dataIndex, index, rowIndex })
        }}
        // onChange={(e) => onChange && onChange(e, { dataSource, dataIndex, index, rowIndex })}
        addonAfter={suffix}
        {...restInputConfig}
      />
    ),
    required,
    rules: [
      required && rules.required(),
      Amount.ruleFunc(1, inputConfig.min, inputConfig.max, precision),
      dataIndex === 'planPayAmount' && Amount.serviceFunc(checkPlanPayAmount)
    ].filter(Boolean),
    initialValue: hasValue(data) ? mathjs.divide(data, initFormat).toFixed(precision) : null,
    transform,
    initFormat,
    precision,
    suffix,
    ...rest,
  }
}

export const DatePickerEditable = (dataSource, dataIndex, params = {}) => {
  const data = dataSource?.[dataIndex]
  const {
    disabled,
    required = true,
    format = 'yyyy-MM-DD',
    disabledDate,
    onChange = () => {},
    ...rest
  } = params
  const showTime =
    format === 'YYYY-MM-DD HH:mm:ss' ? { defaultValue: moment('00:00:00', 'HH:mm:ss') } : false
  return {
    element: (
      <DatePicker
        disabled={disabled}
        format={format}
        showTime={showTime}
        disabledDate={disabledDate}
        onChange={onChange}
      />
    ),
    initialValue: (data && moment(data)) || undefined,
    // initialValue: (data && moment('2023/10/14')) || undefined,
    required,
    transform(values) {
      return {
        [dataIndex]: values && moment(values).format(format),
      }
    },
    rules: required && [rules.required('请选择日期')],
    ...rest,
  }
}

export const RangePickerEditable = (dataSource, dataIndex, params = {}) => {
  const [startField, endField, initField] = dataIndex
  const start = dataSource[startField]
  const end = dataSource[endField]
  const { required = true, ...rest } = params
  return {
    element: <RangePicker ranges={rangePresets} />,
    initialValue: start && end && [start && moment(start), end && moment(end)],
    transform(values) {
      return {
        [initField]: undefined,
        [startField]: values?.[0] && moment(values?.[0]).format('yyyy-MM-DD'),
        [endField]: values?.[0] && moment(values?.[1]).format('yyyy-MM-DD'),
      }
    },
    required,
    rules: required && [rules.arrayRequired('请选择')],
    ...rest,
  }
}

export const mentionsEditable = (params) => {
  const { required = true, options = [], acceptCode = [], prefix, ...rest } = params
  const newAcceptCode = [...ACCEPT_CODE, ...acceptCode].concat(prefix)

  const isNumber = (value) => {
    if (Number.isNaN(Number(value, 10))) {
      return false
    }
    return true
  }

  const isIllegalInput = (value) => {
    const inputArr = String(value)?.split('')
    const isIllegal = inputArr.some((str) => !newAcceptCode.includes(str))
    return isIllegal
  }

  const errorMessage = () => {
    throw new Error('输入值存在不合法字符！')
  }

  const checkMention = async (_, value) => {
    if (value && value !== prefix) {
      if (isNumber(value) && !String(value).includes(prefix)) {
        const isIllegal = isIllegalInput(value)
        if (isIllegal) {
          errorMessage()
        }
      } else {
        try {
          const mentions = getMentions(value, {
            prefix,
          })
          const inputValue = mentions[0]?.value
          if (inputValue) {
            const isIllegal = isIllegalInput(inputValue)
            if (isIllegal) {
              errorMessage()
            }
          } else {
            errorMessage()
          }
        } catch (error) {
          errorMessage()
        }
      }
    }
  }
  return {
    element: (
      <Mentions prefix={prefix} placeholder={`可输入'${prefix}'调用公式`} {...rest}>
        {options?.map(({ label, value }) => {
          return (
            <Mentions.Option value={value} key={value}>
              {label}
            </Mentions.Option>
          )
        })}
      </Mentions>
    ),
    required,
    rules: required && [
      rules.required('请输入'),
      {
        validator: checkMention,
      },
    ],
  }
}

// 可输入计算公式、数值、区间检验
export const InputCalcEditable = (params = {}) => {
  const { required = true, prefix = '=', acceptCode = [], ...rest } = params
  const newAcceptCode = [...ACCEPT_CODE, ...acceptCode].concat(prefix)

  const errorMessage = (tip) => {
    throw new Error(tip ?? '输入值存在不合法字符！')
  }

  const isIllegalInput = (value) => {
    const inputArr = String(value)?.split('')
    const isIllegal = inputArr.some((str) => !newAcceptCode.includes(str))
    return isIllegal
  }

  const checkInput = async (_, value) => {
    if (!value) return
    // 公式、区间
    if (value.startsWith(prefix)) {
      const realValue = value.slice(prefix.length)
      const isIllegal = isIllegalInput(realValue)
      if (isIllegal) {
        errorMessage()
      }
    } else {
      // 数值
      if (Number.isNaN(Number(value, 10))) {
        errorMessage()
        return
      }
      const decimalIndex = String(value).indexOf('.') + 1
      if (decimalIndex === 0) return
      const decimalLen = String(value).length - decimalIndex
      if (decimalLen > 3) {
        errorMessage('最多输入3位小数')
      }
    }
  }
  return {
    element: <Input {...rest} />,
    required,
    rules: required && [
      rules.required('请输入'),
      {
        validator: checkInput,
      },
    ],
  }
}

export const TextAreaEditable = (params = {}) => {
  const { required = false, ...rest } = params
  return {
    element: <Input.TextArea row={4} {...rest} />,
    required,
    rules: required && [rules.required('请输入')],
    style: { marginBottom: 20, marginTop: 10 },
  }
}
