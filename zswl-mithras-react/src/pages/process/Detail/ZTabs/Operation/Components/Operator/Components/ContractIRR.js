import { InputNumber, Input, Row, Col } from 'antd'
import { numToFixed, formatPercent, hasValue } from '@/utils'
const Index = (value) => {
    const lowestIrr = hasValue(value.value?.lowestIrr) ? numToFixed(formatPercent(value.value?.lowestIrr)) : ''
    const averageIrr = hasValue(value.value?.averageIrr) ? numToFixed(formatPercent(value.value?.averageIrr)) : ''
    return (
        <Input.Group>
            <Row gutter={8}>
            <Col span={7}>
                <Input value={lowestIrr} style={{width:'100%'}} addonBefore='最低IRR' addonAfter="%" disabled/>
            </Col>
            <Col span={7}>
                <Input value={averageIrr} style={{width:'100%'}} addonBefore='合同IRR' addonAfter="%" disabled/>
            </Col>
            </Row>
        </Input.Group>
    )
}

export default Index