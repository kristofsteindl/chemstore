
export const getShelfLife = (category) => {
    const days = getDays(category.shelfLife)
    if (days > 365) {
        return `${Math.round(days / 365)} days`
    } else if (days > 30) {
        return `${days / 30} months`
    }
    return `${days} days`
} 


export const getDays = (javaDuration) => {
    return javaDuration.split('H')[0].substring(2) / 24
}