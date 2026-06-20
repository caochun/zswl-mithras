import React, { useEffect, useState } from 'react'
import { Affix, message, Space, Steps } from 'antd'
import { Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import { isFunction } from 'lodash'

const Index = ({
  steps = [],
  onCancel,
  canClick,
  onSubmit,
  disabled,
  onNext,
  nextName,
  offsetTop = 55,
  extra = [],
  submitText = '完成',
  defaultCurrent = 0,
}) => {
  const [current, setCurrent] = useState(defaultCurrent)
  const [nextLoading, setNextLoading] = useState(false)
  const next = async () => {
    setNextLoading(true)
    try {
      await onNext?.(current)
      setNextLoading(false)
      setCurrent(current + 1)
    } catch (e) {
      console.error('e: ', e)
      setNextLoading(false)
    }
  }
  useEffect(() => {
    if (current > steps.length) setCurrent(0)
  }, [steps.length])

  const prev = () => {
    setCurrent(current - 1)
  }
  const items = steps.map((item) => ({
    key: item.title,
    title: item.title,
  }))
  const contentStyle = {
    marginTop: 16,
  }
  const handleChange = (current) => {
    setCurrent(current)
  }
  return (
    <div style={{ background: '#fff', padding: '20px 0 90px 0px' }}>
      <Affix offsetTop={offsetTop}>
        <Steps
          current={current}
          items={items}
          onChange={canClick && handleChange}
          style={{ background: '#fff', padding: ' 24px 0 36px 0' }}
        />
      </Affix>
      <div>
        {steps.map((v, i) => {
          if (v.isPreview) {
            return (
              i === current && (
                <div style={{ ...contentStyle }} key={v.title}>
                  {v?.content}
                </div>
              )
            )
          }
          return (
            <div
              style={{ ...contentStyle, display: i === current ? 'block' : 'none' }}
              key={v.title}
            >
              {v?.content}
            </div>
          )
        })}
      </div>
      {!canClick && (
        <Affix offsetBottom={0} style={{ marginTop: 12 }}>
          <Space
            style={{
              width: '100%',
              background: '#fff',
              justifyContent: 'flex-end',
              padding: '0 24px',
            }}
          >
            {onCancel && (
              <Button confirm onClick={onCancel} disabled={disabled}>
                取消
              </Button>
            )}

            {current > 0 && (
              <Button
                style={{
                  margin: '0 8px',
                }}
                disabled={disabled}
                onClick={() => prev()}
              >
                上一步
              </Button>
            )}
            {current < steps.length - 1 && (
              <Button
                type="primary"
                onClick={() => next()}
                disabled={disabled}
                loading={nextLoading}
              >
                {(isFunction(nextName) ? nextName(current) : nextName) ?? '下一步'}
              </Button>
            )}
            {current === steps.length - 1 && onSubmit && (
              <Button type="primary" onClick={onSubmit} disabled={disabled}>
                {submitText}
              </Button>
            )}
            {isFunction(extra) ? extra(current, steps) : extra}
          </Space>
        </Affix>
      )}
    </div>
  )
}
export default observer(Index)
