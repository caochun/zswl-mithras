const ReadOnly = ({ value, type, defaultValue = '-' }) => {
  return <div>{value ?? defaultValue}</div>
}

export default ReadOnly
