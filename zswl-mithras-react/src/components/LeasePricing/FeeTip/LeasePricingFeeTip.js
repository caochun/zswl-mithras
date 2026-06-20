import { Tooltip } from 'antd'
import { QuestionCircleOutlined } from '@ant-design/icons'

const fieldMapTip = {
  consultingFee: '单独签订的咨询合同中约定的金额',
  commission: '业务主合同中约定的手续费金额',
  firstInstallmentInterest: '租金表中第0期收取的利息金额',
  interestWay: (
    <div>
      <div>1.实际利率法：按实际占用本金计算利息；</div>
      <div>2.平息法：按投放金额计算利息</div>
    </div>
  ),
}

const Index = ({ children, fieldName }) => {
  const tip = fieldMapTip[fieldName]
  return (
    <Tooltip title={tip}>
      {children}
      <span style={{ marginLeft: 5 }}>
        <QuestionCircleOutlined />
      </span>
    </Tooltip>
  )
}
Index.fieldMapTip = fieldMapTip

export default Index
