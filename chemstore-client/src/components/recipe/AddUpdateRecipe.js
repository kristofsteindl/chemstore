import axios from 'axios'
import { useEffect, useRef, useState } from 'react'
import { useSelector } from 'react-redux'
import { useLocation } from 'react-router-dom'
import { useHistory } from 'react-router-dom/cjs/react-router-dom.min'
import { check } from '../../utils/securityUtils'
import IngredientInputs from './IngredientInputs'
import RecipeCoreForm from './RecipeCoreForm'

const AddUpdateRecipe = props => {
    const location = useLocation()
    let history = useHistory()
    
    const auLabel = props.match.params.id ? "Update" : "Add"

    const selectedLab = useSelector((state) => state.selectedLab)

    const [ selectedProject, setSelectedProject ] = useState("")
    const [units, setUnits] = useState([])

    const [originalRecipe, setOriginalRecipe] = useState("")
    
    const [ name, setName ] = useState("")
    const [ amount, setAmount ] = useState("")
    const [ unit, setUnit ] = useState("")
    const [ shelfLifeInDays, setShelfLifeInDays ] = useState("")
    const [ ingredientsAreInValid, setIngredientsAreInValid ] = useState(true)

    const [ chemicalIngredients, setChemicalIngredients ] = useState([{nr:0, amount:""}])
    const [ recipeIngredients, setRecipeIngredients ] = useState([{nr:0, amount:""}])
    
    const [ errors, setErrors ] = useState("")

    const prevSelectedLab = useRef();

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


    const setStateForCreateOrUpdate = updadeId => {
        if (updadeId) {
            axios.get(`/api/lab-manager/recipe/${updadeId}`)
                .then(result => setOriginalRecipe(result.data))
                .catch(() => history.push("/recipes"))
        } else {
            setSelectedProject(location.state.selectedProject)
        }
    }

    useEffect(() => {
        if (originalRecipe) {
            setName(originalRecipe.name)
            setAmount(originalRecipe.amount)
            setUnit({unit: originalRecipe.unit})
            setShelfLifeInDays(originalRecipe.shelfLifeInDays)
            setChemicalIngredients(originalRecipe.chemicalIngredients.map((ing, index) => ({nr: index, ingredient: ing.ingredient, amount: ing.amount, unit: ing.unit})))
            setRecipeIngredients(originalRecipe.recipeIngredients.map((ing, index) => ({nr: index, ingredient: ing.ingredient, amount: ing.amount, unit: ing.unit})))    
            setSelectedProject(originalRecipe.project)
        }
    }, [originalRecipe])

    useEffect(() => {
        check()
        setStateForCreateOrUpdate(props.match.params.id)
        axios.get("/api/chem-item/unit").then(result => setUnits(result.data.map(unit => {return {"unit": unit}})))
    }, [])

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
        if (chemicalIngredients.length === 0 || chemicalIngredients[chemicalIngredients.length - 1].ingredient) {
            setChemicalIngredients(oldList => [...oldList, {nr: chemicalIngredients.length}])
        }

    }, [chemicalIngredients])

    useEffect(() => {
        if (recipeIngredients.length === 0 || recipeIngredients[recipeIngredients.length - 1].ingredient) {
            setRecipeIngredients(oldList => [...oldList, {nr: recipeIngredients.length}])
        }
    }, [recipeIngredients])

    useEffect(() => {
        if (!selectedLab || (prevSelectedLab.current && prevSelectedLab.current.id !== selectedLab.id)) {
            props.history.push("/recipes")
        }
        prevSelectedLab.current = selectedLab
    }, [selectedLab])

    const getBacktrackObject = justAddedRecipe =>  
    ({
        pathname:"/recipes", 
        state: {detail: {selectedProject: selectedProject, justAddedRecipe: justAddedRecipe, isUpdate: originalRecipe}}
    })

    const sendRequest = async (recipeInput, originalRecipe) => {
        if (originalRecipe) {
            return axios.put(`/api/lab-manager/recipe/${originalRecipe.id}`, recipeInput)
        } else {
            return axios.post('/api/lab-manager/recipe', recipeInput)
        }
    }

    const sendAndHandleRequest = async (recipeInput, originalRecipe) => {
        await sendRequest(recipeInput, originalRecipe)
            .then(result => history.push(getBacktrackObject(result.data)))
            .catch(error => setErrors(error.response.data))
    }

    const onSubmit = async (event) => {
        check()
        event.preventDefault()
        const recipeInput = {
            projectId: selectedProject.id,
            name: name,
            amount: amount,
            unit: unit.unit,
            shelfLifeInDays: parseInt(shelfLifeInDays),
            ingredients: collectIngredientInputs(chemicalIngredients, "CHEMICAL").concat(collectIngredientInputs(recipeIngredients, "RECIPE"))
        }
        await sendAndHandleRequest(recipeInput, originalRecipe)
    }

    const handleChemicalOnRemove = nr => {
        setChemicalIngredients(chemicalIngredients.filter(chemicalIngredient => chemicalIngredient.nr !== nr))
    }

    const handleRecipeOnRemove = nr => {
        setRecipeIngredients(recipeIngredients.filter(recipeIngredient => recipeIngredient.nr !== nr))
    }
    
    const sumbitButton = <button type="submit" className="btn btn-info mt-4" disabled={ingredientsAreInValid}>{auLabel} Recipe</button>

    return(
        <div className="col-md-8 m-auto">
            <h1 className="display-4 text-center">{auLabel} Recipe</h1>
            <br/>
            {
                (errors.message && <h5 className="text-danger">{errors.message}</h5>)
            }
            <form onSubmit={onSubmit}>
                {sumbitButton}
                <RecipeCoreForm 
                    isUpdate={originalRecipe}
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