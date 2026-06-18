import { useEffect, useMemo, useRef, useState } from 'react'
import { Input } from 'antd'
import { DownOutlined, CloseCircleOutlined } from '@ant-design/icons'
import styles from './index.less'

const DatePicker = ({ initValue, disabled = false, onChange, ...rest }) => {
  const [isOpen, setIsOpen] = useState(false)
  const [selectedDate, setSelectedDate] = useState(initValue)
  const [isHovered, setIsHovered] = useState(false)
  const datePickerRef = useRef()

  const handleDateClick = (date) => {
    setSelectedDate(date)
    setIsOpen(false)
    onChange?.(date)
  }

  const openPanel = () => {
    if (disabled) return
    setIsOpen(true)
  }

  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (datePickerRef.current && !datePickerRef.current.contains(event.target)) {
        setIsOpen(false)
      }
    }
    document.addEventListener('mousedown', handleOutsideClick)
    return () => {
      document.removeEventListener('mousedown', handleOutsideClick)
    }
  }, [])

  const handleMouseEnter = () => {
    setIsHovered(true)
  }

  const handleMouseLeave = () => {
    setIsHovered(false)
  }

  const renderDatePanel = useMemo(() => {
    const dates = Array.from({ length: 31 }, (_, index) => index + 1)
    return (
      <div className={styles['date-panel']}>
        <div></div>
        <div></div>
        {dates.map((date) => (
          <div
            key={date}
            className={`${styles['date']} ${selectedDate === date ? `${styles['selected']}` : ''}`}
            onClick={() => handleDateClick(date)}
          >
            {date}
          </div>
        ))}
      </div>
    )
  }, [selectedDate])

  const handleClearDate = (event) => {
    event.stopPropagation()
    setSelectedDate('')
    onChange?.()
  }

  const renderSuffix = () => {
    if (isHovered && selectedDate && !disabled) {
      return <CloseCircleOutlined onClick={handleClearDate} className={styles.icon} />
    }
    return <DownOutlined className={styles.icon} />
  }

  return (
    <div
      className={styles['date-picker']}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      ref={datePickerRef}
    >
      <Input
        type="text"
        readOnly
        value={selectedDate}
        onClick={openPanel}
        suffix={renderSuffix()}
        {...rest}
      />
      {isOpen && <div className={styles['pick-mask']}>{renderDatePanel}</div>}
    </div>
  )
}

export default DatePicker
