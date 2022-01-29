import axios from 'axios'
import React, { Component } from 'react'

class ChemItem extends Component {
    constructor(props) {
        super(props)
        this.state = {
            chemItem: props.chemItem
        }
    }
    
    getExpDate(chemItem) {
        if (chemItem.expirationDate) {
            return (<strong>{chemItem.expirationDate}</strong>)
        }
        return (<p>{chemItem.expirationDateBeforeOpened}</p>)
    }

    getOpenContent(chemItem) {
        if (chemItem.openingDate) {
            return (<div>{chemItem.openingDate}</div>)
        } else {
            return (
                <span onClick={() => this.openChemItem(chemItem)}>
                    <li className="list-group-item update" style={{padding: "0.01rem 1rem"}}>              
                        <i className="fa fa-edit pr-1">Open</i>
                    </li>
                </span>)
        }
    }

    getConsumeContent(chemItem) {
        if (!chemItem.openingDate && !this.isExpired(chemItem)) {
            return (<div></div>)
        } else if (chemItem.openingDate && !chemItem.consumptionDate) {
            return (
                <span onClick={() => this.consumeChemItem(chemItem)}>
                    <li className="list-group-item update" style={{padding: "0.01rem 0.5rem"}}>              
                        <i className="fa fa-minus-circle pr-1">Consume</i>
                    </li>
                </span>)
        } else {
            return (<div>{chemItem.consumptionDate}</div>)
        }
    }


    openChemItem(chemItem) {
        const id = chemItem.id
        if (window.confirm(`Are you sure you want to open '${chemItem.chemical.shortName}' (${chemItem.manufacturer.name}, ${chemItem.batchNumber}/${chemItem.seqNumber})?`)) {
            axios.patch(`/api/chem-item/open/${id}`).then(result => this.setState({chemItem: result.data}))
        }
    }

    consumeChemItem(chemItem) {
        const id = chemItem.id
        if (window.confirm(`Are you sure you want to consume '${chemItem.chemical.shortName}' (${chemItem.manufacturer.name}, ${chemItem.batchNumber}/${chemItem.seqNumber})?`)) {
            axios.patch(`/api/chem-item/consume/${id}`).then(result => this.setState({chemItem: result.data}))
        }
    }

    isExpired(chemItem) {
        const expDate = new Date(chemItem.expirationDate)
        const expDateBeforeOpened = new Date(chemItem.expirationDateBeforeOpened)
        const now = Date.now()
        const exipred = 
            expDateBeforeOpened < now ||
            (chemItem.expirationDate && expDate < now )
        return exipred    
    }

    render() {
        const chemItem = this.state.chemItem
        const chemical = chemItem.chemical
        const expDate = new Date(chemItem.expirationDate)
        const expDateBeforeOpened = new Date(chemItem.expirationDateBeforeOpened)
        const now = Date.now()
        const available = !chemItem.consumptionDate && ! this.isExpired(chemItem)
        const style = {
                padding: "2px",
                color: available ? "black" : "#999999"
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
                        <div className="col-sm-1">
                           {this.getExpDate(this.state.chemItem)}
                        </div>
                        <div className="col-sm-1">
                            {this.getOpenContent(this.state.chemItem)}
                        </div>
                        <div className="col-sm-1">
                            {this.getConsumeContent(this.state.chemItem)}
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}

export default ChemItem 
