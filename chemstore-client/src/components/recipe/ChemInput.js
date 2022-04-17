import classNames from "classnames"
import { useState } from "react"
import Select from "react-dropdown-select"

const ChemInput = props => {
    const { 
        units, 
        chemicals, 
        chemicalIngredient, 
        chemicalIngredients, 
        setChemicalIngredients, 
        handleOnRemove,
        isLast } = props

    const [ errors, setErrors ] = useState("")

    
    const updateListItemAttribute = (key, value) => {
        console.log(key)
        console.log(value)
        const updated = ({...chemicalIngredient})
        updated[key] = value
        const updatedList = chemicalIngredients.map(item => item.nr === chemicalIngredient.nr ? updated : item)
        setChemicalIngredients(updatedList)
    }
    
    return(
        <div className="form-group row mb-3" >
            <div className="col-sm-6" >
                <Select
                    options={chemicals}
                    labelField="shortName"
                    valueField="id"
                    placeholder="chemical"
                    searchable={false}
                    clearable={false}
                    style={{height: "42px", fontSize: "16px"}}
                    onChange={items => updateListItemAttribute("chemical", items[0])}
                />
            </div>
            <div className="col-sm-2" >
                <input 
                    name="amount"
                    value={chemicalIngredient.amount}
                    onChange={event =>  updateListItemAttribute("amount", event.target.value)}
                    type="number" 
                    step="0.001"
                    className={classNames("form-control form-control-lg", {"is-invalid": errors.amount})} 
                    placeholder="amount" 
                />
                {
                    (errors.amount && <div className="invalid-feedback">{errors.amount}</div>)
                }
            </div>
            <div className="col-sm-2">
                <Select
                    options={units}
                    labelField="unit"
                    placeholder="unit"
                    valueField="unit"
                    searchable={false}
                    clearable={false}
                    style={{height: "42px", fontSize: "16px"}}
                    onChange={items => updateListItemAttribute("unit", items[0].unit)}
                />
                {
                    (errors.unit && <div className="invalid-feedback">{errors.unit}</div>)
                }
            </div>
            <div className="col-sm-2">
                {!isLast && 
                    <button 
                        onClick={() => handleOnRemove(chemicalIngredient.nr)}
                        className="btn btn-outline-danger" 
                        style={{height: "42px", fontSize: "16px"}}>
                        <i className="fas fa-times-circle"></i>
                    </button>
                }
                
            </div>
        </div>
    )
}

export default ChemInput