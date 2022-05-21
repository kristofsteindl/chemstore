import axios from "axios"
import { useEffect, useState } from "react"
import Select from "react-dropdown-select"
import { useSelector } from "react-redux"
import { useHistory } from "react-router-dom"
import { checkIfAdmin } from "../../../utils/securityUtils"

const ChemItemCoreInputFields = props => {
    const { chemItem, selectedLab, setters, values, history } = props
    const { chemical, manufacturer, unit, batchNumber, quantity, amount, expirationDateBeforeOpened, arrivalDate } = values
    const { setChemicalShortName, setManufacturerId, setBatchNumber, setQuantity, setUnit, setAmount, setExpirationDateBeforeOpened, setArrivalDate} = setters
    
    const user = useSelector((state) => state.security.user)

    const [ chemicals, setChemicals ] = useState([]) 
    const [ manufacturers, setManufacturers ] = useState([]) 
    const [ units, setUnits ] = useState([]) 

    const loadDropDowns = selectedLab => {
        axios.get(`/api/logged-in/chemical/${selectedLab.key}`)
            .then(result => setChemicals(result.data))
        axios.get(`/api/logged-in/manufacturer`)
            .then(result => setManufacturers(result.data))
        axios.get(`/api/chem-item/unit`)
            .then(result => setUnits(result.data.map(unit => {return {"unit": unit}})))
    }
    
    useEffect(() => {
        if (selectedLab && user && checkIfAdmin(selectedLab, user)) {
            loadDropDowns(selectedLab)
        } else {
            history.push("/chem-items")
        }
    }, [selectedLab, user])

    return(
        <div>
            <div className="form-group row mb-3">
                <label htmlFor="chemical" className="col-sm-4 col-form-label">chemical</label>
                <div className="col-sm-8">
                    <Select
                        options={chemicals}
                        values={chemical ? [ chemical ] : []}
                        disabled={chemItem}
                        labelField="shortName"
                        valueField="shortName"
                        placeholder="chemical"
                        searchable={false}
                        clearable={false}
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={(items) => items[0] && setChemicalShortName(items[0].shortName)}
                    />
                </div>
            </div>

            <div className="form-group row mb-3">
                <label htmlFor="manufacturer" className="col-sm-4 col-form-label">manufacturer</label>
                <div className="col-sm-8">
                    <Select
                        options={manufacturers}
                        values={manufacturer ? [ manufacturer ] : []}
                        searchable={false}
                        clearable={false}
                        labelField="name"
                        valueField="name"
                        placeholder="manufacturer"
                        searchBy="name"
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={(items) => items[0] && setManufacturerId(items[0].id )}
                    />
                </div>
            </div>

            <div className="form-group row mb-3">
                <label htmlFor="batchNumber" className="col-sm-4 col-form-label">batch number</label>
                <div className="col-sm-8">
                    <input 
                        name="batchNumber" 
                        value={batchNumber}
                        onChange={event => setBatchNumber(event.target.value)}
                        type="text" 
                        className="form-control form-control-lg " 
                        placeholder="batch number" />
                </div>
            </div>

            <div className="form-group row mb-3">
                <label htmlFor="quantity" className="col-sm-4 col-form-label">quantity</label>
                <div className="col-sm-8">
                    <input 
                        name="quantity" 
                        value={quantity}
                        onChange={event => setQuantity(event.target.value)}
                        type="text" 
                        className="form-control form-control-lg " 
                        placeholder="quantity" />
                </div>
            </div>

            <div className="form-group row mb-3">
                <label htmlFor="unit" className="col-sm-4 col-form-label">unit</label>
                <div className="col-sm-8">
                    <Select
                        options={units}
                        values={unit ? [ unit ] : []}
                        labelField="unit"
                        placeholder="unit"
                        valueField="unit"
                        searchable={false}
                        clearable={false}
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={items => items[0] && setUnit(items[0].unit)}
                    />
                </div>
            </div>

            
            {!chemItem && 
                (<div className="form-group row mb-3">
                    <label htmlFor="amount" className="col-sm-4 col-form-label">amount</label>
                    <div className="col-sm-8">
                            <input 
                                name="amount" 
                                value={amount}
                                onChange={event => setAmount(event.target.value)}
                                type="text" 
                                className="form-control form-control-lg " 
                                placeholder="amount" />
                            
                        
                    </div>
                </div>)}
            
            
            <div className="form-group row mb-3">
                <label htmlFor="expirationDateBeforeOpened" className="col-sm-4 col-form-label">Expiration date (before opened)</label>
                <div className="col-sm-8">
                    <input 
                        name="expirationDateBeforeOpened" 
                        value={expirationDateBeforeOpened}
                        onChange={event => setExpirationDateBeforeOpened(event.target.value)}
                        type="date" 
                        className="form-control form-control-lg " 
                        />
                </div>
            </div>

            <div className="form-group row mb-3">
                <label htmlFor="arrivalDate" className="col-sm-4 col-form-label">Arrival date</label>
                <div className="col-sm-8">
                    <input 
                        name="arrivalDate" 
                        value={arrivalDate}
                        onChange={event => setArrivalDate(event.target.value)}
                        type="date" 
                        className="form-control form-control-lg " 
                        />
                </div>
            </div>
        </div>
    )
}

export default ChemItemCoreInputFields