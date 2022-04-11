import axios from 'axios'
import React, { useEffect, useState } from 'react'
import Select from 'react-dropdown-select'
import { useSelector } from 'react-redux'
import { check } from '../../utils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'

function RecipeDashboard() {

    const [projects, setProjects] = useState("")
    const [recipes, setRecipes] = useState([])
    const [project, setProject] = useState("")

    const selectedLab = useSelector((state) => state.selectedLab)
    const user = useSelector((state) => state.security.user)

    useEffect(() => {
        if (selectedLab) {
            check()
            axios.get(`/api/logged-in/project/${selectedLab.key}`).then(result => {setProjects(result.data)})
        }
        
    }, [selectedLab])
    
    const isManager =  (selectedLab.key) && selectedLab.labManagers.filter(manager => manager.username === user.username).length > 0

    const deleteProject = async id => {
        await axios.delete(`/api/lab-manager/project/${id}`)
        setRecipes(originalList => originalList.filter(project => project.id !== id))
    }

    const getProjectDashboardContent = () => {
        
        
    }

    return (
        <div className="container">
                <div className="row">
                    <div className="col-md-12">
                        <h3 className="display-4 text-center">Recipes</h3>
                        <p className="lead text-center">List {isManager ? " and manage" : ""} the recipes of {project} of {selectedLab.name}</p>
                        <br/>
                        <div className="form-group row mb-3">
                                <label htmlFor="chemical" className="col-sm-4 col-form-label">project</label>
                                <div className="col-sm-8">
                                    <Select
                                        options={projects}
                                        labelField="name"
                                        valueField="name"
                                        placeholder="project"
                                        searchable={false}
                                        clearable={false}
                                        style={{height: "42px", fontSize: "16px"}}
                                        onChange={(items) => items[0] && setProject(items[0])}
                                    />
                                </div>
                            </div>
                        {isManager && 
                            <RedirectFormButton formRoute="/add-project" buttonLabel="Add Project"/>
                        }
                        <hr />
                        {selectedLab.key && project? 
                            getProjectDashboardContent() :
                            <p className="lead"><i>Please select a recipe</i></p>
                        }

                    </div>
                </div>
            </div>
    )
}

export default RecipeDashboard
