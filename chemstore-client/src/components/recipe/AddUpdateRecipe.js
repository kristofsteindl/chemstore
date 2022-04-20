import axios from 'axios'
import { useEffect, useState } from 'react'
import { useSelector } from 'react-redux'
import { useLocation } from 'react-router-dom'
import { useHistory } from 'react-router-dom/cjs/react-router-dom.min'
import { check } from '../../utils/securityUtils'
import IngredientInputs from './IngredientInputs'
import RecipeCoreForm from './RecipeCoreForm'

const AddUpdateRecipe = props => {
    const location = useLocation()
    let history = useHistory()
    
    const selectedLab = useSelector((state) => state.selectedLab)

    const [ firstRender, setFirstRender ] = useState(true)
    const [ selectedProject, setSelectedProject ] = useState(location.state.selectedProject)
    const [units, setUnits] = useState([])
    
    const [ name, setName ] = useState("")
    const [ amount, setAmount ] = useState("")
    const [ unit, setUnit ] = useState("")
    const [ shelfLifeInDays, setShelfLifeInDays ] = useState("")
    const [ ingredientsAreInValid, setIngredientsAreInValid ] = useState(true)

    const [ chemicalIngredients, setChemicalIngredients ] = useState([{nr:0, amount:""}])
    const [ recipeIngredients, setRecipeIngredients ] = useState([{nr:0, amount:""}])
    
    const [ errors, setErrors ] = useState("")

    const handleChemicalOnRemove = nr => {
        setChemicalIngredients(chemicalIngredients.filter(chemicalIngredient => chemicalIngredient.nr !== nr))
    }

    const handleRecipeOnRemove = nr => {
        setRecipeIngredients(recipeIngredients.filter(recipeIngredient => recipeIngredient.nr !== nr))
    }

    const collectIngredientInputs = (ingredients, type) => {
        return ingredients
            .filter(ingredient => ingredient.ingredient && ingredient.ingredient.id && ingredient.unit && ingredient.amount)
            .map(ingredient =>  ({
                type: type,
                ingredientId: ingredient.ingredient.id,
                amount: parseInt(ingredient.amount),
                unit: ingredient.unit
            }))
    }

    const onSubmit = async (event) => {
        check()
        event.preventDefault()
        const newRecipe = {
            projectId: selectedProject.id,
            name: name,
            amount: amount,
            unit: unit.unit,
            shelfLifeInDays: parseInt(shelfLifeInDays),
            ingredients: collectIngredientInputs(chemicalIngredients, "CHEMICAL").concat(collectIngredientInputs(recipeIngredients, "RECIPE"))
        }
        await axios.post('/api/lab-manager/recipe', newRecipe)
            .then(result => history.push(
                {
                    pathname:"/recipes", 
                    state: { 
                        detail: {
                            selectedProject: selectedProject,
                            justAddedRecipe: result.data
                        
                        } 
                    }
                }
            ))
            .catch(error => setErrors(error.response.data))
    }

    useEffect(() => {
        check()
        if (chemicalIngredients.length === 1 && recipeIngredients.length === 1) {
            return setIngredientsAreInValid(true)
        }
        if (!selectedProject || !selectedProject.id || !name || amount < 0.001 || !unit.unit || !shelfLifeInDays || parseInt(shelfLifeInDays) < 0)  {
            return setIngredientsAreInValid(true)
        }
        const invalidChemRows = chemicalIngredients
            .slice(0, chemicalIngredients.length - 1)
            .filter(ingredient => !ingredient.ingredient || !ingredient.ingredient.id || !ingredient.unit || !ingredient.amount)
        if (invalidChemRows.length > 0) {
            setIngredientsAreInValid(true)
            return
        }
        const invalidRecipeRows = recipeIngredients
            .slice(0, recipeIngredients.length - 1)
            .filter(ingredient => !ingredient.ingredient || !ingredient.ingredient.id || !ingredient.unit || !ingredient.amount)
        setIngredientsAreInValid(invalidRecipeRows.length > 0)
    }, [chemicalIngredients, recipeIngredients, selectedProject, name, amount, unit, shelfLifeInDays])

    useEffect(() => {
        if (chemicalIngredients[chemicalIngredients.length - 1].ingredient) {
            setChemicalIngredients(oldList => [...oldList, {nr: chemicalIngredients.length}])
        }

    }, [chemicalIngredients])

    useEffect(() => {
        if (recipeIngredients[recipeIngredients.length - 1].ingredient) {
            setRecipeIngredients(oldList => [...oldList, {nr: recipeIngredients.length}])
        }
    }, [recipeIngredients])

    useEffect(() => {
        if (selectedLab) {
            check()
            axios.get("/api/chem-item/unit").then(result => setUnits(result.data.map(unit => {return {"unit": unit}})))
        } else {
            props.history.push("/recipes")
        }
    }, [])

    useEffect(() => {
        if (!firstRender) {
            props.history.push("/recipes")
        }
        setFirstRender(false)
    }, [selectedLab])
    
    const sumbitButton = <button type="submit" className="btn btn-info mt-4" disabled={ingredientsAreInValid}>Add Recipe</button>

    return(
        <div className="col-md-8 m-auto">
            <h1 className="display-4 text-center">Add Recipe</h1>
            <br/>
            {
                (errors.message && <h5 className="text-danger">{errors.message}</h5>)
            }
            <form onSubmit={onSubmit}>
                {sumbitButton}
                <RecipeCoreForm 
                    units={units}
                    errors={errors}
                    selectedProject={selectedProject} setSelectedProject={setSelectedProject}
                    name={name} setName={setName}
                    amount={amount} setAmount={setAmount}
                    unit={unit} setUnit={setUnit}
                    shelfLifeInDays={shelfLifeInDays} setShelfLifeInDays={setShelfLifeInDays}
                />
                {
                    (errors.ingredients && <div className="text-danger">{errors.ingredients}</div>)
                }
                {selectedProject &&
                    <IngredientInputs 
                        projectId={selectedProject.id}
                        chemicalIngredients={chemicalIngredients} 
                        setChemicalIngredients={setChemicalIngredients}
                        recipeIngredients={recipeIngredients}
                        setRecipeIngredients={setRecipeIngredients}
                        units={units}
                        handleChemicalOnRemove={handleChemicalOnRemove}
                        handleRecipeOnRemove={handleRecipeOnRemove}
                    />
                }
                {sumbitButton}
            </form>
            <div style={{height: "600px", width: "100%", clear:"both"}}></div>
        </div>
    )
}

export default AddUpdateRecipe