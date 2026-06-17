import { useEffect, useState } from 'react'
import { Form, Input, Select, InputNumber, Descriptions, Button, Space } from 'antd'
import Amount from '@/components/Amount'
import ZText from '@/components/ZText'
import styles from './style.less'
import math from '@/utils/math'
import { use } from 'echarts'
const Index = ({ detail, store, canEdit, setEditing }) => {
    const [form] = Form.useForm()
    const [edit,setEdit] = useState(false)
    const [data,setDate] = useState(null)
    useEffect(() => {
        form?.setFieldsValue(detail)
    }, [detail])
    useEffect(() => {
        setEditing && setEditing(edit)
    }, [edit])
    const starDom = (name, must, obj) => {
        return (
        <span className={styles.colorsWrap}>
            {must && <span className={styles.colors}>*</span>}
            <span style={{ color: obj?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>{name}</span>
        </span>
        )
    }
    const handleChange = (vals, allVals) => {
        if(!vals.recyclingFlag){
            form.setFieldValue('recyclingFlag', parseFloat(allVals.returnedAmount)/10000>0?'0':detail.recyclingFlag)
        }
        let deductionAmountValue = allVals.deductionAmount
        if('returnedAmount' in vals){
            deductionAmountValue = math.subtract(allVals['collectionAmount'], vals['returnedAmount']).toString()
            form.setFieldValue('deductionAmount', deductionAmountValue)
        }
        let _returnedAmountValue = allVals.returnedAmount
        if('deductionAmount' in vals){
            const returnedAmountValue = math.subtract(allVals['collectionAmount'], vals['deductionAmount']).toString()
            form.setFieldValue('returnedAmount', returnedAmountValue)
            _returnedAmountValue = returnedAmountValue
        }
        setDate({
            ...allVals,
            recyclingFlag:parseFloat(allVals.returnedAmount)/10000>0?'0':allVals.recyclingFlag,
            deductionAmount:deductionAmountValue,
            returnedAmount:_returnedAmountValue
        })
    }
    return (
        <Form
            form={form}
            onValuesChange={handleChange}
        >
            <Descriptions
                title="保证金退抵方案"
                bordered
                column={2}
                labelStyle={{ background: '#F5F6FA' }}
                size={'small'}
                className={styles.des}
                extra={canEdit&&
                    <Space>
                        {canEdit &&!edit&&<Button type="primary" onClick={()=>setEdit(true)}>编辑</Button>}
                        {edit&&<Button onClick={()=>{
                            setEdit(false)
                            setDate(null)
                            form?.setFieldsValue(detail)
                            }}>取消</Button>}
                        {edit&&<Button type="primary" onClick={()=>store.saveRefund(setEdit,data||detail)}>保存</Button>}
                    </Space>
                }
            >
                <Descriptions.Item
                    label={starDom('保证金余额（元）', false, null)}
                    labelStyle={{ width: '180px' }}
                    contentStyle={{ width: '400px' }}
                >
                    <Form.Item
                        name="collectionAmount"
                    >
                        {edit ? <Amount><Input disabled/></Amount> : <ZText style={{ width: '100%' }}/>}
                    </Form.Item>
                </Descriptions.Item>
                <Descriptions.Item
                    label={starDom('保证金退款金额（元）', true, null)}
                    labelStyle={{ width: '180px' }}
                    contentStyle={{ width: '400px' }}
                >
                    <Form.Item
                        name="returnedAmount"
                        rules={[{ required: true, message: '保证金退款金额（元）必填' }]}
                    >
                        {
                            edit ? <Amount><InputNumber min={0} max={form.getFieldValue('collectionAmount')/10000} style={{ width: '100%' }}/></Amount> : <ZText style={{ width: '100%' }}/>
                        }
                    </Form.Item>
                </Descriptions.Item>
                <Descriptions.Item
                    label={starDom('保证金抵扣金额（元）', true, null)}
                    labelStyle={{ width: '180px' }}
                    contentStyle={{ width: '400px' }}
                >
                    <Form.Item
                        name="deductionAmount"
                        rules={[{ required: true, message: '保证金抵扣金额（元）必填' }]}
                    >

                        {
                            edit ? <Amount><InputNumber min={0} max={form.getFieldValue('collectionAmount')/10000} style={{ width: '100%' }} /></Amount> : <ZText style={{ width: '100%' }}/>
                        }
                    </Form.Item>
                </Descriptions.Item>
                <Descriptions.Item
                    label={starDom('是否回收保证金', false, null)}
                    labelStyle={{ width: '180px' }}
                    contentStyle={{ width: '400px' }}
                >
                    <Form.Item
                        name="recyclingFlag"
                    >
                        {edit ? <Select
                            disabled={form.getFieldValue('returnedAmount') / 10000 > 0}
                            options={[{label:'回收',value:'1'},{label:'不回收',value:'0'}]}
                            allowClear
                            /> : <ZText type='plain' style={{ width: '100%' }}/>}
                    </Form.Item>
                </Descriptions.Item>
            </Descriptions>
        </Form>
    )
}

export default Index
