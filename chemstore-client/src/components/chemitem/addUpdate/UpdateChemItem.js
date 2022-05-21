import React, { useEffect, useState } from 'react'
import { check } from '../../../utils/securityUtils'
import axios from 'axios'
import { useHistory } from 'react-router-dom'
import { useSelector } from 'react-redux'
import ChemItemCoreInputFields from './ChemItemCoreInputFields'
import Select from 'react-dropdown-select'
import ChemItemUpdateInputFields from './ChemItemUpdateInputFields'

const UpdateChemItem = props => {
    const chemItemId = props.match.params.id

    const [ chemItem, setChemItem ] = useState()
    
    const [ manufacturerId, setManufacturerId ] = useState("") 
    const [ unit, setUnit ] = useState("") 
    const [ quantity, setQuantity ] = useState(0) 
    const [ batchNumber, setBatchNumber ] = useState("") 
    const [ expirationDateBeforeOpened, setExpirationDateBeforeOpened ] = useState("") 
    
    const [ arrivalDate, setArrivalDate ] = useState("") 
    const [ arrivedBy, setArrivedBy ] = useState("") 
    const [ openingDate, setOpeningDate ] = useState("") 
    const [ openedBy, setOpenedBy ] = useState("") 
    const [ consumptionDate, setConsumptionDate ] = useState("") 
    const [ consumedBy, setConsumedBy ] = useState("") 
    
    const [ errors, setErrors ] = useState("") 

    const selectedLab = useSelector(state => state.selectedLab)
    const history = useHistory()
    
    useEffect(() => {
        if (selectedLab) {
            axios.get(`/api/chem-item/${selectedLab.key}/${chemItemId}`).then(result => {setChemItem(result.data)})   
        }
    }, [])

    useEffect(() => {
        if (chemItem) {
            setManufacturerId(chemItem.manufacturer.id)
            setUnit(chemItem.unit)
            setQuantity(chemItem.quantity)
            setBatchNumber(chemItem.batchNumber)
            setExpirationDateBeforeOpened(chemItem.expirationDateBeforeOpened)
            
            setArrivalDate(chemItem.arrivalDate)
            setArrivedBy(chemItem.arrivedBy)
            setOpeningDate(chemItem.openingDate)
            setOpenedBy(chemItem.openedBy)
            setConsumptionDate(chemItem.consumptionDate)
            setConsumedBy(chemItem.consumedBy)
        }
    
    }, [chemItem])

    useEffect(() => {
        check()
    })

    const onSubmit = async e => {
        check()
        e.preventDefault()
        const updatedChemItem = {
            labKey: chemItem.lab.key,
            chemicalShortName: chemItem.chemical.shortName,
            manufacturerId: manufacturerId,
            unit: unit,
            amount: 1,
            quantity: quantity,
            batchNumber: batchNumber,
            expirationDateBeforeOpened: expirationDateBeforeOpened,
            arrivalDate: arrivalDate,
            arrivedByUsername: arrivedBy.username,
            openingDate: openingDate,
            openedByUsername: openedBy ? openedBy.username  : "",
            consumptionDate: consumptionDate,
            consumedByUsername: consumedBy ? consumedBy.username : ""
        }
        await axios.put(`/api/chem-item/${chemItem.id}`, updatedChemItem)
            .then(() =>  history.push('/chem-items'))
            .catch(error => setErrors(error.response.data))   
    }

    const setters = {
        setManufacturerId: setManufacturerId,
        setBatchNumber: setBatchNumber,
        setQuantity: setQuantity,
        setUnit: setUnit,
        setExpirationDateBeforeOpened: setExpirationDateBeforeOpened,
        setArrivalDate: setArrivalDate
    }

    const updateSetters = {
        setArrivedBy: setArrivedBy,
        setOpeningDate: setOpeningDate,
        setOpenedBy: setOpenedBy,
        setConsumptionDate: setConsumptionDate,
        setConsumedBy: setConsumedBy,
    }

    const values = {
        unit: chemItem && {unit: chemItem.unit},
        manufacturer: chemItem && chemItem.manufacturer,
        batchNumber: batchNumber,
        quantity: quantity,
        expirationDateBeforeOpened: expirationDateBeforeOpened,
        arrivalDate: arrivalDate,
    }

    const updateValues = {
        arrivedBy: arrivedBy,
        openingDate: openingDate,
        openedBy: openedBy,
        consumptionDate: consumptionDate,
        consumedBy: consumedBy
    }


    return (
        <div className="col-md-8 m-auto">
            <h5 className="display-4 text-center">{`Update registered chemical (chem item) in ${selectedLab.name}`}</h5>
            <hr />
            <br/>   
            {
                (errors.message && <h5 className="text-danger">{errors.message}</h5>)
            }
            <form onSubmit={onSubmit}>
                <ChemItemCoreInputFields
                    chemItem={chemItem}
                    selectedLab={selectedLab}
                    setters={setters}
                    values={values}
                    history={history}
                />
                <ChemItemUpdateInputFields
                    selectedLab={selectedLab}
                    updateSetters={updateSetters}
                    updateValues={updateValues}
                />

                <input type="submit" className="btn btn-primary btn-block mt-4" />
            </form>
        </div>
    )
    
}

export default UpdateChemItem
