import classNames from 'classnames'
import React, { useContext, useEffect, useMemo, useState } from 'react'
import CheckCardGroup, { CardLoading, CheckCardGroupConnext } from './Group'
import './index.less'
import useMergedState from '@/utils/hooks/useMergedState'

const prefixCls = 'site-pro-checkcard'
const CheckCard = (props) => {
  const [stateChecked, setStateChecked] = useMergedState(props.defaultChecked || false, {
    value: props.checked,
    onChange: props.onChange,
  })
  const checkCardGroup = useContext(CheckCardGroupConnext)

  const handleClick = (e) => {
    props?.onClick?.(e)
    const newChecked = !stateChecked
    checkCardGroup?.toggleOption?.({ value: props.value })
    setStateChecked?.(newChecked)
  }

  useEffect(() => {
    checkCardGroup?.registerValue?.(props.value)
    return () => {
      if (!checkCardGroup?.cache) {
        checkCardGroup?.cancelValue?.(props.value)
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [props.value])

  const { className, avatar, title, description, cover, extra, style = {}, ...others } = props

  const checkCardProps = { ...others }

  checkCardProps.checked = stateChecked

  let multiple = false

  if (checkCardGroup) {
    // 受组控制模式
    checkCardProps.disabled = props.disabled || checkCardGroup.disabled
    checkCardProps.loading = props.loading || checkCardGroup.loading
    checkCardProps.bordered = props.bordered || checkCardGroup.bordered

    multiple = checkCardGroup.multiple

    const isChecked = checkCardGroup.multiple
      ? checkCardGroup.value?.includes(props.value)
      : checkCardGroup.value === props.value

    // loading时check为false
    checkCardProps.checked = checkCardProps.loading ? false : isChecked
    checkCardProps.size = props.size || checkCardGroup.size
  }

  const { disabled = false, size, loading: cardLoading, checked, bordered = true } = checkCardProps

  return (
    <div className={prefixCls}>
      <div
        className={classNames({
          [`${prefixCls}-multiple`]: multiple,
          [`${prefixCls}-checked`]: checked,
          [`${prefixCls}-disabled`]: disabled || cardLoading,
          [`${prefixCls}-loading`]: cardLoading,
          [`${prefixCls}-bordered`]: bordered,
          [className]: className,
        })}
        style={style}
        onClick={(e) => {
          if (!cardLoading && !disabled) {
            handleClick(e)
          }
        }}
        onMouseEnter={props.onMouseEnter}
      >
        {props.children ? (
          <div className={classNames(`${prefixCls}-body`)} style={props.bodyStyle}>
            {props.children}
          </div>
        ) : null}
      </div>
    </div>
  )
}

CheckCard.Group = CheckCardGroup

export default CheckCard
