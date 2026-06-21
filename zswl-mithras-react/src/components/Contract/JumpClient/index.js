import { Tooltip } from 'antd'
import { history } from '@zswl/admin'
import styles from './index.less'

const ContractJumpClient = ({ value, isChange }) => {
  if (value?.length > 0) {
    return value.map((item, index) => {
      if (!item.clientName) {
        return '-'
      }
      const JumpA = (
        <a
          style={{ color: isChange ? 'red' : '#2552e6' }}
          onClick={() => {
            history.push(
              `/customer/maintain/detail/${item.clientId}?clientType=${item.clientType}&flag=info&typeId=create`
            )
          }}
        >
          {item.clientName}
        </a>
      )

      return (
        <div key={index} className={styles.ellipsis}>
          <Tooltip title={item.clientName} placement="topLeft">
            {JumpA}
          </Tooltip>
        </div>
      )
    })
  } else {
    return '-'
  }
}

export default ContractJumpClient
