import axios from "axios"
import classNames from "classnames"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { check, checkIfManager } from "../../utils/securityUtils"

const UpdateProject = props => {
    const [name, setName] = useState("")
    const [error, setError] = useState("")
    const [project, setProject] = useState("")
    
    
    const selectedLab = useSelector((state) => state.selectedLab)
    const user = useSelector((state) => state.security.user)
    const id = props.match.params.id

    useEffect(() => {
        if (selectedLab) {
            check()
            if (!checkIfManager(selectedLab, user)) {
                props.history.push("/projects")
            }
        }
        
    }, [selectedLab, user, props.history])

    useEffect( () => {
        axios.get(`/api/lab-manager/project/${id}`).then(result => {
            setProject(result.data)
            setName(result.data.name)
        })
    }, [])


    const onSubmit = async (event) => {
        event.preventDefault()
        const input = {
            name: name,
            labKey: selectedLab.key
        }
        try {
            await axios.put(`/api/lab-manager/project/${id}`, input)
            props.history.push("/projects")
        } catch(error) {
            setError(error.response.data)
        }
    }

    return (
        <div className="container">
            <div className="row">
                <div className="col-md-8 m-auto">
                    <h1 className="display-4 text-center">Update Project</h1>
                    <p className="lead text-center">Update {project.name}</p>
                    <br/>
                    {
                        (error.message && <h5 className="invalid-input">{error.message}</h5>)
                    }
                    <form onSubmit={onSubmit}>
                        <div className="form-group row mb-3">
                            <label htmlFor="username" className="col-sm-4 col-form-label">name</label>
                            <div className="col-sm-8">
                                <input 
                                    name="name"
                                    value={name}
                                    onChange={event => setName(event.target.value)}
                                    type="text" 
                                    className={classNames("form-control form-control-lg", {"is-invalid": error.name})} 
                                    placeholder="project name" 
                                />
                                {
                                    (error.name && <div className="invalid-feedback">{error.name}</div>)
                                }
                                
                            </div>
                        </div>
                        <button type="submit" className="btn btn-info btn-block mt-4">Update Project</button>
                        
                    </form>
                </div>
            </div>
        </div>
    )

}

export default UpdateProject