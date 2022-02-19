export const isAvailable = chemItem => {
    return !chemItem.consumptionDate && ! isExpired(chemItem)
}

export const isExpired = chemItem => {
    const expDate = new Date(chemItem.expirationDate)
    const expDateBeforeOpened = new Date(chemItem.expirationDateBeforeOpened)
    let today = new Date().setHours(0,0,0,0)
    const expired = 
        expDateBeforeOpened < today ||
        (chemItem.expirationDate && expDate < today )
    return expired    
}