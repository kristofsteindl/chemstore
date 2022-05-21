import React, { useEffect, useState } from 'react'
import { check } from '../../../utils/securityUtils'
import axios from 'axios'
import { useHistory } from 'react-router-dom'
import { useSelector } from 'react-redux'
import ChemItemCoreInputFields from './ChemItemCoreInputFields'

const AddChemItem = () => {
    const [ chemicalShortName, setChemicalShortName ] = useState("") 
    const [ manufacturerId, setManufacturerId ] = useState("") 
    const [ unit, setUnit ] = useState("") 
    const [ amount, setAmount ] = useState(1) 
    const [ quantity, setQuantity ] = useState(0) 
    const [ batchNumber, setBatchNumber ] = useState("") 
    const [ expirationDateBeforeOpened, setExpirationDateBeforeOpened ] = useState("") 
    const [ arrivalDate, setArrivalDate ] = useState(new Date().toISOString().split('T')[0]) 
    const [ errors, setErrors ] = useState("") 

    const selectedLab = useSelector(state => state.selectedLab)
    const history = useHistory()
    

    useEffect(() => {
        check()
    })

    const onSubmit = async e => {
        check()
        e.preventDefault()
        const newChemItem = {
            labKey: selectedLab.key,
            chemicalShortName: chemicalShortName,
            manufacturerId: manufacturerId,
            unit: unit,
            amount: amount,
            quantity: quantity,
            batchNumber: batchNumber,
            expirationDateBeforeOpened: expirationDateBeforeOpened,
            arrivalDate: arrivalDate
        }
        await axios.post(`/api/chem-item`, newChemItem)
            .then(() =>  history.push('/chem-items'))
            .catch(error => setErrors(error.response.data))   
    }

    const setters = {
        setChemicalShortName: setChemicalShortName,
        setManufacturerId: setManufacturerId,
        setBatchNumber: setBatchNumber,
        setQuantity: setQuantity,
        setUnit: setUnit,
        setAmount: setAmount,
        setExpirationDateBeforeOpened: setExpirationDateBeforeOpened,
        setArrivalDate: setArrivalDate
    }

    const values = {
        batchNumber: batchNumber,
        quantity: quantity,
        amount: amount,
        expirationDateBeforeOpened: expirationDateBeforeOpened,
        arrivalDate: arrivalDate,
    }

    return (
        <div className="col-md-8 m-auto">
            <h5 className="display-4 text-center">{`Register chemical into ${selectedLab.name}`}</h5>
            <hr />
            <br/>   
            <form onSubmit={onSubmit}>
                <ChemItemCoreInputFields
                    selectedLab={selectedLab}
                    setters={setters}
                    values={values}
                    history={history}
                />

                <input type="submit" className="btn btn-primary btn-block mt-4" />
            </form>
        </div>
    )
    
}

export default AddChemItem
