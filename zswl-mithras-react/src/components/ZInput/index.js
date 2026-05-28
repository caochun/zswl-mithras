import React, { useState, useRef } from 'react'
import { Input } from 'antd'

const BaseHOC = (key) => {
  return (props) => {
    const { defaultValue, value, onChange } = props
    // 用来记录此时是否Compositionstart事件触发了，如果触发就置为true
    const composingRef = useRef(false)

    const [composingValue, setComposingValue] = useState('')

    // 如果启动了中文输入法，那么innerValue就是composingValue
    // composingValue就是中文输入的时候比如“客户”，你输入从“k”到“kehu”，此时innerValue都是composingValue
    // 除了中文输入法外，innerValue都是value
    const innerValue = composingRef.current ? composingValue : value ?? defaultValue

    const handleChange = (e) => {
      let { value: newStr } = e.currentTarget
      if (composingRef.current) {
        setComposingValue(newStr)
      } else {
        setComposingValue(newStr)
        // 中文输入完毕，此时触发onChange
        onChange && onChange(e)
      }
    }
    const handleComposition = (e) => {
      if ('compositionend' === e.type) {
        composingRef.current = false
        handleChange(e)
      } else {
        composingRef.current = true
      }
    }
    let Component = Input
    if (key) {
      Component = Input[key]
    }
    return (
      <Component
        {...props}
        value={innerValue}
        onCompositionStart={handleComposition}
        onCompositionUpdate={handleComposition}
        onCompositionEnd={handleComposition}
        onChange={handleChange}
      />
    )
  }
}

const ZInput = function (props) {
  return BaseHOC()(props)
}

ZInput.Search = function (props) {
  return BaseHOC('Search')(props)
}

ZInput.TextArea = function (props) {
  return BaseHOC('TextArea')(props)
}

ZInput.Password = Input.Password
ZInput.Group = Input.Group

export default ZInput
