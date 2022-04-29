import axios from "axios"
import classNames from "classnames"
import { useEffect, useRef, useState } from "react"
import Select from "react-dropdown-select"
import { useSelector } from "react-redux"
import { check } from "../../../utils/securityUtils"
import VerifyPanel from "../../UI/VerifyPanel"
import ItemInputsInMixture from "./ItemInputsInMixture"

const AddUpdateMixture = props => {
    const mixtureId = props.match.params.id
    
    const selectedLab = useSelector(state => state.selectedLab)
    const user = useSelector(state => state.security.user)

    const [ projects, setProjects ] = useState([])
    const [ recipes, setRecipes ] = useState([])
    const [ originalMixture, setOriginalMixture ] = useState("")

    const [ selectedProject, setSelectedProject ] = useState("")
    const [ recipe, setRecipe ] = useState("")
    const [ amount, setAmount ] = useState("")
    const [ chemItems, setChemItems ] = useState("")
    const [ mixtureItems, setMixtureItems ] = useState("")

    const [ errors, setErrors ] = useState("")

    const prevSelectedLab = useRef();

    const [justAddedMixture, setJustAddedMixture] = useState("")

    useEffect(() => {
        if (mixtureId) {
            axios.get(`/api/mixture/${selectedLab.key}/${mixtureId}`)
                .then(result => {setOriginalMixture(result.data)})
                .catch(() => props.history.push("/mixtures"))
        }
    }, [])

    useEffect(() => {
        if (originalMixture) {
            setSelectedProject(originalMixture.recipe.project)
            setRecipe(originalMixture.recipe)
            setAmount(originalMixture.amount)
            const initialChemItems = {}
            originalMixture.chemItems.forEach(item => {
                initialChemItems[item.chemical.id] = item
            });
            setChemItems(initialChemItems)
            const initialMixtureItems = {}
            originalMixture.mixtureItems.forEach(item => {
                initialMixtureItems[item.recipe.id] = item
            });
            setMixtureItems(initialMixtureItems)
        }

    }, [originalMixture])

    useEffect(() => {
        check()
        if (prevSelectedLab.current && prevSelectedLab.current.id !== selectedLab.id) {
            props.history.push("/mixtures")
        }
        if (selectedLab) {
            axios.get(`/api/logged-in/project/${selectedLab.key}`).then(result => {setProjects(result.data)})
        }
        prevSelectedLab.current = selectedLab
    }, [selectedLab])

    useEffect(() => {
        if (selectedProject) {
            axios.get(`/api/logged-in/recipe/${selectedProject.id}`).then(result => {setRecipes(result.data)})
        } else {
            setRecipes([])
        }
    }, [selectedProject])

    const sendRequest = async (mixtureInput) => {
        if (originalMixture) {
            return axios.put(`/api/mixture/${originalMixture.id}`, mixtureInput)
        } else {
            return axios.post(`/api/mixture`, mixtureInput)
        }
    }

    const onSubmit = async (event) => {
        check()
        event.preventDefault()
        const mixtureInput = {
            recipeId: recipe.id,
            creationDate: (new Date()).toISOString().split('T')[0],
            username: user.username,
            amount: amount,
            chemItemIds: Object.values(chemItems).map(chemItem => chemItem.id),
            mixtureItemIds: Object.values(mixtureItems).map(mixtureItem => mixtureItem.id)
        }
        await sendRequest(mixtureInput)
                .then(result => setJustAddedMixture(result.data))
                .catch(error => setErrors(error.response.data))
       
    }

    const createVerifyMessage = () => {
        return `Mixture ${justAddedMixture.recipe.name} was successfully ${originalMixture ? "updated" : "created"} in project ${selectedProject.name} in lab ${selectedLab.name}`
    }
    
    const auLabel = mixtureId ? "Update" : "Add"
    const sumbitButton = <button type="submit" className="btn btn-info mt-4" disabled={false}>{auLabel} Mixture</button>
    
    return (
        <div className="col-md-8 m-auto">
            <h1 className="display-4 text-center">{auLabel} Mixture</h1>
            <br/>
            {
                (errors.message && <h5 className="text-danger">{errors.message}</h5>)
            }
            <form onSubmit={onSubmit}>
                {sumbitButton}
                <div className="form-group row mb-3">
                    <label htmlFor="project" className="col-sm-2 col-form-label">project</label>
                        <div className="col-sm-10">
                            <Select
                                disabled={mixtureId}
                                options={projects}
                                values={projects.filter(project => selectedProject && (project.id === selectedProject.id))}
                                labelField="name"
                                valueField="name"
                                placeholder="project"
                                searchable={false}
                                clearable={false}
                                style={{height: "42px", fontSize: "16px"}}
                                onChange={items => setSelectedProject(items[0] ? items[0] : "")}
                            />
                        </div>
                    </div>
                    <div className="form-group row mb-3">
                        <label htmlFor="project" className="col-sm-2 col-form-label">recipe</label>
                        <div className="col-sm-10">
                            <Select
                                options={recipes}
                                values={recipes.filter(recipeFromList => recipe && (recipeFromList.id === recipe.id))}
                                labelField="name"
                                valueField="id"
                                placeholder="recipe"
                                searchable={false}
                                clearable={false}
                                style={{height: "42px", fontSize: "16px"}}
                                onChange={items => setRecipe(items[0] ? items[0] : "")}
                            />
                        </div>
                    </div>
                    <div className="form-group row mb-3">
                        <label htmlFor="amount" className="col-sm-2 col-form-label">amount</label>
                        <div className="col-sm-10">
                            <input 
                                name="amount"
                                value={amount}
                                onChange={event => setAmount(parseFloat(event.target.value))}
                                type="number" 
                                className={classNames("form-control form-control-lg", {"is-invalid": errors.amount})} 
                                placeholder="amount" 
                                min="0.000"
                                step="0.001"
                            />
                            {
                                (errors.amount && <div className="text-danger">{errors.amount}</div>)
                            } 
                        </div>
                    </div>
                    <ItemInputsInMixture
                        type="CHEM_ITEM" 
                        recipe={recipe}
                        ingredients={recipe.chemicalIngredients}
                        amount={amount}
                        setSelectedItems={setChemItems}
                        selectedItems={chemItems}
                    />
                    <ItemInputsInMixture
                        type="MIXTURE_ITEM" 
                        recipe={recipe}
                        ingredients={recipe.recipeIngredients}
                        amount={amount}
                        setSelectedItems={setMixtureItems}
                        selectedItems={mixtureItems}
                    />
            </form>
            <div style={{height: "600px", width: "100%", clear:"both"}}></div>
            {justAddedMixture && 
                <VerifyPanel 
                    onCancel={() => props.history.push('/mixtures')} 
                    veryfyMessage={createVerifyMessage()}
                    buttonLabel="Ok"
                />}
        </div>
    )
}

export default AddUpdateMixture