import axios from "axios"
import { useEffect, useState } from "react"
import Select from "react-dropdown-select"
import { useSelector } from "react-redux"
import { useHistory } from "react-router-dom"
import { checkIfAdmin } from "../../../utils/securityUtils"

const ChemItemUpdateInputFields = props => {
    const { selectedLab, updateSetters, updateValues } = props
    const { arrivedBy, openingDate, openedBy, consumptionDate, consumedBy } = updateValues
    const { setArrivedBy, setOpeningDate, setOpenedBy, setConsumptionDate, setConsumedBy} = updateSetters

    const [ users, setUsers ] = useState([])

    useEffect(() => {
        if (selectedLab) {
            axios.get(`/api/logged-in/user?labKey=${selectedLab.key}`).then(result => {setUsers(result.data)})
        }
    }, [selectedLab])

    useEffect(() => {
        if (!openedBy) {
            setOpeningDate("")
            setConsumedBy("")
        }
    }, [openedBy])

    useEffect(() => {
        if (!openedBy) {
            setConsumptionDate("")
        }
    }, [consumedBy])

    const getOpeningDateInput = () => {
        if (!openedBy) {
            return
        }
        return (
            <div className="form-group row mb-3">
                <label htmlFor="openingDate" className="col-sm-4 col-form-label">Opening date</label>
                <div className="col-sm-8">
                    <input 
                        name="openingDate" 
                        value={openingDate}
                        onChange={event => setOpeningDate(event.target.value)}
                        type="date" 
                        className="form-control form-control-lg " 
                        />
                </div>
            </div> )
    }

    const getConsumtionDateInput = () => {
        if (!consumedBy) {
            return
        }
        return (
            <div className="form-group row mb-3">
                <label htmlFor="consumptionDate" className="col-sm-4 col-form-label">Consumtion date</label>
                <div className="col-sm-8">
                    <input 
                        name="consumptionDate" 
                        value={consumptionDate}
                        onChange={event => setConsumptionDate(event.target.value)}
                        type="date" 
                        className="form-control form-control-lg " 
                        />
                </div>
            </div> )
    }

    const getConsumedByInput = () => {
        if (!openedBy) {
            return
        }
        return (
            <div className="form-group row mb-3">
                <label htmlFor="consumedBy" className="col-sm-4 col-form-label">consumed by</label>
                <div className="col-sm-8">
                    <Select
                        options={users}
                        values={ consumedBy ? [ consumedBy ] : []}
                        labelField="fullName"
                        valueField="id"
                        placeholder="consumed by"
                        searchable={false}
                        clearable={true}
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={items => setConsumedBy(items[0] ? items[0] : "")}
                    />
                </div>
            </div>)
    }

    return(
        <div>
            <div className="form-group row mb-3">
                <label htmlFor="arrivedBy" className="col-sm-4 col-form-label">arrived by</label>
                <div className="col-sm-8">
                    <Select
                        options={users}
                        values={ arrivedBy ? [ arrivedBy ] : []}
                        labelField="fullName"
                        valueField="id"
                        placeholder="arrived by"
                        searchable={false}
                        clearable={false}
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={items => setArrivedBy(items[0] ? items[0] : "")}
                    />
                </div>
            </div>
            
            <div className="form-group row mb-3">
                <label htmlFor="openedBy" className="col-sm-4 col-form-label">opened by</label>
                <div className="col-sm-8">
                    <Select
                        options={users}
                        values={ openedBy ? [ openedBy ] : []}
                        labelField="fullName"
                        valueField="id"
                        placeholder="opened by"
                        searchable={false}
                        clearable={true}
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={items => setOpenedBy(items[0] ? items[0] : "")}
                    />
                </div>
            </div>
            {getOpeningDateInput()}
            {getConsumedByInput()}
            {getConsumtionDateInput()}
        </div>
    )
}

export default ChemItemUpdateInputFields