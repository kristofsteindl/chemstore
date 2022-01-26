import React, { Component } from 'react'

class ChemItem extends Component {
    

    render() {
        const chemItem = this.props.chemItem
        const chemical = chemItem.chemical
        const expDate = new Date(chemItem.expirationDate)
        const expDateBeforeOpened = new Date(chemItem.expirationDateBeforeOpened)
        const now = Date.now()
        const available = 
            !chemItem.consumptionDate && 
            expDateBeforeOpened > now &&
            (!chemItem.expirationDate || expDate > now )
        console.log(!chemItem.consumptionDate)
        console.log(expDateBeforeOpened > now)
        console.log(!expDate)
        console.log(expDate > now )
        console.log("----")

        const style = {
                padding: "2px",
                color: available ? "black" : "grey"
            }
        
        return (
            <div className="container">
                <div className="card card-body bg-light mb-2" style={style}>
                    <div className="row" >
                        <div className="col-2">
                            <h4 className="mx-auto">{chemical.shortName}</h4>
                        </div>
                        <div className="col-sm-1">
                            <i>{chemItem.quantity} {chemItem.unit}</i>
                        </div>
                        <div className="col-1">
                            <span className="mx-auto">{chemItem.arrivalDate}</span>
                        </div>
                        <div className="col-sm-2">
                            <p>{chemItem.manufacturer.name}</p>
                        </div>
                        <div className="col-sm-2">
                            <p>{chemItem.batchNumber}/{chemItem.seqNumber}</p>
                        </div>
                        <div className="col-sm-2">
                           <p> {chemItem.expirationDateBeforeOpened}</p>
                        </div>
                        <div className="col-sm-2">
                            
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default ChemItem 
