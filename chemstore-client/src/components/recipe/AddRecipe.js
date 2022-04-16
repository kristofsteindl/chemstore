
import axios from 'axios'
import classNames from 'classnames'
import { useEffect, useState } from 'react'
import Select from 'react-dropdown-select'
import { useSelector } from 'react-redux'
import { useLocation } from 'react-router-dom'
import { check } from '../../utils/securityUtils'

const AddRecipe = props => {
    const location = useLocation()
    const { state } = location
    
    const selectedLab = useSelector((state) => state.selectedLab)

    const [ firstRender, setFirstRender ] = useState(true)
    const [ selectedProject, setSelectedProject ] = useState(state.selectedProject)
    const [projects, setProjects] = useState([])
    const [ name, setName ] = useState("")
    const [ errors, setErrors ] = useState("")

    const handleProjectDropdownChange = items => {
        const selectedProject = items[0]
        if (selectedProject) {
            setSelectedProject(selectedProject)
        }
    }

    const onSubmit = () => {
    }


    useEffect(() => {
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
        <div className="container">
            <div className="col-md-8 m-auto">
                {state.selectedProject.name}
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
                                className={classNames("form-control form-control-lg", {"is-invalid": errors.shortName})} 
                                placeholder="name" 
                            />
                            {
                                (errors.name && <div className="invalid-feedback">{errors.name}</div>)
                            }
                            
                        </div>
                    </div>
                </form>
            </div>
            
        </div>
    )
}

export default AddRecipe