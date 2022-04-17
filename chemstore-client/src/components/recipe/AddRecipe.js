
import axios from 'axios'
import classNames from 'classnames'
import { useEffect, useState } from 'react'
import Select from 'react-dropdown-select'
import { useSelector } from 'react-redux'
import { useLocation } from 'react-router-dom'
import { check } from '../../utils/securityUtils'
import IngredientInputs from './IngredientInputs'

const AddRecipe = props => {
    const location = useLocation()
    const { state } = location
    
    const selectedLab = useSelector((state) => state.selectedLab)

    const [ firstRender, setFirstRender ] = useState(true)
    const [ selectedProject, setSelectedProject ] = useState(state.selectedProject)
    const [projects, setProjects] = useState([])
    const [units, setUnits] = useState([])
    
    const [ name, setName ] = useState("")
    const [ amount, setAmount ] = useState("")
    const [ unit, setUnit ] = useState("")
    const [ chemicalIngredients, setChemicalIngredients ] = useState([{nr:0}, {nr:1}])
    const [ recipeIngredients, setRecipeIngredients ] = useState([{nr:0}, {nr:1}])
    const [ selectedRecipes, setSelectedRecipes ] = useState([])
    
    const [ errors, setErrors ] = useState("")

    const handleProjectDropdownChange = items => {
        const selectedProject = items[0]
        if (selectedProject) {
            setSelectedProject(selectedProject)
        }
    }

    const handleChemicalOnRemove = nr => {
        setChemicalIngredients(chemicalIngredients.filter(chemicalIngredient => chemicalIngredient.nr != nr))
    }

    const handleRecipeOnRemove = nr => {
        setRecipeIngredients(recipeIngredients.filter(recipeIngredient => recipeIngredient.nr != nr))
    }

    const onSubmit = () => {
    }

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
        axios.get("/api/chem-item/unit")
                .then(result => setUnits(result.data.map(unit => {return {"unit": unit}})))
                .catch(error =>  this.setState({ errors: {...this.state.errors, unitsErrorStatus: error.response.status}}))
        if (selectedLab) {
            check()
            axios.get(`/api/logged-in/project/${selectedLab.key}`).then(result => {setProjects(result.data)})
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
    

    return(
        <div className="col-md-8 m-auto">
            <h1 className="display-4 text-center">Add Recipe</h1>
            <br/>
            {
                (errors.message && <h5 className="invalid-input">{errors.message}</h5>)
            }
            <form onSubmit={onSubmit}>
                <div className="form-group row mb-3">
                    <label htmlFor="chemical" className="col-sm-2 col-form-label">project</label>
                    <div className="col-sm-10">
                        <Select
                            options={projects}
                            values={projects.filter(project => selectedProject && (project.id === selectedProject.id))}
                            labelField="name"
                            valueField="name"
                            placeholder="project"
                            searchable={false}
                            clearable={false}
                            style={{height: "42px", fontSize: "16px"}}
                            onChange={handleProjectDropdownChange}
                        />
                    </div>
                </div>
                <div className="form-group row mb-3">
                    <label htmlFor="name" className="col-sm-2 col-form-label">recipe name</label>
                    <div className="col-sm-10">
                        <input 
                            name="name"
                            value={name}
                            onChange={event => setName(event.target.value)}
                            type="text" 
                            className={classNames("form-control form-control-lg", {"is-invalid": errors.name})} 
                            placeholder="name" 
                        />
                        {
                            (errors.name && <div className="invalid-feedback">{errors.name}</div>)
                        }
                        
                    </div>
                </div>
                <div className="form-group row mb-3">
                    <label htmlFor="amount" className="col-sm-2 col-form-label">amount</label>
                    <div className="col-sm-10">
                        <input 
                            name="amount"
                            value={amount}
                            onChange={event => setAmount(event.target.value)}
                            type="number" 
                            className={classNames("form-control form-control-lg", {"is-invalid": errors.amount})} 
                            placeholder="amount" 
                        />
                        {
                            (errors.amount && <div className="invalid-feedback">{errors.amount}</div>)
                        }
                    </div>
                </div>
                <div className="form-group row mb-3">
                    <label htmlFor="unit" className="col-sm-2 col-form-label">unit</label>
                    <div className="col-sm-10">
                        <Select
                            options={units}
                            labelField="unit"
                            placeholder="unit"
                            valueField="unit"
                            searchable={false}
                            clearable={false}
                            style={{height: "42px", fontSize: "16px"}}
                            onChange={items => items[0] && setUnit({unit: items[0].unit })}
                        />
                        {
                            (errors.unit && <div className="invalid-feedback">{errors.unit}</div>)
                        }
                    </div>
                </div>
                
            </form>
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
        </div>
    )
}

export default AddRecipe