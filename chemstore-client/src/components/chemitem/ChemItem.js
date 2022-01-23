import React, { Component } from 'react'

class ChemItem extends Component {
    

    render() {
        const chemItem = this.props.chemItem
        const chemical = chemItem.chemical
        return (
            <div className="container">
                <div className="card card-body bg-light mb-2" style={{padding: "2px"}}>
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
