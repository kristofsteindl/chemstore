import { useState } from "react"
import DuButtons from "../UI/DuButtons"
import VerifyPanel from "../UI/VerifyPanel"

const ProjectCard = props => {
    const project = props.project

    const [activeModal, setActiveModal] = useState(false)

    return (
        <div className="card card-body bg-light mb-3" style={{padding: "10px"}}>
            <div className="row" >
                <div className="col-sm-9">
                    <h4>{project.name}</h4>
                </div>
                
                <div className="col-sm-3">
                    { props.isManager && 
                        <DuButtons 
                            updateFormTo={`/update-project/${project.id}`}
                            onDelete={() => setActiveModal(true)}
                        /> 
                        
                    }
                </div>
            </div>
            {activeModal && 
                <VerifyPanel 
                    onCancel={() => setActiveModal(false)} 
                    veryfyMessage={`Are you sure you want to delete project ${project.name}?`}
                    onSubmit={() => props.deleteProject(project.id)}
                    buttonLabel="Delete"
                />}
        </div>
    )
    
}

export default ProjectCard

    
