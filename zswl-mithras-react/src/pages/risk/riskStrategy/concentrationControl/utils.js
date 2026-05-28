import moment from 'moment'

const date = new Date()
const year = date.getFullYear()
const mouth = date.getMonth()

export const TIME_POINT = moment().year(year).month(mouth).date(1)
