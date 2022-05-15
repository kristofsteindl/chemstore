import axios from 'axios'
import React, { useState } from 'react'
import { isAvailable } from '../../utils/chem-item-utils'
import VerifyPanel from '../UI/VerifyPanel';
import "./ChemItem.css"
import useCollapse from 'react-collapsed';
import ChemItemFirstRow from './ChemItemFirstRow';
import ChemItemSecondRow from './ChemItemSecondRow';
  

const ChemItem = props => {
    const [chemItem, setChemItem] = useState(props.chemItem)
    const [activeModal, setActiveModal] = useState("")
    const { getCollapseProps, getToggleProps } = useCollapse();

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
                <ChemItemFirstRow 
                    chemItem={chemItem}
                    setActiveModal={setActiveModal}
                    isManager={props.isManager}
                    getToggleProps={getToggleProps}
                />
                <ChemItemSecondRow 
                    chemItem={chemItem}
                    getCollapseProps={getCollapseProps}
                />
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
