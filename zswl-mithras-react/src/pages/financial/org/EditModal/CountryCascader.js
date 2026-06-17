import { ReadOnly } from '@/components'
import { http, observer } from '@zswl/admin'
import { Select, Form, App } from '@zswl/components'
import { Cascader, Input, Col, Row } from 'antd'
import { useState, useEffect, useCallback } from 'react'
import { RegionCascader } from '@/components'

const { Item } = Form

function Index({ form, onlyRead = true }) {
  const handleCascadeChange = (value, selectedOptions) => {
    form?.setFieldValue(
      'areaName',
      selectedOptions.map((v) => v.label)
    )
  }

  const SelectEditable = ({ value, options, labelInValue, ...rest }) => {
    const newValue = labelInValue ? value?.label : App.matchOption(options, value).label
    return onlyRead ? (
      <ReadOnly value={newValue} />
    ) : (
      <Select
        placeholder="请选择"
        options={options}
        labelInValue={labelInValue}
        value={value}
        {...rest}
      />
    )
  }

  const TextAreaEditable = (props) => {
    return onlyRead ? <ReadOnly {...props} /> : <Input.TextArea placeholder="请输入" {...props} />
  }

  const countryChange = () => {
    form?.setFieldValue('area', undefined)
    form?.setFieldValue('areaName', undefined)
  }
  return (
    <>
      <Col span={12}>
        <Item
          label={'国家/地区'}
          name={'country'} /* rules={[{ required: true, message: '请选择国家/地区' }]} */
        >
          <SelectEditable options={'countryList'} labelInValue onChange={countryChange} />
        </Item>
      </Col>
      <Item dependencies={['country']} noStyle>
        {({ getFieldValue }) => {
          const country = getFieldValue('country')
          const isChina = country?.value === '156' || undefined
          const detail = (
            <Col span={24}>
              <Item label={'详细地址'} name={'detail'} /* rules={[{ required: isChina }]} */>
                <TextAreaEditable rows={4} />
              </Item>
            </Col>
          )
          return (
            <>
              <Col span={12}>
                {!onlyRead ? (
                  <Item label={'省/市/区县'} name={'area'} /* rules={[{ required: isChina }]} */>
                    <RegionCascader onChange={handleCascadeChange} disabled={!isChina} />
                  </Item>
                ) : (
                  <Item label={'省/市/区县'} name={'areaName'}>
                    <ReadOnly />
                  </Item>
                )}
              </Col>
              {detail}
            </>
          )
          return <>{detail}</>
        }}
      </Item>
    </>
  )
}

export default observer(Index)
