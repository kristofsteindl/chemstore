import axios from "axios"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import ChemInput from "./ChemInput"

const ChemicalIngredientInputs = props => {
    
    const selectedLab = useSelector(state => state.selectedLab)

    const { 
        chemicalIngredients, 
        setChemicalIngredients, 
        recipeIngredients, 
        setRecipeIngredients, 
        units, 
        handleChemicalOnRemove, 
        handleRecipeOnRemove } = props

    const [ chemicals, setChemicals ] = useState([])
    const [ availableChemicals, setAvailableChemicals ] = useState([])

    const [ recipes, setRecipes ] = useState([])
    const [ availableRecipes, setAvailableRecipes ] = useState([])

    useEffect( () => {
        axios.get(`/api/logged-in/chemical/${selectedLab.key}`).then(result => setChemicals(result.data))
    }, [])

    
    return(
        <div>
            <h3 className="display-8">Chemical ingredients</h3>
            {chemicalIngredients.map((chemicalIngredient, index) => 
                <ChemInput 
                    key={chemicalIngredient.nr}
                    chemicalIngredient={chemicalIngredient} 
                    chemicalIngredients={chemicalIngredients}
                    setChemicalIngredients={setChemicalIngredients} 
                    chemicals={chemicals} 
                    handleOnRemove={handleChemicalOnRemove} 
                    units={units}
                    isLast={chemicalIngredients.length - 1 === index}
                    />

            )}

        </div>
    )
}

export default ChemicalIngredientInputs