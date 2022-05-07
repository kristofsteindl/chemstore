import { IconButton } from '@mui/material'
import DeleteIcon from '@mui/icons-material/Delete';
import axios from 'axios'
import React, { useState } from 'react'
import { isAvailable, isExpired } from '../../utils/chem-item-utils'
import VerifyPanel from '../UI/VerifyPanel';
import "./ChemItem.css"
import { Button } from "@mui/material"
import useCollapse from 'react-collapsed';
  

const ChemItem = props => {
    const [chemItem, setChemItem] = useState(props.chemItem)
    const [activeModal, setActiveModal] = useState("")
    const { getCollapseProps, getToggleProps } = useCollapse();
    
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
        } else if (isExpired(chemItem)) {
            return (<div></div>)
        } else {
            return (
                <Button onClick={() => setActiveModal("OPEN")} className="btn action-button" variant="outlined" size="medium">
                    <i className="fa fa-edit">Open</i>
                </Button>
            )
        }
    }

    const getConsumeContent = chemItem => {
        if (!chemItem.openingDate && !isExpired(chemItem)) {
            return (<div></div>)
        } else if (chemItem.openingDate && !chemItem.consumptionDate) {
            return (
                <Button className="btn action-button" color="error" variant="outlined" size="medium" onClick={() => setActiveModal("CONSUME")}  >
                    <i className="fa fa-minus-circle">Consume</i>
                </Button>)
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
                <div className="row"  {...getToggleProps()}>
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
                <div {...getCollapseProps()}>
                    <div className="row content" style={{padding: "10px"}}>
                        <div className="col-2">
                            <i className="mx-auto">{chemical.exactName}</i>
                        </div>
                        <div className="col-sm-1" />
                            
                        <div className="col-1">
                            <i className="mx-auto">by {chemItem.arrivedBy.fullName}</i>
                        </div>
                        
                        <div className="col-sm-4" />
                            
                        <div className="col-sm-1" />
                            
                        <div className="col-sm-1" >
                            <i className="mx-auto">{chemItem.openedBy && `by ${chemItem.openedBy.fullName}`}</i>
                           
                        </div>
                        <div className="col-sm-1" />
                            
                        <div className="col-sm-1" />
                            
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
