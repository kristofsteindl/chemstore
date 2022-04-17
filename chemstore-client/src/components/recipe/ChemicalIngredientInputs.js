import { Forest } from "@mui/icons-material"
import axios from "axios"
import { useEffect, useState } from "react"
import Select from "react-dropdown-select"
import { useSelector } from "react-redux"
import ChemItem from "../chemitem/ChemItem"
import ChemInput from "./ChemInput"

const ChemicalIngredientInputs = props => {
    
    const selectedLab = useSelector(state => state.selectedLab)

    const [ chemicals, setChemicals ] = useState([])
    const { chemicalIngredients, setChemicalIngredients, units, handleOnRemove } = props

    useEffect( () => {
        axios.get(`/api/logged-in/chemical/${selectedLab.key}`).then(result => setChemicals(result.data))
    }, [])

    
    return(
        <div>
            <h3 className="display-8">Chemical ingredients</h3>
            {chemicalIngredients.map(chemicalIngredient => 
                <ChemInput 
                    key={chemicalIngredient.nr}
                    chemicalIngredient={chemicalIngredient} 
                    chemicalIngredients={chemicalIngredients}
                    setChemicalIngredients={setChemicalIngredients} 
                    chemicals={chemicals} 
                    handleOnRemove={handleOnRemove} 
                    units={units}/>

            )}

        </div>
    )
}

export default ChemicalIngredientInputs