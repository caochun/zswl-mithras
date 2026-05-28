import { useEffect } from 'react'
import { Form, Input, InputNumber, Descriptions, Button } from 'antd'
import { formatPercent, amountFormat } from '@/utils'
import Api from '../api'
import styles from '../refundScheme/style.less'
const Index = ({ id }) => {
    const [form] = Form.useForm()
    useEffect(async() => {
        const res = await Api.depostInfo(id)
        if(res){
            form?.setFieldsValue({
                ...res,
                collectionAmount:amountFormat(formatPercent(res.collectionAmount)),
                returnedAmount:amountFormat(formatPercent(res.returnedAmount)),
                deductionAmount:amountFormat(formatPercent(res.deductionAmount)),
            })
        }
    } ,[])
    return (
        <Form
            form={form}
        >
            <Descriptions
                bordered
                column={2}
                labelStyle={{ background: '#F5F6FA' }}
                size={'small'}
                className={styles.des}
            >
                <Descriptions.Item
                    label={'客户名称'}
                    labelStyle={{ width: '180px' }}
                    contentStyle={{ width: '400px' }}
                >
                    <Form.Item
                        name="clientName"
                    >
                        <Input disabled/>
                    </Form.Item>
                </Descriptions.Item>
                <Descriptions.Item
                    label={'合同编号'}
                    labelStyle={{ width: '180px' }}
                    contentStyle={{ width: '400px' }}
                >
                    <Form.Item
                        name="contractCode"
                    >
                        <Input disabled/>
                    </Form.Item>
                </Descriptions.Item>
                <Descriptions.Item
                    label={'保证金余额（元）'}
                    labelStyle={{ width: '180px' }}
                    contentStyle={{ width: '400px' }}
                >
                    <Form.Item
                        name="collectionAmount"
                    >
                        <Input disabled/>
                    </Form.Item>
                </Descriptions.Item>
                <Descriptions.Item
                    label={'保证金退款金额（元）'}
                    labelStyle={{ width: '180px' }}
                    contentStyle={{ width: '400px' }}
                >
                    <Form.Item
                        name="returnedAmount"
                    >
                        <InputNumber min={0} style={{ width: '100%' }} disabled/>
                    </Form.Item>
                </Descriptions.Item>
                <Descriptions.Item
                    label={'保证金抵扣金额（元）'}
                    labelStyle={{ width: '180px' }}
                    contentStyle={{ width: '400px' }}
                >
                    <Form.Item
                        name="deductionAmount"
                    >
                        <InputNumber min={0} style={{ width: '100%' }} disabled/>
                    </Form.Item>
                </Descriptions.Item>
            </Descriptions>
        </Form>
    )
}
export default Index