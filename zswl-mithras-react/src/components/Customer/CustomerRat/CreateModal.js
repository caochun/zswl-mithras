import customerRatApi from '@/api/customer/customerRat/customerRatApi'
import { ClientSelect } from '@/components/Select'
import { getUserInfo, rules } from '@/utils'
import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import { Col, Input, Row } from 'antd'
import { useEffect, useState } from 'react'
import SearchInput from './SelectInput'

const CustomerRatingCreateModal = ({ store }) => {
  const initial = store.createModal.getInitialValues() ?? {}
  const isEdit = initial?.editType === 'edit'
  const form = store.createModal.getFormStore()
  const clientChange = async (clientId) => {
    if (!clientId) {
      return
    }
    store.postClientInfo(clientId, undefined, () => {
      form.setFieldValue('clientId','')
      form.setFieldValue('clientCode','')
      form.setFieldValue('province','')
      form.setFieldValue('city','')
      form.setFieldValue('district','')
    })
  }
  useEffect(() => {
    isEdit && clientChange(initial.clientId)
  }, [isEdit])

  const onValuesChange = (changedValues, allValues) => {
    if(store.isUpdate){
      return
    }
    if(changedValues.model || !changedValues.clientId){
      form.setFieldValue('clientId','')
      form.setFieldValue('clientCode','')
      form.setFieldValue('province','')
      form.setFieldValue('city','')
      form.setFieldValue('district','')
    }
    if(changedValues.model){
      if(store.clientDisabled){
        store.clientDisabled = false
      }
      if(changedValues.model.value === 'client_hymx'){
        store.isHymx = true
      }else{
        store.isHymx = false
        store.showDistrict = false
      }
    }
  }
  const clearClientCode = (clear = false) => {
    const { model, clientCode, province, clientId } = form.getFieldsValue()
    if(model?.value === 'client_hymx'){
      if(!clientCode || clear){
        store.showDistrict = true
        form.setFieldsValue({
          clientCode: '',
          province:'',
          city:'',
          district:''
        })
        store.clientCodeRequire = false
      }
      if(clientCode && !province){
        store.showDistrict = true
        form.setFieldsValue({
          province:'',
          city:'',
          district:''
        })
      }
      if(clientCode && province){
        store.showDistrict = false
      }
    }else{
      clear && form.setFieldsValue({
        clientCode: '',
        clientId:'',
        province:'',
        city:'',
        district:''
      })
    }
  }
  const setHymxValue = (data) => {
    form.setFieldsValue({
      clientId: data,
    })
  }
  return (
    <Modal
      title={`${isEdit ? '编辑' : '新建'}客户评级`}
      store={store.createModal}
      width={800}
      destroyOnClose
    >
      <Form onValuesChange={onValuesChange}>
        <div>评级模型</div>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="model" label="评级模型名称" rules={[rules.required('请选择')]}>
              <Select
                labelInValue
                fieldNames={{ label: 'name', value: 'code' }}
                options={async () =>
                  await customerRatApi.postRatingModelQuery({
                    bizType: 'CLIENT',
                  })
                }
              />
            </Form.Item>
          </Col>
        </Row>
        <div>评级对象</div>
        <Row gutter={12}>
          <Col span={12}>
            {isEdit||store.isUpdate ? (
              <>
                <Form.Item name="clientName" label="客户名称" required>
                  <Input disabled />
                </Form.Item>
              </>
            ) : (
            <Form.Item name="clientId" label="客户名称" rules={[rules.required('请选择')]}>
              <SearchInput
                disabled={store.clientDisabled}
                onChange={clientChange}
                params={{ pageSize: 30, clientType: 'CORPORATION' }}
                setHymxValue={setHymxValue}
                isHymx={store.isHymx}
                clearClientCode={clearClientCode}
              ></SearchInput>
            </Form.Item>
            )}
          </Col>
          <Col span={12}>
            <Form.Item name="clientCode" label="客户编号" required={store.clientCodeRequire}>
              <Input disabled />
            </Form.Item>
          </Col>
        </Row>
        {
          !store.showDistrict &&
          <>
            <div>所在地区</div>
            <Row gutter={12}>
              <Col span={8}>
                <Form.Item name="province" label="所在省" required>
                  <Input disabled />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item name="city" label="所在地级市" required>
                  <Input disabled />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item name="district" label="所在区县" required>
                  <Input disabled />
                </Form.Item>
              </Col>
            </Row>
          </>
        }
      </Form>
    </Modal>
  )
}

export default observer(CustomerRatingCreateModal)
