import { ConfigProvider, Skeleton } from 'antd'
import { RightOutlined } from '@ant-design/icons'
import classNames from 'classnames'
import React, { createContext, useCallback, useContext, useMemo, useRef, useState } from 'react'
import CheckCard from './index'
import { omit } from 'lodash'
import useMergedState from '@/utils/hooks/useMergedState'

export const CheckCardGroupConnext = createContext(null)

export const CardLoading = ({ prefixCls }) => {
  return (
    <div className={classNames(`${prefixCls}-loading-content`)}>
      <Skeleton loading active paragraph={{ rows: 4 }} title={false} />
    </div>
  )
}

/**
 * SubCheckCardGroup component.
 *
 * @component
 * @param {React.ReactNode} title - The title of the group.
 * @param {React.ReactNode} children - The content of the group.
 * @param {string} prefix - The prefix for CSS class names.
 * @returns {React.ReactNode} The rendered SubCheckCardGroup component.
 */
const SubCheckCardGroup = (props) => {
  const [collapse, setCollapse] = useState(false)
  const baseCls = `${props.prefix}-sub-check-card`
  return (
    <div className={classNames(baseCls)}>
      <div
        className={classNames(`${baseCls}-title`)}
        onClick={() => {
          setCollapse(!collapse)
        }}
      >
        <RightOutlined
          style={{
            transform: `rotate(${collapse ? 90 : 0}deg)`,
            transition: 'transform 0.3s',
          }}
        />
        {props.title}
      </div>
      <div
        className={classNames(`${baseCls}-panel`, {
          [`${baseCls}-panel-collapse`]: collapse,
        })}
      >
        {props.children}
      </div>
    </div>
  )
}

const CheckCardGroup = (props) => {
  const {
    prefixCls: customizePrefixCls,
    className,
    style,
    options = [],
    loading = false,
    multiple = false,
    bordered = true,
    onChange,
    ...restProps
  } = props

  const antdContext = useContext(ConfigProvider.ConfigContext)

  const getOptions = useCallback(() => {
    return options?.map((option) => {
      if (typeof option === 'string') {
        return {
          title: option,
          value: option,
        }
      }
      return option
    })
  }, [options])

  const prefixCls = antdContext.getPrefixCls('pro-checkcard', customizePrefixCls)

  const groupPrefixCls = `${prefixCls}-group`

  const domProps = omit(restProps, ['children', 'defaultValue', 'value', 'disabled', 'size'])

  const [stateValue, setStateValue] = useMergedState(props.defaultValue, {
    value: props.value,
    onChange: props.onChange,
  })

  const registerValueMap = useRef(new Map())

  const registerValue = (value) => {
    registerValueMap.current?.set(value, true)
  }

  const cancelValue = (value) => {
    registerValueMap.current?.delete(value)
  }

  const toggleOption = (option) => {
    if (!multiple) {
      let changeValue

      changeValue = stateValue
      // 单选模式
      if (changeValue === option.value) {
        changeValue = undefined
      } else {
        changeValue = option.value
      }
      setStateValue?.(changeValue)
    }

    if (multiple) {
      let changeValue = []
      const stateValues = stateValue
      const hasOption = stateValues?.includes(option.value)
      changeValue = [...(stateValues || [])]
      if (!hasOption) {
        changeValue.push(option.value)
      }
      if (hasOption) {
        changeValue = changeValue.filter((itemValue) => itemValue !== option.value)
      }
      const newOptions = getOptions()
      const newValue = changeValue
        ?.filter((val) => registerValueMap.current.has(val))
        ?.sort((a, b) => {
          const indexA = newOptions.findIndex((opt) => opt.value === a)
          const indexB = newOptions.findIndex((opt) => opt.value === b)
          return indexA - indexB
        })

      setStateValue(newValue)
    }
  }

  const children = useMemo(() => {
    if (loading) {
      return (
        new Array(options.length || React.Children.toArray(props.children).length || 1)
          .fill(0)
          // eslint-disable-next-line react/no-array-index-key
          .map((_, index) => <CheckCard key={index} loading />)
      )
    }

    if (options?.length > 0) {
      const optionValue = stateValue

      const renderOptions = (list) => {
        return list.map((option) => {
          if (option?.children?.length > 0) {
            return (
              <SubCheckCardGroup
                title={option.title}
                prefix={groupPrefixCls}
                key={option.value?.toString() || option.title?.toString()}
              >
                {renderOptions(option.children)}
              </SubCheckCardGroup>
            )
          }
          return (
            <CheckCard
              key={option.value.toString()}
              disabled={option.disabled}
              size={option.size ?? props.size}
              value={option.value}
              checked={
                multiple ? optionValue?.includes(option.value) : optionValue === option.value
              }
              onChange={option.onChange}
              title={option.title}
              avatar={option.avatar}
              description={option.description}
              cover={option.cover}
            />
          )
        })
      }
      return renderOptions(getOptions())
    }
    return props.children
  }, [getOptions, loading, multiple, options, props.children, props.size, stateValue])

  const classString = classNames(groupPrefixCls, className)

  return (
    <CheckCardGroupConnext.Provider
      value={{
        toggleOption,
        bordered,
        value: stateValue,
        disabled: props.disabled,
        size: props.size,
        loading: props.loading,
        multiple: props.multiple,
        registerValue,
        cancelValue,
        cache: props.cache,
      }}
    >
      <div className={classString} style={style} {...domProps}>
        {children}
      </div>
    </CheckCardGroupConnext.Provider>
  )
}

export default CheckCardGroup
