
export const getShelfLife = (category) => {
    const days = getDays(category.shelfLife)
    return `${Math.round(days)} days`
} 


export const getDays = (javaDuration) => {
    return javaDuration.split('H')[0].substring(2) / 24
}