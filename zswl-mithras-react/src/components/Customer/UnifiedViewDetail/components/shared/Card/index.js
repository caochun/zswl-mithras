import { Card, Typography } from 'antd'
import './CardContainer.less'
import { observer } from '@zswl/admin'

const { Text } = Typography

const CustomCard = ({ icon, title, current, total, red, single, onlyTotal }) => {
  return (
    <Card className={red ? 'red-custom-card' : 'custom-card'} bordered={false}>
      <div className="card-content">
        <div className="card-icon">{icon}</div>
        <div className="card-data">
          <div className="card-number">
            {onlyTotal ? (
              <Text strong className="current">
                {total}
              </Text>
            ) : (
              <>
                <Text strong className="current">
                  {current}
                </Text>
                {<Text className="total">/{total}</Text>}
              </>
            )}
          </div>
          <div className="card-title">{title}</div>
        </div>
      </div>
    </Card>
  )
}

export default observer(CustomCard)
