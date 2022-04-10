import axios from 'axios'
import React, { useEffect, useState } from 'react'
import { useSelector } from 'react-redux'
import { check } from '../../utils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import ProjectCard from './ProjectCard'

function ProjectDashboard() {

    const [projects, setProjects] = useState([])

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
        setProjects(originalList => originalList.filter(project => project.id !== id))
    }

    const getProjectDashboardContent = () => {
        if (!projects) {
            return <p className="lead"><i>There is no registered project for this lab so far</i></p>
        }
        return (
        <div>
            {projects.map(project => (
                <ProjectCard 
                    key={project.id}
                    project={project}
                    isManager={isManager}
                    deleteProject={deleteProject}
                />
                ))
            }
        </div>)
        
    }

    return (
        <div className="container">
                <div className="row">
                    <div className="col-md-12">
                        <h3 className="display-4 text-center">Projects</h3>
                        <p className="lead text-center">List {isManager ? " and manage" : ""} the projects for {selectedLab.name}</p>
                        <br/>
                        {isManager && 
                            <RedirectFormButton formRoute="/add-project" buttonLabel="Add Project"/>
                        }
                        <hr />
                        {selectedLab.key ? 
                            getProjectDashboardContent() :
                            <p className="lead"><i>Please select a lab</i></p>
                        }

                    </div>
                </div>
            </div>
    )
}

export default ProjectDashboard
