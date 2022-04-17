import axios from "axios"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import IngredientInputRow from "./IngredientInputRow"

const IngredientInputs = props => {
    
    const selectedLab = useSelector(state => state.selectedLab)

    const { 
        projectId,
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
        axios.get(`/api/logged-in/recipe/${projectId}`).then(result => setRecipes(result.data))
    }, [])

    	
    useEffect(() => {
        const usedChemicalIds = chemicalIngredients.map(ingredient => ingredient.ingredient && ingredient.ingredient.id)
        setAvailableChemicals(chemicals.filter(chemical => !usedChemicalIds.includes(chemical.id)))
    }, [chemicalIngredients, chemicals])

    
    return(
        <div>
            <h3 className="display-8">Chemical ingredients</h3>
            {chemicalIngredients.map((chemicalIngredient, index) => 
                <IngredientInputRow 
                    key={chemicalIngredient.nr}
                    label={"shortName"} 
                    ingredient={chemicalIngredient} 
                    ingredients={chemicalIngredients}
                    setIngredients={setChemicalIngredients} 
                    entities={availableChemicals} 
                    handleOnRemove={handleChemicalOnRemove} 
                    units={units}
                    isLast={chemicalIngredients.length - 1 === index}
                    />

            )}
            <h3 className="display-8">Recipe ingredients</h3>
            {recipeIngredients.map((recipeIngredient, index) => 
                <IngredientInputRow 
                    key={recipeIngredient.nr}
                    label={"name"} 
                    ingredient={recipeIngredient} 
                    ingredients={recipeIngredients}
                    setIngredients={setRecipeIngredients} 
                    entities={recipes} 
                    handleOnRemove={handleRecipeOnRemove} 
                    units={units}
                    isLast={recipeIngredients.length - 1 === index}
                    />

            )}
            <div style={{height: "600px", width: "100%", clear:"both"}}></div>
        </div>
    )
}

export default IngredientInputs