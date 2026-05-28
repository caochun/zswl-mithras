import { useLayoutEffect, useState } from 'react'

function hasValue(value) {
  return value !== undefined
}

export default function useMergedState(defaultStateValue, option = {}) {
  const { defaultValue, value, onChange, postState } = option

  const [innerValue, setInnerValue] = useState(() => {
    if (hasValue(value)) {
      return value
    } else if (hasValue(defaultValue)) {
      return typeof defaultValue === 'function' ? defaultValue() : defaultValue
    } else {
      return typeof defaultStateValue === 'function' ? defaultStateValue() : defaultStateValue
    }
  })

  const mergedValue = value !== undefined ? value : innerValue
  const postMergedValue = postState ? postState(mergedValue) : mergedValue

  const [prevValue, setPrevValue] = useState([mergedValue])

  useLayoutEffect(() => {
    const prev = prevValue[0]
    if (innerValue !== prev) {
      onChange?.(innerValue, prev)
    }
  }, [prevValue])

  useLayoutEffect(() => {
    if (!hasValue(value)) {
      setInnerValue(value)
    }
  }, [value])

  const triggerChange = (updater, ignoreDestroy) => {
    setInnerValue(updater, ignoreDestroy)
    setPrevValue([mergedValue], ignoreDestroy)
  }

  return [postMergedValue, triggerChange]
}
