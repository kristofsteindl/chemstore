import { IconButton } from '@mui/material'
import DeleteIcon from '@mui/icons-material/Delete';
import axios from 'axios'
import React, { useState } from 'react'
import { isAvailable, isExpired } from '../../utils/chem-item-utils'
import VerifyPanel from '../UI/VerifyPanel';
import "./ChemItem.css"

const ChemItem = props => {
    const [chemItem, setChemItem] = useState(props.chemItem)
    const [activeModal, setActiveModal] = useState("")
    
    const getExpDate = chemItem => {
        if (chemItem.expirationDate) {
            return (<strong>{chemItem.expirationDate}</strong>)
        }
        return (<p>{chemItem.expirationDateBeforeOpened}</p>)
    }

    const getDeleteContent = () => {
        if (!props.isManager) {
            return <div></div>
        }
        return (
            <span  onClick={() => setActiveModal("DELETE")}>
                <li className="list-group-item action-button delete">              
                    <IconButton aria-label="delete" size="small">
                        <DeleteIcon fontSize="inherit"/>
                    </IconButton>
                </li>
            </span>
        )
    }

    const getOpenContent = chemItem => {
        if (chemItem.openingDate) {
            return (<div>{chemItem.openingDate}</div>)
        } else {
            return (
                <span onClick={() => setActiveModal("OPEN")}>
                    <li className="list-group-item update action-button">              
                        <i className="fa fa-edit pr-1">Open</i>
                    </li>
                </span>
            )
        }
    }

    const getConsumeContent = chemItem => {
        if (!chemItem.openingDate && !isExpired(chemItem)) {
            return (<div></div>)
        } else if (chemItem.openingDate && !chemItem.consumptionDate) {
            return (
                <span onClick={() => setActiveModal("CONSUME")}>
                    <li className="list-group-item update action-button">              
                        <i className="fa fa-minus-circle pr-1">Consume</i>
                    </li>
                </span>)
        } else {
            return (<div>{chemItem.consumptionDate}</div>)
        }
    }

    const getVerifyMessage = action => {
        return (
            <div>
                <p>{`Are you sure you want to ${action} `}<b>{chemItem.chemical.shortName}</b></p>
                <p>(<b>{chemItem.manufacturer.name}</b>, <b>{`${chemItem.batchNumber}/${chemItem.seqNumber}`}</b>)</p>
            </div>
        )
    }

    const openChemItem = () => {
        axios.patch(`/api/chem-item/open/${chemItem.id}`).then(result => setChemItem(result.data))
        setActiveModal("")
    }

    const consumeChemItem = () => {
        axios.patch(`/api/chem-item/consume/${chemItem.id}`).then(result => setChemItem(result.data))
        setActiveModal("")
    }

    const deleteChemItem = () => {
        props.deleteChemItem(chemItem.id)
        setActiveModal("")
    }

    const chemical = chemItem.chemical
        
    return (
        <div className="container">
            <div className={`card card-body bg-light mb-2 chem-item ${isAvailable(chemItem) ? "" : "unavailable"}`}>
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
                        {getExpDate(chemItem)}
                    </div>
                    <div className="col-sm-1">
                        {getOpenContent(chemItem)}
                    </div>
                    <div className="col-sm-1">
                        {getConsumeContent(chemItem)}
                    </div>
                    <div className="col-sm-1">
                        {getDeleteContent()}
                    </div>


                </div>
            </div>
            {activeModal === "CONSUME" && 
                <VerifyPanel 
                    onCancel={() => setActiveModal("")} 
                    veryfyMessage={getVerifyMessage("consume")}
                    onSubmit={consumeChemItem}
                    buttonLabel="Consume"
                />}
            {activeModal === "OPEN" && 
                <VerifyPanel 
                    onCancel={() => setActiveModal("")} 
                    veryfyMessage={getVerifyMessage("open")}
                    onSubmit={openChemItem}
                    buttonLabel="Open"
                />}
            {activeModal === "DELETE" && 
                <VerifyPanel 
                    onCancel={() => setActiveModal("")} 
                    veryfyMessage={getVerifyMessage("delete")}
                    onSubmit={deleteChemItem}
                    buttonLabel="Delete"
                />}
        </div>
    )
    

}

export default ChemItem 
