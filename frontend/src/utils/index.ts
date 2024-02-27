/**
 * 对话框标题动态内容字符限制
 * @param str 标题的动态内容
 * @returns 转化后的字符串
 */
export function characterLimit(str?: string): string {
    if (!str) return ''
    if (str.length <= 20) {
        return str
    }
    return `${str.slice(0, 20 - 3)}...`
}
/**
 * 当前日期是本年第几周
 * @returns
 */
export function getCurrentWeek() {
    const currentDate = new Date()
    const firstDayOfYear = new Date(currentDate.getFullYear(), 0, 1)
    const daysBetween = (currentDate.valueOf() - firstDayOfYear.valueOf()) / 86400000
    return Math.ceil(daysBetween / 7)
}
