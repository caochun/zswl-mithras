import React from 'react'
import { Input } from 'antd'
import { LockOutlined, EyeInvisibleOutlined, EyeOutlined } from '@ant-design/icons'

class PasswordInput extends React.Component {
  static getDerivedStateFromProps(nextProps) {
    if ('value' in nextProps) {
      return {
        ...(nextProps.value || {}),
      }
    }
    return null
  }
  constructor(props) {
    super(props)
    const value = props.value
    this.state = {
      showValue: value,
      value: '',
      showPassword: false,
    }
  }
  componentWillReceiveProps(newProps) {
    if (newProps.value === '') {
      this.setState({ value: '', showValue: '' })
    }
  }

  handleChange = (e) => {
    const isDeleteContentBackward = e.nativeEvent.inputType === 'deleteContentBackward'
    let newValue
    const value = e.target.value
    const currValue = value[value.length - 1] // 当前值
    const showValue = new Array(value.length + 1).join('•')
    const oldValue = this.state.value
    // 删除键
    if (isDeleteContentBackward) {
      if (this.state.showPassword) {
        newValue = value
        this.setState({ value: value, showValue })
        this.triggerChange(newValue)
        return
      } else {
      }
    }

    if (currValue !== '•') {
      newValue = oldValue.substr(0, showValue.length - 1) + currValue
    } else {
      // 没办法坚听到哪个位置，只能取最后一个了
      newValue = oldValue.substr(0, showValue.length)
    }
    if (newValue === 'undefined') newValue = ''
    this.setState({ value: newValue, showValue })
    this.triggerChange(newValue)
  }
  triggerChange = (changedValue) => {
    const onChange = this.props.onChange
    if (onChange) {
      onChange(changedValue)
    }
  }
  switchShow = () => {
    this.setState({ showPassword: !this.state.showPassword })
  }
  render() {
    return (
      <span>
        <Input
          prefix={<LockOutlined />}
          suffix={
            <div onClick={this.switchShow} style={{ cursor: 'pointer' }}>
              {this.state.showPassword ? <EyeOutlined /> : <EyeInvisibleOutlined />}
            </div>
          }
          onPaste={(e) => e.preventDefault()}
          onCopy={(e) => e.preventDefault()}
          onContextMenu={(e) => e.preventDefault()}
          value={this.state.showPassword ? this.state.value : this.state.showValue}
          type="text"
          onChange={this.handleChange}
          id="myPassword"
        />
      </span>
    )
  }
}
export default PasswordInput
