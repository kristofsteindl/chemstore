import axios from 'axios'
import React, { useEffect, useState } from 'react'
import Select from 'react-dropdown-select'
import { useSelector } from 'react-redux'
import { useLocation } from 'react-router-dom'
import { check } from '../../utils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import VerifyPanel from '../UI/VerifyPanel'
import RecipeCard from './RecipeCard'

const RecipeDashboard = props => {
    const location = useLocation()

    const [projects, setProjects] = useState([])
    const [recipes, setRecipes] = useState([])
    const [selectedProject, setSelectedProject] = useState("")
    const [justAddedRecipe, setJustAddedRecipe] = useState(location.state ? location.state.detail.justAddedRecipe : "")

    const selectedLab = useSelector((state) => state.selectedLab)
    const user = useSelector((state) => state.security.user)
        
    useEffect(() => {
        if (selectedLab) {
            console.log("hello1")
            check()
            if (justAddedRecipe) {
                setSelectedProject(location.state.detail.selectedProject)
            } else {
                setSelectedProject("")
            }
            axios.get(`/api/logged-in/project/${selectedLab.key}`).then(result => {setProjects(result.data)})
        }
        
    }, [selectedLab])


    useEffect(() => {
        if (selectedProject) {
            check()
            axios.get(`/api/logged-in/recipe/${selectedProject.id}`).then(result => {setRecipes(result.data)})
        }
        
    }, [selectedProject])
    
    const isManager =  (selectedLab.key) && selectedLab.labManagers.filter(manager => manager.username === user.username).length > 0

    const getProjectDashboardContent = () => recipes.map(recipe => <RecipeCard key={recipe.id} recipe={recipe} />)

    const handleProjectDropdownChange = items => {
        const selectedProject = items[0]
        if (selectedProject) {
            setSelectedProject(selectedProject)
        }
    }

    return (
        
        <div className="container">
                {justAddedRecipe && 
                    <VerifyPanel 
                        onCancel={() => setJustAddedRecipe("")} 
                        veryfyMessage={`Recipe ${justAddedRecipe.name} was successfully created 
                            in project ${location.state.detail.selectedProject.name} in lab ${selectedLab.name}`}
                        buttonLabel="Ok"
                    />}
                <div className="row">
                    <div className="col-md-12">
                        <h3 className="display-4 text-center">Recipes</h3>
                        <p className="lead text-center">List {isManager ? " and manage" : ""} the recipes of {selectedLab.name}</p>
                        <br/>
                        {isManager && 
                            <RedirectFormButton 
                                objectToPass={{formRoute:"/add-recipe", selectedProject: selectedProject}} 
                                formRoute="/add-recipe" 
                                buttonLabel="Add Recipe"
                            />
                        }
                        <hr />
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

                        {selectedLab.key && selectedProject? 
                            getProjectDashboardContent() :
                            <p className="lead"><i>Please select a recipe</i></p>
                        }
                    </div>
                </div>
            </div>
    )
}

export default RecipeDashboard
